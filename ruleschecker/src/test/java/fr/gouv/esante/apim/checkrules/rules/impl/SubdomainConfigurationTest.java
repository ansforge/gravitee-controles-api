/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.RuleResult;
import fr.gouv.esante.apim.checkrules.services.ApiDefinitionMapper;
import fr.gouv.esante.apim.client.model.ApiEntrypointEntityGravitee;
import fr.gouv.esante.apim.client.model.VirtualHostGravitee;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {SubdomainConfigurationTest.class, ApiDefinitionMapper.class})
@ActiveProfiles({ "test" })
@Slf4j
class SubdomainConfigurationTest extends SubdomainConfiguration {

    @Test
    void testSubdomainConfigurationIsOK() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> tags = new HashSet<>();
        String publicTag = "testPublicTag";
        String privateTag = "testPrivateTag";
        tags.add(publicTag);
        tags.add(privateTag);

        List<ApiEntrypointEntityGravitee> entrypoints = new ArrayList<>();
        ApiEntrypointEntityGravitee entrypoint = new ApiEntrypointEntityGravitee();
        String epHost = "testHost";
        entrypoint.setHost(epHost);
        entrypoint.setTags(tags);
        entrypoints.add(entrypoint);

        List<VirtualHostGravitee> virtualHosts = new ArrayList<>();
        VirtualHostGravitee virtualHost = new VirtualHostGravitee();
        String vhHost = "testHost";
        virtualHost.setHost(vhHost);
        virtualHosts.add(virtualHost);

        apiDef.setTags(tags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(SUCCESS_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoTags() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> tags = new HashSet<>();
        String publicTag = "testPublicTag";
        String privateTag = "testPrivateTag";
        tags.add(publicTag);
        tags.add(privateTag);

        List<ApiEntrypointEntityGravitee> entrypoints = new ArrayList<>();
        ApiEntrypointEntityGravitee entrypoint = new ApiEntrypointEntityGravitee();
        String epHost = "testHost";
        entrypoint.setHost(epHost);
        entrypoint.setTags(tags);
        entrypoints.add(entrypoint);

        List<VirtualHostGravitee> virtualHosts = new ArrayList<>();
        VirtualHostGravitee virtualHost = new VirtualHostGravitee();
        String vhHost = "testHost";
        virtualHost.setHost(vhHost);
        virtualHosts.add(virtualHost);

        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertEquals(super.getName(), result.getRuleName());
        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyTags() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> tags = new HashSet<>();

        List<ApiEntrypointEntityGravitee> entrypoints = new ArrayList<>();
        ApiEntrypointEntityGravitee entrypoint = new ApiEntrypointEntityGravitee();
        String epHost = "testHost";
        entrypoint.setHost(epHost);
        entrypoint.setTags(tags);
        entrypoints.add(entrypoint);

        List<VirtualHostGravitee> virtualHosts = new ArrayList<>();
        VirtualHostGravitee virtualHost = new VirtualHostGravitee();
        String vhHost = "testHost";
        virtualHost.setHost(vhHost);
        virtualHosts.add(virtualHost);

        apiDef.setTags(tags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertEquals(super.getName(), result.getRuleName());
        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoEntrypoint() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> tags = new HashSet<>();
        String publicTag = "testPublicTag";
        String privateTag = "testPrivateTag";
        tags.add(publicTag);
        tags.add(privateTag);

        List<VirtualHostGravitee> virtualHosts = new ArrayList<>();
        VirtualHostGravitee virtualHost = new VirtualHostGravitee();
        String vhHost = "testHost";
        virtualHost.setHost(vhHost);
        virtualHosts.add(virtualHost);

        apiDef.setTags(tags);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertEquals(super.getName(), result.getRuleName());
        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyEntrypoint() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> tags = new HashSet<>();
        String publicTag = "testPublicTag";
        String privateTag = "testPrivateTag";
        tags.add(publicTag);
        tags.add(privateTag);

        List<ApiEntrypointEntityGravitee> entrypoints = new ArrayList<>();

        List<VirtualHostGravitee> virtualHosts = new ArrayList<>();
        VirtualHostGravitee virtualHost = new VirtualHostGravitee();
        String vhHost = "testHost";
        virtualHost.setHost(vhHost);
        virtualHosts.add(virtualHost);

        apiDef.setTags(tags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertEquals(super.getName(), result.getRuleName());
        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoEPHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> tags = new HashSet<>();
        String publicTag = "testPublicTag";
        String privateTag = "testPrivateTag";
        tags.add(publicTag);
        tags.add(privateTag);

        List<ApiEntrypointEntityGravitee> entrypoints = new ArrayList<>();
        ApiEntrypointEntityGravitee entrypoint = new ApiEntrypointEntityGravitee();
        entrypoint.setTags(tags);
        entrypoints.add(entrypoint);

        List<VirtualHostGravitee> virtualHosts = new ArrayList<>();
        VirtualHostGravitee virtualHost = new VirtualHostGravitee();
        String vhHost = "testHost";
        virtualHost.setHost(vhHost);
        virtualHosts.add(virtualHost);

        apiDef.setTags(tags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertEquals(super.getName(), result.getRuleName());
        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyEPHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> tags = new HashSet<>();
        String publicTag = "testPublicTag";
        String privateTag = "testPrivateTag";
        tags.add(publicTag);
        tags.add(privateTag);

        List<ApiEntrypointEntityGravitee> entrypoints = new ArrayList<>();
        ApiEntrypointEntityGravitee entrypoint = new ApiEntrypointEntityGravitee();
        String epHost = "";
        entrypoint.setHost(epHost);
        entrypoint.setTags(tags);
        entrypoints.add(entrypoint);

        List<VirtualHostGravitee> virtualHosts = new ArrayList<>();
        VirtualHostGravitee virtualHost = new VirtualHostGravitee();
        String vhHost = "testHost";
        virtualHost.setHost(vhHost);
        virtualHosts.add(virtualHost);

        apiDef.setTags(tags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertEquals(super.getName(), result.getRuleName());
        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoVirtualHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> tags = new HashSet<>();
        String publicTag = "testPublicTag";
        String privateTag = "testPrivateTag";
        tags.add(publicTag);
        tags.add(privateTag);

        List<ApiEntrypointEntityGravitee> entrypoints = new ArrayList<>();
        ApiEntrypointEntityGravitee entrypoint = new ApiEntrypointEntityGravitee();
        String epHost = "testHost";
        entrypoint.setHost(epHost);
        entrypoint.setTags(tags);
        entrypoints.add(entrypoint);

        apiDef.setTags(tags);
        apiDef.setEntrypoints(entrypoints);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertEquals(super.getName(), result.getRuleName());
        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyVirtualHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> tags = new HashSet<>();
        String publicTag = "testPublicTag";
        String privateTag = "testPrivateTag";
        tags.add(publicTag);
        tags.add(privateTag);

        List<ApiEntrypointEntityGravitee> entrypoints = new ArrayList<>();
        ApiEntrypointEntityGravitee entrypoint = new ApiEntrypointEntityGravitee();
        String epHost = "testHost";
        entrypoint.setHost(epHost);
        entrypoint.setTags(tags);
        entrypoints.add(entrypoint);

        List<VirtualHostGravitee> virtualHosts = new ArrayList<>();

        apiDef.setTags(tags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertEquals(super.getName(), result.getRuleName());
        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoVHHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> tags = new HashSet<>();
        String publicTag = "testPublicTag";
        String privateTag = "testPrivateTag";
        tags.add(publicTag);
        tags.add(privateTag);

        List<ApiEntrypointEntityGravitee> entrypoints = new ArrayList<>();
        ApiEntrypointEntityGravitee entrypoint = new ApiEntrypointEntityGravitee();
        String epHost = "testHost";
        entrypoint.setHost(epHost);
        entrypoint.setTags(tags);
        entrypoints.add(entrypoint);

        List<VirtualHostGravitee> virtualHosts = new ArrayList<>();
        VirtualHostGravitee virtualHost = new VirtualHostGravitee();
        virtualHosts.add(virtualHost);

        apiDef.setTags(tags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertEquals(super.getName(), result.getRuleName());
        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyVHHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> tags = new HashSet<>();
        String publicTag = "testPublicTag";
        String privateTag = "testPrivateTag";
        tags.add(publicTag);
        tags.add(privateTag);

        List<ApiEntrypointEntityGravitee> entrypoints = new ArrayList<>();
        ApiEntrypointEntityGravitee entrypoint = new ApiEntrypointEntityGravitee();
        String epHost = "testHost";
        entrypoint.setHost(epHost);
        entrypoint.setTags(tags);
        entrypoints.add(entrypoint);

        List<VirtualHostGravitee> virtualHosts = new ArrayList<>();
        VirtualHostGravitee virtualHost = new VirtualHostGravitee();
        String vhHost = "";
        virtualHost.setHost(vhHost);
        virtualHosts.add(virtualHost);

        apiDef.setTags(tags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertEquals(super.getName(), result.getRuleName());
        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEPHostNotInVirtualHosts() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> tags = new HashSet<>();
        String publicTag = "testPublicTag";
        String privateTag = "testPrivateTag";
        tags.add(publicTag);
        tags.add(privateTag);

        List<ApiEntrypointEntityGravitee> entrypoints = new ArrayList<>();
        ApiEntrypointEntityGravitee entrypoint = new ApiEntrypointEntityGravitee();
        String epHost = "testEpHost";
        entrypoint.setHost(epHost);
        entrypoint.setTags(tags);
        entrypoints.add(entrypoint);

        List<VirtualHostGravitee> virtualHosts = new ArrayList<>();
        VirtualHostGravitee virtualHost = new VirtualHostGravitee();
        String vhHost = "testVhHost";
        virtualHost.setHost(vhHost);
        virtualHosts.add(virtualHost);

        apiDef.setTags(tags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }
}