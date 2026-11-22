package org.emsi.Discovery_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Discovery Service (Eureka) : l'annuaire des micro-services.
 * Chaque service s'enregistre ici au démarrage avec son nom (customer-service, ebank-service...).
 * La Gateway et OpenFeign le consultent pour trouver l'adresse d'un service à partir de son nom.
 * Tableau de bord : http://localhost:8761
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscoveryServiceApplication.class, args);
	}

}
