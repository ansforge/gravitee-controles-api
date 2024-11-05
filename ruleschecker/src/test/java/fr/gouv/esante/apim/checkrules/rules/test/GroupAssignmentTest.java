/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.test;

import fr.gouv.esante.apim.checkrules.config.AppTestConfig;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.GroupAssignment;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionMapper;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
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
class GroupAssignmentTest extends GroupAssignment {

    @Autowired
    public GroupAssignmentTest(RulesRegistry registry, MessageProvider messageProvider) {
        super(registry, messageProvider);
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGroupAssignementSuccess() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> groups = new HashSet<>();
        groups.add("mon-group");
        apiDef.setAdminGroups(groups);
        GroupAssignment groupRule = new GroupAssignment(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(groupRule);
        log.info(result.getTimestamp());

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.groupassignment.msg.success"), result.getMessage());
    }

    // Verify when groups field is null
    @Test()
    void testGroupIsNull() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        GroupAssignment groupRule = new GroupAssignment(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(groupRule);

        assertFalse(result.isSuccess());
        assertEquals(messageProvider.getMessage("rule.groupassignment.msg.failure"), result.getMessage());
    }

    @Test
    void testGroupListIsEmpty() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> groups = new HashSet<>();
        apiDef.setAdminGroups(groups);
        GroupAssignment groupRule = new GroupAssignment(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(groupRule);

        assertFalse(result.isSuccess());
    }

    @Test
    void testGroupMultiple() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> groups = new HashSet<>();
        groups.add("1st-group");
        groups.add("2nd-group");
        apiDef.setAdminGroups(groups);
        GroupAssignment groupRule = new GroupAssignment(new RulesRegistry(), messageProvider);
        RuleResult result = apiDef.accept(groupRule);

        assertTrue(result.isSuccess());
    }
}