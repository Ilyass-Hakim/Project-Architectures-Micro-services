package org.emsi.EBank_Service.config;

import org.emsi.EBank_Service.entities.BankAccount;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

    private final LocalValidatorFactoryBean validator;

    public RestConfig(LocalValidatorFactoryBean validator) {
        this.validator = validator;
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(BankAccount.class);
    }

    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener listener) {
        // Reject invalid payloads with 400 instead of failing at the persistence layer
        listener.addValidator("beforeCreate", validator);
        listener.addValidator("beforeSave", validator);
    }
}
