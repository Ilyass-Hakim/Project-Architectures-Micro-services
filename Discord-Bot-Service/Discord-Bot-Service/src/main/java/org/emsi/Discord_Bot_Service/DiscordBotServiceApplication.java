package org.emsi.Discord_Bot_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Client Discord du chatbot EBank.
 * Micro-service séparé de l'agent : il reçoit les messages Discord et les envoie
 * à l'agent IA en REST (GET /chat via la Gateway), avec une mémoire par utilisateur.
 * (Séparé aussi pour une raison technique : JDA utilise OkHttp 5, la bibliothèque
 * Telegram de l'agent utilise OkHttp 4.)
 */
@SpringBootApplication
public class DiscordBotServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscordBotServiceApplication.class, args);
	}

}
