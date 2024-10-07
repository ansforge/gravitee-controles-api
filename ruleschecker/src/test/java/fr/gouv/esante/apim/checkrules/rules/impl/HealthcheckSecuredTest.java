/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.RuleResult;
import fr.gouv.esante.apim.checkrules.services.ApiDefinitionMapper;
import fr.gouv.esante.apim.client.model.PlanEntityGravitee;
import fr.gouv.esante.apim.client.model.PlanSecurityTypeGravitee;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {HealthcheckSecuredTest.class, ApiDefinitionMapper.class})
@ActiveProfiles({ "test" })
@Slf4j
class HealthcheckSecuredTest extends HealthcheckSecured {

    @Test
    void testHealthCheckPlanIsSecured() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<PlanEntityGravitee> plans = new HashSet<>();
        PlanEntityGravitee plan = new PlanEntityGravitee();
        plan.setName("testPlan-HealthCheck");
        plan.setSecurity(PlanSecurityTypeGravitee.KEY_LESS);
        plans.add(plan);
        apiDef.setPlans(plans);
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured();
        RuleResult result = apiDef.accept(healthcheckSecured);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(SUCCESS_MSG, result.getMessage());
    }

    @Test
    void testNoPlanExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured();
        RuleResult result = apiDef.accept(healthcheckSecured);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testPlansIsEmpty() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<PlanEntityGravitee> plans = new HashSet<>();
        apiDef.setPlans(plans);
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured();
        RuleResult result = apiDef.accept(healthcheckSecured);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testHealthCheckPlanDoesntExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<PlanEntityGravitee> plans = new HashSet<>();
        PlanEntityGravitee plan = new PlanEntityGravitee();
        plan.setName("testPlan-NotHealthCheck");
        plan.setSecurity(PlanSecurityTypeGravitee.KEY_LESS);
        plans.add(plan);
        apiDef.setPlans(plans);
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured();
        RuleResult result = apiDef.accept(healthcheckSecured);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testHealthCheckPlanIsNotSecured() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<PlanEntityGravitee> plans = new HashSet<>();
        PlanEntityGravitee plan = new PlanEntityGravitee();
        plan.setName("testPlan-HealthCheck");
        plans.add(plan);
        apiDef.setPlans(plans);
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured();
        RuleResult result = apiDef.accept(healthcheckSecured);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

}