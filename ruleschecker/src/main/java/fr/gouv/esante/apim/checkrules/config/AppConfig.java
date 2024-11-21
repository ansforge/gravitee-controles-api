/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.config;

import fr.gouv.esante.apim.client.ApiClient;
import fr.gouv.esante.apim.client.api.ApisApi;
import fr.gouv.esante.apim.client.api.ConfigurationApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;


@Configuration
@Profile({"!test"})
@Slf4j
public class AppConfig {

    /**
     * URL de l'API de gestion de l'APIM
     * Dépend de l'environnement ciblé
     */
    @Value("${apim.management.url}")
    private String apimUrl;

    /**
     * Token d'accès à l'API de gestion de l'APIM
     * Dépend de l'environnement ciblé
     */
    @Value("${apikey}")
    private String apiKey;


    @Bean
    public ApiClient apiClient() {
        log.info("ApiClient instantiated");
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(apimUrl);
        apiClient.setBearerToken(apiKey);
        return apiClient;
    }

    @Bean
    public ApisApi apisApi() {
        return new ApisApi(apiClient());
    }

    @Bean
    public ConfigurationApi configurationApi() {
        return new ConfigurationApi(apiClient());
    }


    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
