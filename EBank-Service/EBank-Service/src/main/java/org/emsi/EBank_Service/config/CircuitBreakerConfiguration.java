package org.emsi.EBank_Service.config;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

/**
 * Réglages du disjoncteur appliqués à tous les clients Feign.
 *
 *  CLOSED    : tout va bien, les appels passent.
 *  OPEN      : trop d'échecs -> on n'appelle même plus le service, fallback direct.
 *  HALF_OPEN : après une pause, on laisse passer quelques appels "test".
 */
@Configuration
public class CircuitBreakerConfiguration {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCircuitBreakerCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)                           // on juge sur les 10 derniers appels
                        .minimumNumberOfCalls(5)                         // pas de décision avant 5 appels
                        .failureRateThreshold(50)                        // >= 50 % d'échecs -> OPEN
                        .waitDurationInOpenState(Duration.ofSeconds(10)) // reste OPEN 10 s
                        .permittedNumberOfCallsInHalfOpenState(2)        // 2 appels test en HALF_OPEN
                        .ignoreExceptions(FeignException.NotFound.class) // un 404 n'est pas une panne
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(3))          // au-delà de 3 s = échec
                        .build())
                .build());
    }
}
