/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import fr.gouv.esante.apim.checkrules.model.definition.Configuration;
import fr.gouv.esante.apim.checkrules.model.definition.Filter;
import fr.gouv.esante.apim.checkrules.model.definition.Flow;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.HealthCheckRequest;
import fr.gouv.esante.apim.checkrules.model.definition.HealthCheckService;
import fr.gouv.esante.apim.checkrules.model.definition.Logging;
import fr.gouv.esante.apim.checkrules.model.definition.Plan;
import fr.gouv.esante.apim.checkrules.model.definition.Step;
import fr.gouv.esante.apim.checkrules.model.definition.VirtualHost;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionMapper;
import fr.gouv.esante.apim.client.ApiClient;
import fr.gouv.esante.apim.client.api.ApisApi;
import fr.gouv.esante.apim.client.model.ApiEntityGravitee;
import fr.gouv.esante.apim.client.model.EntrypointEntityGravitee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@WireMockTest
@SpringBootTest(classes = ApiDefinitionMapper.class)
@ActiveProfiles({"test"})
@Slf4j
class ApiDefinitionMapperTest {

    @Test
    void testMappingOK(WireMockRuntimeInfo wmri) {

        ApiClient client = new ApiClient();
        client.setBasePath("http://localhost:" + wmri.getHttpPort() + "/");
        ApisApi apisApi = new ApisApi(client);


        GraviteeApiDefinition expectedApiDef = new GraviteeApiDefinition();
        Set<String> groups = new HashSet<>();
        groups.add("group1");
        groups.add("group2");

        Plan accessPlan = new Plan();
        accessPlan.setAuthMechanism("JWT");
        accessPlan.setName("access-plan");
        accessPlan.setStatus("PUBLISHED");
        Flow emptyFlow = new Flow();
        emptyFlow.setPreSteps(new ArrayList<>());
        emptyFlow.setPostSteps(new ArrayList<>());
        accessPlan.setFlows(List.of(emptyFlow));

        Plan healthcheckPlan = new Plan();
        healthcheckPlan.setAuthMechanism("KEY_LESS");
        healthcheckPlan.setName("plan-HealthCheck");
        healthcheckPlan.setStatus("STAGING");

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

        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("localhost");
        virtualHost.setOverrideEntrypoint(true);
        virtualHost.setPath("/vhost/path");

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

        expectedApiDef.setApiName("TestAPI-ex (v1.0)");
        expectedApiDef.setAdminGroups(groups);
        expectedApiDef.setPlans(Set.of(accessPlan, healthcheckPlan));
        expectedApiDef.setVirtualHosts(List.of(virtualHost));
        expectedApiDef.setHealthCheck(healthCheck);
        expectedApiDef.setLogging(logging);


        ApiEntityGravitee apiEntity = apisApi.getApi("mapper-test-ok", "envId", "orgId");

        List<EntrypointEntityGravitee> entrypointEntities = new ArrayList<>();
        EntrypointEntityGravitee entrypointEntity = new EntrypointEntityGravitee();
        entrypointEntity.setId("0123-4567-8910");
        entrypointEntity.setTags(List.of("tagId"));
        entrypointEntity.setValue("http://localhost");
        entrypointEntities.add(entrypointEntity);

        ApiDefinitionMapper mapper = new ApiDefinitionMapper();
        GraviteeApiDefinition apiDef = mapper.map(apiEntity, entrypointEntities);


        assertNotNull(apiDef);
        assertEquals(expectedApiDef.getApiName(), apiDef.getApiName());
        assertEquals(expectedApiDef.getAdminGroups(), apiDef.getAdminGroups());
        assertEquals(expectedApiDef.getPlans(), apiDef.getPlans());
        assertEquals(expectedApiDef.getVirtualHosts(), apiDef.getVirtualHosts());
        assertEquals(expectedApiDef.getHealthCheck(), apiDef.getHealthCheck());
        assertEquals(expectedApiDef.getLogging(), apiDef.getLogging());
    }

}