package org.emsi.AI_Agent_Service.telegram;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.emsi.AI_Agent_Service.agents.EbankAiAgent;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Tests du bot Telegram sans Telegram ni OpenAI : l'agent et le client Telegram sont simulés.
 */
class TelegramBotTests {

    private final EbankAiAgent agent = mock(EbankAiAgent.class);
    private final TelegramBot bot = new TelegramBot(agent, "test-token", mock(TelegramClient.class));

    @Test
    void startCommandAnswersWithWelcomeMessage() {
        assertThat(bot.reply("42", "/start")).isEqualTo(TelegramBot.WELCOME);
        verifyNoInteractions(agent);
    }

    @Test
    void messageIsSentToAgentWithOneConversationPerChat() {
        when(agent.chat("telegram-42", "Quels sont les comptes de Imane ?")).thenReturn("Imane a 2 comptes.");

        assertThat(bot.reply("42", "Quels sont les comptes de Imane ?")).isEqualTo("Imane a 2 comptes.");
    }

    @Test
    void agentErrorGivesAFriendlyMessage() {
        when(agent.chat(anyString(), anyString())).thenThrow(new RuntimeException("OpenAI unavailable"));

        assertThat(bot.reply("42", "Bonjour")).isEqualTo(TelegramBot.ERROR);
    }

    @Test
    void longAnswersAreSplitIntoTelegramMessages() {
        List<String> parts = TelegramBot.split("a".repeat(9000));

        assertThat(parts).hasSize(3);
        assertThat(parts.get(0)).hasSize(TelegramBot.MAX_MESSAGE_LENGTH);
        assertThat(String.join("", parts)).hasSize(9000);
    }
}
