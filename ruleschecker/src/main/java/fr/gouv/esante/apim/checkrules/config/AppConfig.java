package fr.gouv.esante.apim.checkrules.config;

import fr.gouv.esante.apim.client.api.ApiPlansApi;
import fr.gouv.esante.apim.client.api.ApisApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ApisApi apisApi() {
        return new ApisApi();
    }

    @Bean
    public ApiPlansApi apiPlansApi() {
        return new ApiPlansApi();
    }
}
