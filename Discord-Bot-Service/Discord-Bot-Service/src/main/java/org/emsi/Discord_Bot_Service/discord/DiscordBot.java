package org.emsi.Discord_Bot_Service.discord;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.emsi.Discord_Bot_Service.agent.AiAgentClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * Bot Discord du chatbot EBank.
 * Il répond aux messages privés, et dans les salons quand on le mentionne (@bot question).
 * Chaque utilisateur Discord a sa propre mémoire : conversationId = "discord-" + userId.
 *
 * Le bot ne démarre que si la variable d'environnement DISCORD_BOT_TOKEN est définie,
 * lue par Spring comme la propriété discord.bot.token.
 */
@Component
@ConditionalOnProperty(prefix = "discord.bot", name = "token")
public class DiscordBot extends ListenerAdapter implements DisposableBean {

    static final String HELP = """
            Bonjour ! Je suis l'assistant de la banque EBank.
            Écris-moi en message privé, ou mentionne-moi dans un salon, par exemple :
            @EBank Quels sont les comptes de Imane ?""";

    static final String ERROR = "Désolé, je n'ai pas pu traiter ta demande. Réessaie dans un instant.";

    private static final Pattern MENTION = Pattern.compile("<@!?\\d+>");
    private static final Logger log = LoggerFactory.getLogger(DiscordBot.class);

    private final AiAgentClient agentClient;
    private final String token;
    // l'agent peut répondre lentement : on ne bloque pas les threads de JDA
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private JDA jda;

    public DiscordBot(AiAgentClient agentClient, @Value("${discord.bot.token}") String token) {
        this.agentClient = agentClient;
        this.token = token;
    }

    /** Connecte le bot à Discord une fois l'application prête. */
    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        try {
            // Pas besoin de l'intent privilégié MESSAGE_CONTENT : Discord donne le contenu
            // des messages privés et des messages qui mentionnent le bot.
            jda = JDABuilder.createLight(token, EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES))
                    .addEventListeners(this)
                    .build();
            log.info("Discord bot started");
        }
        catch (RuntimeException e) {
            // un token invalide ne doit pas arrêter le service
            log.error("Discord bot could not start, check DISCORD_BOT_TOKEN", e);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return; // jamais de réponse aux bots (ni à lui-même)
        }
        boolean privateMessage = !event.isFromGuild();
        boolean mentioned = event.getMessage().getMentions()
                .isMentioned(event.getJDA().getSelfUser(), Message.MentionType.USER);
        if (!privateMessage && !mentioned) {
            return;
        }
        String userId = event.getAuthor().getId();
        String question = cleanMentions(event.getMessage().getContentRaw());
        event.getChannel().sendTyping().queue();
        executor.submit(() -> {
            for (String part : split(reply(userId, question))) {
                event.getMessage().reply(part).queue();
            }
        });
    }

    /** Réponse du bot à une question. */
    String reply(String userId, String question) {
        if (question.isBlank()) {
            return HELP;
        }
        try {
            return agentClient.chat("discord-" + userId, question);
        }
        catch (RuntimeException e) {
            log.error("Agent error for Discord user {}", userId, e);
            return ERROR;
        }
    }

    /** Retire les mentions (<@123...>) du message. */
    static String cleanMentions(String content) {
        return MENTION.matcher(content).replaceAll("").trim();
    }

    /** Découpe une réponse trop longue en plusieurs messages Discord (2000 caractères max). */
    static List<String> split(String text) {
        if (text == null || text.isBlank()) {
            return List.of(ERROR);
        }
        List<String> parts = new ArrayList<>();
        for (int start = 0; start < text.length(); start += Message.MAX_CONTENT_LENGTH) {
            parts.add(text.substring(start, Math.min(text.length(), start + Message.MAX_CONTENT_LENGTH)));
        }
        return parts;
    }

    @Override
    public void destroy() {
        executor.shutdown();
        if (jda != null) {
            jda.shutdown();
        }
    }
}
