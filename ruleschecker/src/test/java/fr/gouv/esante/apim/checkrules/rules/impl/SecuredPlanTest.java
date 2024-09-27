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


@SpringBootTest(classes = {SecuredPlanTest.class, ApiDefinitionMapper.class})
@ActiveProfiles({ "test" })
@Slf4j
class SecuredPlanTest extends SecuredPlan {

    @Test
    void testSecuredPlanExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<PlanEntityGravitee> plans = new HashSet<>();
        PlanEntityGravitee plan = new PlanEntityGravitee();
        plan.setSecurity(PlanSecurityTypeGravitee.API_KEY);
        plans.add(plan);
        apiDef.setPlans(plans);
        SecuredPlan securedPlanRule = new SecuredPlan();
        RuleResult result = apiDef.accept(securedPlanRule);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(SUCCESS_MSG, result.getMessage());
    }

    @Test
    void testNoPlanExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        SecuredPlan securedPlanRule = new SecuredPlan();
        RuleResult result = apiDef.accept(securedPlanRule);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSecuredPlanDoesNotExist() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<PlanEntityGravitee> plans = new HashSet<>();
        PlanEntityGravitee keyLessPlan = new PlanEntityGravitee();
        keyLessPlan.setSecurity(PlanSecurityTypeGravitee.KEY_LESS);
        plans.add(keyLessPlan);
        PlanEntityGravitee jwtPlan = new PlanEntityGravitee();
        jwtPlan.setSecurity(PlanSecurityTypeGravitee.JWT);
        plans.add(jwtPlan);
        PlanEntityGravitee subscriptionPlan = new PlanEntityGravitee();
        subscriptionPlan.setSecurity(PlanSecurityTypeGravitee.SUBSCRIPTION);
        plans.add(subscriptionPlan);
        PlanEntityGravitee noSecurityPlan = new PlanEntityGravitee();
        plans.add(noSecurityPlan);
        apiDef.setPlans(plans);
        SecuredPlan securedPlanRule = new SecuredPlan();
        RuleResult result = apiDef.accept(securedPlanRule);

        assertFalse(result.isSuccess());
    }

    @Test
    void testMultipleSecuredPlanExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<PlanEntityGravitee> plans = new HashSet<>();
        PlanEntityGravitee noSecurityPlan = new PlanEntityGravitee();
        plans.add(noSecurityPlan);
        PlanEntityGravitee apiKeyPlan = new PlanEntityGravitee();
        apiKeyPlan.setSecurity(PlanSecurityTypeGravitee.API_KEY);
        plans.add(apiKeyPlan);
        PlanEntityGravitee oAuth2Plan = new PlanEntityGravitee();
        oAuth2Plan.setSecurity(PlanSecurityTypeGravitee.OAUTH2);
        plans.add(oAuth2Plan);
        PlanEntityGravitee keyLessPlan = new PlanEntityGravitee();
        keyLessPlan.setSecurity(PlanSecurityTypeGravitee.KEY_LESS);
        plans.add(keyLessPlan);
        apiDef.setPlans(plans);
        SecuredPlan securedPlanRule = new SecuredPlan();
        RuleResult result = apiDef.accept(securedPlanRule);

        assertTrue(result.isSuccess());
    }








}