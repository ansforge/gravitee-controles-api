/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.RuleResult;
import fr.gouv.esante.apim.checkrules.services.ApiDefinitionMapper;
import fr.gouv.esante.apim.client.model.HealthCheckServiceGravitee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {HealthcheckActivationTest.class, ApiDefinitionMapper.class})
@ActiveProfiles({ "test" })
@Slf4j
class HealthcheckActivationTest extends HealthcheckActivation {

    @Test
    void testHealthCheckIsEnabled() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        HealthCheckServiceGravitee healthCheck = new HealthCheckServiceGravitee();
        healthCheck.setEnabled(true);
        apiDef.setHealthCheck(healthCheck);
        HealthcheckActivation healthcheckActivation = new HealthcheckActivation();
        RuleResult result = apiDef.accept(healthcheckActivation);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(SUCCESS_MSG, result.getMessage());
    }

    @Test
    void testHealthCheckDoesntExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        HealthcheckActivation healthcheckActivation = new HealthcheckActivation();
        RuleResult result = apiDef.accept(healthcheckActivation);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testHealthCheckIsDisabled() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        HealthCheckServiceGravitee healthCheck = new HealthCheckServiceGravitee();
        healthCheck.setEnabled(false);
        apiDef.setHealthCheck(healthCheck);
        HealthcheckActivation healthcheckActivation = new HealthcheckActivation();
        RuleResult result = apiDef.accept(healthcheckActivation);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }
}