/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.test;

import fr.gouv.esante.apim.checkrules.config.AppTestConfig;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.HealthCheckService;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.HealthcheckActivation;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionMapper;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(
        classes = {
            AppTestConfig.class,
            ApiDefinitionMapper.class,
            RulesRegistry.class,
            MessageProvider.class
        })
@ActiveProfiles({"test"})
@Slf4j
class HealthcheckActivationTest extends HealthcheckActivation {

    @Autowired
    public HealthcheckActivationTest(RulesRegistry registry, MessageProvider messageProvider) {
        super(registry, messageProvider);
    }

    @Test
    void testHealthCheckIsEnabled() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        HealthCheckService healthCheck = new HealthCheckService();
        healthCheck.setEnabled(true);
        apiDef.setHealthCheck(healthCheck);
        HealthcheckActivation healthcheckActivation = new HealthcheckActivation(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(healthcheckActivation);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.healthcheckactivation.msg.success"), result.getMessage());
    }

    @Test
    void testHealthCheckDoesntExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        HealthcheckActivation healthcheckActivation = new HealthcheckActivation(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(healthcheckActivation);

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.healthcheckactivation.msg.failure"), result.getMessage());
    }

    @Test
    void testHealthCheckIsDisabled() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        HealthCheckService healthCheck = new HealthCheckService();
        healthCheck.setEnabled(false);
        apiDef.setHealthCheck(healthCheck);
        HealthcheckActivation healthcheckActivation = new HealthcheckActivation(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(healthcheckActivation);

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.healthcheckactivation.msg.failure"), result.getMessage());
    }
}