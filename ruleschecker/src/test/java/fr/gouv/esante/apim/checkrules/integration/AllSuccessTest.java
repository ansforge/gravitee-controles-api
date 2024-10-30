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

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(
        classes = {AllSuccessTest.class, CheckRulesService.class,
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
class AllSuccessTest {

    @Autowired
    private CheckRulesService checkRulesService;

    @RegisterExtension
    static WireMockExtension httpMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort().usingFilesUnderClasspath(".")).build();


    @DynamicPropertySource
    static void dynamicPropertySources(DynamicPropertyRegistry registry) {
        registry.add("apikey",  () -> "mock-key");
        registry.add("envid",  () -> "ALL");
        registry.add("apim.management.url",  () -> httpMockServer.baseUrl() + "/");
    }


    @Test
    void testAllSuccess() throws Exception {
        Report report = checkRulesService.check();
        assertTrue(report.isSuccess());
        assertEquals(1, report.getGlobalCheckResults().size());
        List<RuleResult> apiResults = report.getGlobalCheckResults().get("Certificat_Structure").getRuleResults();
        assertEquals(6, apiResults.size());
    }
}