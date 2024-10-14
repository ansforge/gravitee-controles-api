/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import fr.gouv.esante.apim.checkrules.model.Configuration;
import fr.gouv.esante.apim.checkrules.model.Entrypoint;
import fr.gouv.esante.apim.checkrules.model.Filter;
import fr.gouv.esante.apim.checkrules.model.Flow;
import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.HealthCheckRequest;
import fr.gouv.esante.apim.checkrules.model.HealthCheckService;
import fr.gouv.esante.apim.checkrules.model.Logging;
import fr.gouv.esante.apim.checkrules.model.Plan;
import fr.gouv.esante.apim.checkrules.model.Step;
import fr.gouv.esante.apim.checkrules.model.VirtualHost;

import fr.gouv.esante.apim.client.ApiClient;
import fr.gouv.esante.apim.client.api.ApisApi;
import fr.gouv.esante.apim.client.model.ApiEntityGravitee;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@WireMockTest
@SpringBootTest(classes=ApiDefinitionMapper.class)
@ActiveProfiles({ "test" })
@Slf4j
class ApiDefinitionMapperTest {

    private ApiClient client;

    @Test
    void testMapping(WireMockRuntimeInfo wmri) {

        client = new ApiClient();
        client.setBasePath("http://localhost:" + wmri.getHttpPort() + "/");
        ApisApi apisApi = new ApisApi(client);

        ApiEntityGravitee apiEntity = apisApi.getApi("test-mapping-ok", "envId", "orgId");
        ApiDefinitionMapper mapper = new ApiDefinitionMapper();
        GraviteeApiDefinition apiDef = mapper.map(apiEntity);

        GraviteeApiDefinition expectedApiDef = new GraviteeApiDefinition();
        Set<String> groups = new HashSet<>();
        groups.add("group1");
        groups.add("group2");

        Plan accessPlan = new Plan();
        accessPlan.setAuthMechanism("JWT");
        accessPlan.setName("access-plan");
        Flow emptyFlow = new Flow();
        emptyFlow.setPreSteps(new ArrayList<>());
        emptyFlow.setPostSteps(new ArrayList<>());
        accessPlan.setFlows(List.of(emptyFlow));

        Plan healthcheckPlan = new Plan();
        healthcheckPlan.setAuthMechanism("KEY_LESS");
        healthcheckPlan.setName("plan-HealthCheck");

        List<Flow> flows = new ArrayList<>();
        Flow flow = new Flow();
        List<Step> preSteps = new ArrayList<>();
        Step preStep = new Step();
        preStep.setPolicy("resource-filtering");

        Configuration config = new Configuration();
        Filter whitelistItem = new Filter();
        whitelistItem.setMethods(List.of("GET"));
        whitelistItem.setPattern("/healthcheck");
        config.setWhitelist(List.of(whitelistItem));
        config.setBlacklist(new ArrayList<>());
        preStep.setConfiguration(config);

        preSteps.add(preStep);
        flow.setPreSteps(preSteps);
        flow.setPostSteps(new ArrayList<>());
        flows.add(flow);
        healthcheckPlan.setFlows(flows);

        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setHost("localhost");
        entrypoint.setTags(Set.of("public"));
        entrypoint.setTarget("entryPath");
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("localhost");
        virtualHost.setOverrideEntrypoint(true);
        virtualHost.setPath("vhostPath");

        HealthCheckService healthCheck = new HealthCheckService();
        healthCheck.setEnabled(true);
        HealthCheckRequest healthCheckPath = new HealthCheckRequest();
        healthCheckPath.setMethod("GET");
        healthCheckPath.setPath("/actuator/healthcheck");
        healthCheck.setPaths(List.of(healthCheckPath));

        Logging logging = new Logging();
        logging.setContent("NONE");
        logging.setMode("NONE");
        logging.setScope("NONE");

        expectedApiDef.setApiName("TestAPI-ex");
        expectedApiDef.setGroups(groups);
        expectedApiDef.setPlans(Set.of(accessPlan, healthcheckPlan));
        expectedApiDef.setTags(Set.of("public"));
        expectedApiDef.setEntrypoints(List.of(entrypoint));
        expectedApiDef.setVirtualHosts(List.of(virtualHost));
        expectedApiDef.setHealthCheck(healthCheck);
        expectedApiDef.setLogging(logging);

        assertNotNull(apiDef);
        assertEquals(expectedApiDef.getApiName(), apiDef.getApiName());
        assertEquals(expectedApiDef.getGroups(), apiDef.getGroups());
        assertEquals(expectedApiDef.getPlans(), apiDef.getPlans());
        assertEquals(expectedApiDef.getTags(), apiDef.getTags());
        assertEquals(expectedApiDef.getEntrypoints(), apiDef.getEntrypoints());
        assertEquals(expectedApiDef.getVirtualHosts(), apiDef.getVirtualHosts());
        assertEquals(expectedApiDef.getHealthCheck(), apiDef.getHealthCheck());
        assertEquals(expectedApiDef.getLogging(), apiDef.getLogging());
    }

}