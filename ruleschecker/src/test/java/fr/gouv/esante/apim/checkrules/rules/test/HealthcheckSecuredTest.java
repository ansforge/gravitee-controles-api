/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.test;

import fr.gouv.esante.apim.checkrules.config.AppTestConfig;
import fr.gouv.esante.apim.checkrules.model.definition.Configuration;
import fr.gouv.esante.apim.checkrules.model.definition.Filter;
import fr.gouv.esante.apim.checkrules.model.definition.Flow;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Plan;
import fr.gouv.esante.apim.checkrules.model.definition.Step;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.HealthcheckSecured;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
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


@SpringBootTest(
        classes = {
            AppTestConfig.class,
            ApiDefinitionMapper.class,
            RulesRegistry.class,
            MessageProvider.class
        })
@ActiveProfiles({"test"})
@Slf4j
class HealthcheckSecuredTest extends HealthcheckSecured {

    @Autowired
    public HealthcheckSecuredTest(RulesRegistry registry, MessageProvider messageProvider) {
        super(registry, messageProvider);
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

        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(healthcheckSecured);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.healthchecksecured.msg.success"), result.getMessage());
    }

    @Test
    void testNoPlanExists() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(String.format(messageProvider.getMessage("rule.healthchecksecured.msg.detail.noplan"), System.lineSeparator()));

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.healthchecksecured.msg.failure") + errorDetails, result.getMessage());
    }

    @Test
    void testPlansIsEmpty() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        apiDef.setPlans(plans);
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(String.format(messageProvider.getMessage("rule.healthchecksecured.msg.detail.noplan"), System.lineSeparator()));

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.healthchecksecured.msg.failure") + errorDetails, result.getMessage());
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
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(messageProvider.getMessage("rule.healthchecksecured.msg.detail.nohealthcheckplan"),
                System.lineSeparator());

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.healthchecksecured.msg.failure") + errorDetails, result.getMessage());
    }

    @Test
    void testHealthCheckPlanIsNotKeyLess() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<Plan> plans = new HashSet<>();
        Plan plan = new Plan();
        plan.setName("testPlan-HealthCheck");
        plans.add(plan);
        apiDef.setPlans(plans);
        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(messageProvider.getMessage("rule.healthchecksecured.msg.detail.authmechanismnotallowed"),
                System.lineSeparator());

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.healthchecksecured.msg.failure") + errorDetails, result.getMessage());
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

        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(messageProvider.getMessage("rule.healthchecksecured.msg.detail.noresourcefiltering"),
                System.lineSeparator());

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.healthchecksecured.msg.failure") + errorDetails, result.getMessage());
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

        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(messageProvider.getMessage("rule.healthchecksecured.msg.detail.emptywhitelist"),
                System.lineSeparator());

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.healthchecksecured.msg.failure") + errorDetails, result.getMessage());
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

        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(messageProvider.getMessage("rule.healthchecksecured.msg.detail.accessunauthorized"),
                System.lineSeparator());

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.healthchecksecured.msg.failure") + errorDetails, result.getMessage());
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

        HealthcheckSecured healthcheckSecured = new HealthcheckSecured(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(healthcheckSecured);
        String errorDetails = String.format(messageProvider.getMessage("rule.healthchecksecured.msg.detail.methodnotallowed"),
                System.lineSeparator(),
                whitelist.get(0).getMethods().get(0));
        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.healthchecksecured.msg.failure") + errorDetails, result.getMessage());
    }

}