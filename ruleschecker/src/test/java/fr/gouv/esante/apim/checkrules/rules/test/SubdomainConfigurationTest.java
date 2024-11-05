/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.test;

import fr.gouv.esante.apim.checkrules.config.AppTestConfig;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.ShardingTag;
import fr.gouv.esante.apim.checkrules.model.definition.VirtualHost;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.SubdomainConfiguration;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionMapper;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(
        classes = {
            AppTestConfig.class,
            SubdomainConfigurationTest.class,
            ApiDefinitionMapper.class,
            RulesRegistry.class,
            MessageProvider.class
        })
@ActiveProfiles({"test"})
@Slf4j
class SubdomainConfigurationTest extends SubdomainConfiguration {

    @Autowired
    public SubdomainConfigurationTest(RulesRegistry registry, MessageProvider messageProvider) {
        super(registry, messageProvider);
    }

    @Test
    void testSubdomainConfigurationIsOK() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag("testPublicTag");
        publicTag.setEntrypointMappings(List.of("api.gateway.com", "api.gateway.net"));
        shardingTags.add(publicTag);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("api.gateway.com");
        virtualHost.setPath("/test/vhost/path");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.subdomainconfig.msg.success"), result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoTags() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("api.gateway.com");
        virtualHost.setPath("/test/vhost/path");
        virtualHosts.add(virtualHost);

        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                messageProvider.getMessage("rule.subdomainconfig.msg.noshardingtag"),
                System.lineSeparator(),
                ""
        );

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.subdomainconfig.msg.failure") + errorDetails, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyTags() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("testHost");
        virtualHost.setPath("/testPath");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                messageProvider.getMessage("rule.subdomainconfig.msg.noshardingtag"),
                System.lineSeparator(),
                ""
        );

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.subdomainconfig.msg.failure") + errorDetails, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyTagEntrypointMapping() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag("testPublicTag");
        shardingTags.add(publicTag);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("testHost");
        virtualHost.setPath("/testPath");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                messageProvider.getMessage("rule.subdomainconfig.msg.nohostnamemapping"),
                System.lineSeparator()
        );

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.subdomainconfig.msg.failure") + errorDetails, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoVirtualHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag("testPublicTag");
        publicTag.setEntrypointMappings(List.of("api.gateway.com", "api.gateway.net"));
        shardingTags.add(publicTag);

        apiDef.setShardingTags(shardingTags);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                messageProvider.getMessage("rule.subdomainconfig.msg.novhost"),
                System.lineSeparator()
        );

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.subdomainconfig.msg.failure") + errorDetails, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyVirtualHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag("testPublicTag");
        publicTag.setEntrypointMappings(List.of("api.gateway.com", "api.gateway.net"));
        shardingTags.add(publicTag);

        List<VirtualHost> virtualHosts = new ArrayList<>();

        apiDef.setShardingTags(shardingTags);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                messageProvider.getMessage("rule.subdomainconfig.msg.novhost"),
                System.lineSeparator()
        );

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.subdomainconfig.msg.failure") + errorDetails, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoVHHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag("testPublicTag");
        publicTag.setEntrypointMappings(List.of("api.gateway.com", "api.gateway.net"));
        shardingTags.add(publicTag);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                messageProvider.getMessage("rule.subdomainconfig.msg.notvhostmode"),
                System.lineSeparator(),
                System.lineSeparator()
        );

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.subdomainconfig.msg.failure") + errorDetails, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationVirtualHostIsNotShardingTag() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag("testPublicTag");
        publicTag.setEntrypointMappings(List.of("api.gateway.com", "api.gateway.net"));
        shardingTags.add(publicTag);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("api.other.gateway.net");
        virtualHost.setPath("/testPath");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                messageProvider.getMessage("rule.subdomainconfig.msg.novhostmapping"),
                System.lineSeparator(),
                virtualHost.getHost()
        );

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.subdomainconfig.msg.failure") + errorDetails, result.getMessage());
    }
}