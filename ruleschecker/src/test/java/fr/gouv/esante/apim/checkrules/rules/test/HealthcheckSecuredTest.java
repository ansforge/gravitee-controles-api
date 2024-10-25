/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.test;

import fr.gouv.esante.apim.checkrules.model.definition.Configuration;
import fr.gouv.esante.apim.checkrules.model.definition.Filter;
import fr.gouv.esante.apim.checkrules.model.definition.Flow;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Plan;
import fr.gouv.esante.apim.checkrules.model.definition.Step;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.HealthcheckSecured;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionMapper;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {HealthcheckSecuredTest.class, ApiDefinitionMapper.class, RulesRegistry.class})
@ActiveProfiles({"test"})
@Slf4j
class HealthcheckSecuredTest extends HealthcheckSecured {

    @Autowired
    public HealthcheckSecuredTest(RulesRegistry registry) {
        super(registry);
    }

    @Test
    void testHealthCheckPlanIsSecured() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan plan = new Plan();
        plan.setName("testPlan-HealthCheck");
        plan.setAuthMechanism("KEY_LESS");

        Flow flow = new Flow();
        Step pre = new Step();
        pre.setPolicy("resource-filtering");

        Filter filter = new Filter();
        filter.setMethods(List.of("GET"));
        List<Filter> whitelist = List.of(filter);

        Configuration configuration = new Configuration();
        configuration.setWhitelist(whitelist);
        configuration.setBlacklist(new ArrayList<>());

        pre.setConfiguration(configuration);
        flow.setPreSteps(Collections.singletonList(pre));
        plan.setFlows(Collections.singletonList(flow));
        plans.add(plan);
        apiDef.setPlans(plans);

        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry());
        RuleResult result = apiDef.accept(healthcheckSecured);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(SUCCESS_MSG, result.getMessage());
    }

    @Test
    void testNoPlanExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry());
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(" :%sAucun plan n'est associé à cette API", System.lineSeparator());

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
    }

    @Test
    void testPlansIsEmpty() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        apiDef.setPlans(plans);
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry());
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(" :%sAucun plan n'est associé à cette API", System.lineSeparator());

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
    }

    @Test
    void testHealthCheckPlanDoesntExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan plan = new Plan();
        plan.setName("testPlan-NotHealthCheck");
        plan.setAuthMechanism("KEY_LESS");
        plans.add(plan);
        apiDef.setPlans(plans);
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry());
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(" :%sAucun plan se terminant par -HealthCheck" +
                        " n'est associé à cette API",
                System.lineSeparator());

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
    }

    @Test
    void testHealthCheckPlanIsNotKeyLess() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan plan = new Plan();
        plan.setName("testPlan-HealthCheck");
        plans.add(plan);
        apiDef.setPlans(plans);
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry());
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(" :%sLe type d'authentification du plan healthcheck" +
                        " doit être KEY_LESS",
                System.lineSeparator());

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
    }

    @Test
    void testHealthCheckPlanNoFiltering() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan plan = new Plan();
        plan.setName("testPlan-HealthCheck");
        plan.setAuthMechanism("KEY_LESS");

        Flow flow = new Flow();
        Step pre = new Step();
        pre.setPolicy("");

        Filter filter = new Filter();
        filter.setMethods(List.of("GET"));
        List<Filter> whitelist = List.of(filter);

        Configuration configuration = new Configuration();
        configuration.setWhitelist(whitelist);
        configuration.setBlacklist(new ArrayList<>());

        pre.setConfiguration(configuration);
        flow.setPreSteps(Collections.singletonList(pre));
        plan.setFlows(Collections.singletonList(flow));
        plans.add(plan);
        apiDef.setPlans(plans);

        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry());
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(" :%sLe plan healthcheck doit inclure" +
                        " une restriction de type Resource Filtering",
                System.lineSeparator());

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
    }

    @Test
    void testHealthCheckPlanEmptyWhitelist() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan plan = new Plan();
        plan.setName("testPlan-HealthCheck");
        plan.setAuthMechanism("KEY_LESS");

        Flow flow = new Flow();
        Step pre = new Step();
        pre.setPolicy("resource-filtering");

        Configuration configuration = new Configuration();
        configuration.setWhitelist(new ArrayList<>());
        configuration.setBlacklist(new ArrayList<>());

        pre.setConfiguration(configuration);
        flow.setPreSteps(Collections.singletonList(pre));
        plan.setFlows(Collections.singletonList(flow));
        plans.add(plan);
        apiDef.setPlans(plans);

        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry());
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(" :%sLa whitelist du plan" +
                        " healthcheck est vide",
                System.lineSeparator());

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
    }

    @Test
    void testHealthCheckPlanWhitelistNotStrictEnough() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan plan = new Plan();
        plan.setName("testPlan-HealthCheck");
        plan.setAuthMechanism("KEY_LESS");

        Flow flow = new Flow();
        Step pre = new Step();
        pre.setPolicy("resource-filtering");

        Filter filter = new Filter();
        filter.setMethods(List.of("GET"));
        List<Filter> whitelist = List.of(filter, filter);

        Configuration configuration = new Configuration();
        configuration.setWhitelist(whitelist);
        configuration.setBlacklist(new ArrayList<>());

        pre.setConfiguration(configuration);
        flow.setPreSteps(Collections.singletonList(pre));
        plan.setFlows(Collections.singletonList(flow));
        plans.add(plan);
        apiDef.setPlans(plans);

        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry());
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(" :%sLa whitelist du plan" +
                        " healthcheck ne doit autoriser l'accès qu'au endpoit" +
                        " healthcheck",
                System.lineSeparator());

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
    }

    @Test
    void testHealthCheckPlanWhitelistMustOnlyUseGET() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan plan = new Plan();
        plan.setName("testPlan-HealthCheck");
        plan.setAuthMechanism("KEY_LESS");

        Flow flow = new Flow();
        Step pre = new Step();
        pre.setPolicy("resource-filtering");

        Filter filter = new Filter();
        filter.setMethods(List.of("POST"));
        List<Filter> whitelist = List.of(filter);

        Configuration configuration = new Configuration();
        configuration.setWhitelist(whitelist);
        configuration.setBlacklist(new ArrayList<>());

        pre.setConfiguration(configuration);
        flow.setPreSteps(Collections.singletonList(pre));
        plan.setFlows(Collections.singletonList(flow));
        plans.add(plan);
        apiDef.setPlans(plans);

        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry());
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(" :%sLe endpoint healthcheck" +
                        " ne doit être accessible qu'en GET : méthode %s trouvée",
                System.lineSeparator(),
                whitelist.get(0).getMethods().get(0));
        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
    }

}