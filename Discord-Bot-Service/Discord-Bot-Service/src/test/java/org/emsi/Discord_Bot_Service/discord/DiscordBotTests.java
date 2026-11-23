package org.emsi.Discord_Bot_Service.discord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.emsi.Discord_Bot_Service.agent.AiAgentClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests du bot Discord sans Discord ni agent : le client de l'agent est simulé.
 */
class DiscordBotTests {

    private final AiAgentClient agentClient = mock(AiAgentClient.class);
    private final DiscordBot bot = new DiscordBot(agentClient, "test-token");

    @AfterEach
    void stopBot() {
        bot.destroy();
    }

    @Test
    void mentionsAreRemovedFromTheQuestion() {
        assertThat(DiscordBot.cleanMentions("<@123456789> Quels sont les comptes de Imane ?"))
                .isEqualTo("Quels sont les comptes de Imane ?");
        assertThat(DiscordBot.cleanMentions("<@!987> Bonjour")).isEqualTo("Bonjour");
    }

    @Test
    void questionIsSentToAgentWithOneConversationPerUser() {
        when(agentClient.chat("discord-42", "Quels sont les comptes de Imane ?")).thenReturn("Imane a 2 comptes.");

        assertThat(bot.reply("42", "Quels sont les comptes de Imane ?")).isEqualTo("Imane a 2 comptes.");
    }

    @Test
    void emptyQuestionShowsHelp() {
        assertThat(bot.reply("42", "")).isEqualTo(DiscordBot.HELP);
        verifyNoInteractions(agentClient);
    }

    @Test
    void agentErrorGivesAFriendlyMessage() {
        when(agentClient.chat(anyString(), anyString())).thenThrow(new RuntimeException("Gateway down"));

        assertThat(bot.reply("42", "Bonjour")).isEqualTo(DiscordBot.ERROR);
    }

    @Test
    void longAnswersAreSplitIntoDiscordMessages() {
        List<String> parts = DiscordBot.split("a".repeat(4500));

        assertThat(parts).hasSize(3);
        assertThat(parts.get(0)).hasSize(2000);
        assertThat(String.join("", parts)).hasSize(4500);
    }
}
