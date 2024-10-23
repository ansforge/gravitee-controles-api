/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.test;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.HealthCheckService;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.HealthcheckActivation;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionMapper;

import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {HealthcheckActivationTest.class, ApiDefinitionMapper.class, RulesRegistry.class})
@ActiveProfiles({ "test" })
@Slf4j
class HealthcheckActivationTest extends HealthcheckActivation {

    @Autowired
    public HealthcheckActivationTest(RulesRegistry registry) {
        super(registry);
    }

    @Test
    void testHealthCheckIsEnabled() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        HealthCheckService healthCheck = new HealthCheckService();
        healthCheck.setEnabled(true);
        apiDef.setHealthCheck(healthCheck);
        HealthcheckActivation healthcheckActivation = new HealthcheckActivation(new RulesRegistry());
        RuleResult result = apiDef.accept(healthcheckActivation);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(SUCCESS_MSG, result.getMessage());
    }

    @Test
    void testHealthCheckDoesntExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        HealthcheckActivation healthcheckActivation = new HealthcheckActivation(new RulesRegistry());
        RuleResult result = apiDef.accept(healthcheckActivation);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testHealthCheckIsDisabled() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        HealthCheckService healthCheck = new HealthCheckService();
        healthCheck.setEnabled(false);
        apiDef.setHealthCheck(healthCheck);
        HealthcheckActivation healthcheckActivation = new HealthcheckActivation(new RulesRegistry());
        RuleResult result = apiDef.accept(healthcheckActivation);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }
}