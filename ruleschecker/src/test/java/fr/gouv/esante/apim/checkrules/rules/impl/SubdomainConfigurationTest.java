/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.Entrypoint;
import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.RuleResult;
import fr.gouv.esante.apim.checkrules.model.ShardingTag;
import fr.gouv.esante.apim.checkrules.model.VirtualHost;
import fr.gouv.esante.apim.checkrules.services.ApiDefinitionMapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {SubdomainConfigurationTest.class, ApiDefinitionMapper.class})
@ActiveProfiles({ "test" })
@Slf4j
class SubdomainConfigurationTest extends SubdomainConfiguration {

    @Test
    void testSubdomainConfigurationIsOK() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("/testPath");
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/testHost");
        virtualHost.setPath("/testPath");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
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

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("/testPath");
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/testHost");
        virtualHost.setPath("/testPath");
        virtualHosts.add(virtualHost);

        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyTags() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("/testPath");
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/testHost");
        virtualHost.setPath("/testPath");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyTagName() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("/testPath");
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/testHost");
        virtualHost.setPath("/testPath");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyTagHostname() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("/testPath");
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/testHost");
        virtualHost.setPath("/testPath");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyTagGroup() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(Collections.emptyList());
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("/testPath");
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/testHost");
        virtualHost.setPath("/testPath");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoEntrypoint() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/testHost");
        virtualHost.setPath("/testPath");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyEntrypoint() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/testHost");
        virtualHost.setPath("/testPath");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoEntryPointTarget() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        String vhHost = "testHost";
        virtualHost.setHost(vhHost);
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyEntryPointTarget() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("");
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        String vhHost = "testHost";
        virtualHost.setHost(vhHost);
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoVirtualHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("/testPath");
        entrypoints.add(entrypoint);

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyVirtualHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("/testPath");
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoVHHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("/testPath");
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyVHHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("/testPath");
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        String vhHost = "";
        virtualHost.setHost(vhHost);
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoVirtualHostPath() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("/testPath");
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/testHost");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyVirtualHostPath() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("/testPath");
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/testHost");
        virtualHost.setPath("");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEntrypointNotProtected() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("/unprotectedTestPath");
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/testHost");
        virtualHost.setPath("/otherTestPath");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationVirtualHostIsNotShardingTag() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag();
        publicTag.setName("testPublicTag");
        publicTag.setHostname("/testHost");
        publicTag.setRestrictedGroups(List.of("group1", "group2"));
        shardingTags.add(publicTag);

        List<Entrypoint> entrypoints = new ArrayList<>();
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setTarget("/testPath");
        entrypoints.add(entrypoint);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/otherTestHost");
        virtualHost.setPath("/testPath");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setEntrypoints(entrypoints);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration();
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }
}