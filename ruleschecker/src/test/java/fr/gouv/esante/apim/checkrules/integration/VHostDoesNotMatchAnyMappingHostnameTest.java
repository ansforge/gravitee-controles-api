/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import fr.gouv.esante.apim.checkrules.TestConfig;
import fr.gouv.esante.apim.checkrules.model.results.Report;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.GroupAssignment;
import fr.gouv.esante.apim.checkrules.rules.impl.HealthcheckActivation;
import fr.gouv.esante.apim.checkrules.rules.impl.HealthcheckSecured;
import fr.gouv.esante.apim.checkrules.rules.impl.LogsDisabled;
import fr.gouv.esante.apim.checkrules.rules.impl.SecuredPlan;
import fr.gouv.esante.apim.checkrules.rules.impl.SubdomainConfiguration;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionLoader;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionMapper;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.CheckRulesService;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesChecker;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import fr.gouv.esante.apim.client.api.ApisApi;
import fr.gouv.esante.apim.client.api.ConfigurationApi;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(
        classes = {VHostDoesNotMatchAnyMappingHostnameTest.class, CheckRulesService.class,
                ApiDefinitionMapper.class, ApiDefinitionLoader.class,
            RulesChecker.class, ApisApi.class, ConfigurationApi.class, RulesRegistry.class,
            GroupAssignment.class,
            HealthcheckActivation.class,
            HealthcheckSecured.class,
            LogsDisabled.class,
            SecuredPlan.class,
            SubdomainConfiguration.class
        }
)
@Import(TestConfig.class)
@ActiveProfiles({"test"})
@Slf4j
class VHostDoesNotMatchAnyMappingHostnameTest {

    @Autowired
    private CheckRulesService checkRulesService;

    @RegisterExtension
    static WireMockExtension httpMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort().usingFilesUnderClasspath(".")).build();


    @DynamicPropertySource
    static void dynamicPropertySources(DynamicPropertyRegistry registry) {
        registry.add("apikey",  () -> "mock-key");
        registry.add("envid",  () -> "VHM");
        registry.add("apim.management.url",  () -> httpMockServer.baseUrl() + "/");
    }


    @Test
    void testVHostDoesNotMatchAnyMappingHostname() throws Exception {
        String expectedMessage = String.format(
                "Erreur dans la configuration de sous-domaine d'accès à cette API :%sLe virtual host no.mapping.vhost n'est pas mappé dans les entrypoint mappings",
                System.lineSeparator()
        );

        Report report = checkRulesService.check();
        assertFalse(report.isSuccess());
        assertEquals(1, report.getGlobalCheckResults().size());
        List<RuleResult> apiResults = report.getGlobalCheckResults().get("Certificat_Structure").getRuleResults();
        Optional<RuleResult> rule3_3 = apiResults.stream().filter(r -> r.getRuleName().equalsIgnoreCase("3.3 - Un sous-domaine spécifique doit être configuré pour accéder à l’API")).findFirst();
        if(rule3_3.isPresent()) {
            assertFalse(rule3_3.get().isSuccess());
            assertTrue(rule3_3.get().getMessage().equalsIgnoreCase(expectedMessage), "Erreur de message : " + rule3_3.get().getMessage());
        }

    }
}