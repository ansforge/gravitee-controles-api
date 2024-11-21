/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.integration;

import fr.gouv.esante.apim.checkrules.config.AppTestConfig;
import fr.gouv.esante.apim.checkrules.model.results.Report;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.HealthcheckActivation;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionLoader;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionMapper;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.CheckRulesService;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesChecker;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(
        classes = {
            AppTestConfig.class,
            CheckRulesService.class,
            ApiDefinitionMapper.class,
            ApiDefinitionLoader.class,
            RulesChecker.class,
            RulesRegistry.class,
            MessageProvider.class,
            HealthcheckActivation.class,
        }
)
@ActiveProfiles({"test"})
@Slf4j
class NoEndpointHealthcheckTest extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void dynamicPropertySources(DynamicPropertyRegistry registry) {
        registry.add("envid",  () -> "NEH");
    }


    @Test
    void testNoEndpointHealthcheck() throws Exception {
        String expectedMessage = messageProvider.getMessage("rule.healthcheckactivation.msg.failure");

        Report report = checkRulesService.check();
        assertFalse(report.isSuccess());
        assertEquals(1, report.getGlobalCheckResults().size());
        List<RuleResult> apiResults = report.getGlobalCheckResults().get("Certificat_Structure (Certificat_Structure)").getRuleResults();
        Optional<RuleResult> rule6_1 = apiResults.stream().filter(r -> r.getRuleName().equalsIgnoreCase(messageProvider.getMessage("rule.healthcheckactivation.name"))).findFirst();
        if(rule6_1.isPresent()) {
            assertFalse(rule6_1.get().isSuccess());
            assertTrue(rule6_1.get().getMessage().equalsIgnoreCase(expectedMessage));
        }
    }
}