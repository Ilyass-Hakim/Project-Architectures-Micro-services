package org.emsi.AI_Agent_Service.telegram;

import java.util.ArrayList;
import java.util.List;

import org.emsi.AI_Agent_Service.agents.EbankAiAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Client Telegram du chatbot (long polling).
 * Chaque message reçu est envoyé à l'agent IA, qui utilise les outils MCP des micro-services.
 * Chaque chat Telegram a sa propre mémoire : conversationId = "telegram-" + chatId.
 *
 * Le bot ne démarre que si la variable d'environnement TELEGRAM_BOT_TOKEN est définie
 * (token donné par @BotFather), lue par Spring comme la propriété telegram.bot.token.
 */
@Component
@ConditionalOnProperty(prefix = "telegram.bot", name = "token")
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer, DisposableBean {

    /** Taille maximale d'un message Telegram. */
    static final int MAX_MESSAGE_LENGTH = 4096;

    static final String WELCOME = """
            Bonjour ! Je suis l'assistant de la banque EBank.
            Pose-moi tes questions sur les clients et les comptes, par exemple :
            - Quels sont les clients de la banque ?
            - Quels sont les comptes de Imane ?
            - Ouvre un compte courant de 1000 MAD pour Yassine""";

    static final String ERROR = "Désolé, je n'ai pas pu traiter ta demande. Réessaie dans un instant.";

    private static final Logger log = LoggerFactory.getLogger(TelegramBot.class);

    private final EbankAiAgent agent;
    private final String token;
    private final TelegramClient telegramClient;
    private TelegramBotsLongPollingApplication botsApplication;

    @Autowired
    public TelegramBot(EbankAiAgent agent, @Value("${telegram.bot.token}") String token) {
        this(agent, token, new OkHttpTelegramClient(token));
    }

    // utilisé par les tests pour remplacer le client Telegram par un mock
    TelegramBot(EbankAiAgent agent, String token, TelegramClient telegramClient) {
        this.agent = agent;
        this.token = token;
        this.telegramClient = telegramClient;
    }

    /** Démarre le long polling une fois l'application prête. */
    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        try {
            botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(token, this);
            log.info("Telegram bot started (long polling)");
        }
        catch (TelegramApiException e) {
            // un token invalide ne doit pas empêcher le reste de l'agent (REST /chat) de fonctionner
            log.error("Telegram bot could not start, check TELEGRAM_BOT_TOKEN", e);
        }
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return; // on ignore les photos, stickers, etc.
        }
        String chatId = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText();
        sendTyping(chatId);
        for (String part : split(reply(chatId, text))) {
            send(chatId, part);
        }
    }

    /** Réponse du bot à un message texte. */
    String reply(String chatId, String text) {
        if (text.startsWith("/start")) {
            return WELCOME;
        }
        try {
            return agent.chat("telegram-" + chatId, text);
        }
        catch (RuntimeException e) {
            log.error("Agent error for Telegram chat {}", chatId, e);
            return ERROR;
        }
    }

    /** Découpe une réponse trop longue en plusieurs messages Telegram. */
    static List<String> split(String text) {
        if (text == null || text.isBlank()) {
            return List.of(ERROR);
        }
        List<String> parts = new ArrayList<>();
        for (int start = 0; start < text.length(); start += MAX_MESSAGE_LENGTH) {
            parts.add(text.substring(start, Math.min(text.length(), start + MAX_MESSAGE_LENGTH)));
        }
        return parts;
    }

    private void sendTyping(String chatId) {
        try {
            SendChatAction typing = SendChatAction.builder().chatId(chatId).action("typing").build();
            telegramClient.execute(typing);
        }
        catch (TelegramApiException e) {
            log.warn("Could not send typing action to Telegram chat {}", chatId, e);
        }
    }

    private void send(String chatId, String text) {
        try {
            SendMessage message = SendMessage.builder().chatId(chatId).text(text).build();
            telegramClient.execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Could not send message to Telegram chat {}", chatId, e);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (botsApplication != null) {
            botsApplication.close();
        }
    }
}
