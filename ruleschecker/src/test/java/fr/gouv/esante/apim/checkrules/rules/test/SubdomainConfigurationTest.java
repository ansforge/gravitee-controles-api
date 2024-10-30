/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.test;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.ShardingTag;
import fr.gouv.esante.apim.checkrules.model.definition.VirtualHost;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.SubdomainConfiguration;
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


@SpringBootTest(classes = {SubdomainConfigurationTest.class, ApiDefinitionMapper.class, RulesRegistry.class})
@ActiveProfiles({"test"})
@Slf4j
class SubdomainConfigurationTest extends SubdomainConfiguration {

    @Autowired
    public SubdomainConfigurationTest(RulesRegistry registry) {
        super(registry);
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

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry());
        RuleResult result = apiDef.accept(subdomainConfiguration);

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(SUCCESS_MSG, result.getMessage());
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

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry());
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                " : %sAucun sharding tag n'est associé à cette API",
                System.lineSeparator()
        );

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
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

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry());
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                " : %sAucun sharding tag n'est associé à cette API",
                System.lineSeparator()
        );

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyTagName() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag("");
        publicTag.setEntrypointMappings(List.of("api.gateway.com", "api.gateway.net"));
        shardingTags.add(publicTag);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("testHost");
        virtualHost.setPath("/testPath");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry());
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                " :%sUn sharding tag associé à cette API n'a pas de nom",
                System.lineSeparator()
        );

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
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

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry());
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                " :%sUn sharding tag associé à cette API n'a aucun mapping vers un entrypoint",
                System.lineSeparator()
        );

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoVirtualHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag("testPublicTag");
        publicTag.setEntrypointMappings(List.of("api.gateway.com", "api.gateway.net"));
        shardingTags.add(publicTag);

        apiDef.setShardingTags(shardingTags);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry());
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                " :%sAucun virtual host n'est associé à cette API",
                System.lineSeparator()
        );

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
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

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry());
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                " :%sAucun virtual host n'est associé à cette API",
                System.lineSeparator()
        );

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
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

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry());
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                " :%sUn virtual host de cette API n'a pas de host associé",
                System.lineSeparator()
        );

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyVHHost() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag("testPublicTag");
        publicTag.setEntrypointMappings(List.of("api.gateway.com", "api.gateway.net"));
        shardingTags.add(publicTag);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        String vhHost = "";
        virtualHost.setHost(vhHost);
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry());
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                " :%sUn virtual host de cette API n'a pas de host associé",
                System.lineSeparator()
        );

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationNoVirtualHostPath() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag("testPublicTag");
        publicTag.setEntrypointMappings(List.of("api.gateway.com", "api.gateway.net"));
        shardingTags.add(publicTag);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/testHost");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry());
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                " :%sUn virtual host de cette API ne protège aucun path",
                System.lineSeparator()
        );

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
    }

    @Test
    void testSubdomainConfigurationEmptyVirtualHostPath() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        List<ShardingTag> shardingTags = new ArrayList<>();
        ShardingTag publicTag = new ShardingTag("testPublicTag");
        publicTag.setEntrypointMappings(List.of("api.gateway.com", "api.gateway.net"));
        shardingTags.add(publicTag);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost("/testHost");
        virtualHost.setPath("");
        virtualHosts.add(virtualHost);

        apiDef.setShardingTags(shardingTags);
        apiDef.setVirtualHosts(virtualHosts);

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry());
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                " :%sUn virtual host de cette API ne protège aucun path",
                System.lineSeparator()
        );

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
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

        SubdomainConfiguration subdomainConfiguration = new SubdomainConfiguration(new RulesRegistry());
        RuleResult result = apiDef.accept(subdomainConfiguration);
        String errorDetails = String.format(
                " :%sLe virtual host %s ne correspond à aucun sharding tag de cette API",
                System.lineSeparator(),
                virtualHost.getHost()
        );

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG + errorDetails, result.getMessage());
    }
}