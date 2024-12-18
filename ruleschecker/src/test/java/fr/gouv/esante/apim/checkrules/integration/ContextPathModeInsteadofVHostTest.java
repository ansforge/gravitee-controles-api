/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.integration;

import fr.gouv.esante.apim.checkrules.config.AppTestConfig;
import fr.gouv.esante.apim.checkrules.model.results.Report;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.SubdomainConfiguration;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@SpringBootTest(
        classes = {
            AppTestConfig.class,
            CheckRulesService.class,
            ApiDefinitionMapper.class,
            ApiDefinitionLoader.class,
            RulesChecker.class,
            RulesRegistry.class,
            MessageProvider.class,
            SubdomainConfiguration.class,
        }
)
@ActiveProfiles({"test"})
@Slf4j
class ContextPathModeInsteadofVHostTest extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void dynamicPropertySources(DynamicPropertyRegistry registry) {
        registry.add("envid",  () -> "NVH");
    }


    @Test
    void testContextPathModeInsteadofVHost() throws Exception {
        String expectedMessage = String.format(
                messageProvider.getMessage("rule.subdomainconfig.msg.failure") +
                messageProvider.getMessage("rule.subdomainconfig.msg.notvhostmode"),
                System.lineSeparator()
        );

        Report report = checkRulesService.check();
        assertFalse(report.isSuccess());
        assertEquals(1, report.getGlobalCheckResults().size());
        List<RuleResult> apiResults = report.getGlobalCheckResults().get("Certificat_Structure (Certificat_Structure)").getRuleResults();
        Optional<RuleResult> rule3_3 = apiResults.stream().filter(r -> r.getRuleName().equalsIgnoreCase(messageProvider.getMessage("rule.subdomainconfig.name"))).findFirst();
        if(rule3_3.isPresent()) {
            assertFalse(rule3_3.get().isSuccess());
            assertEquals(expectedMessage, rule3_3.get().getMessage());
        }

    }
}