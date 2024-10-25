/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.test;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.impl.GroupAssignment;
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


@SpringBootTest(classes = {GroupAssignmentTest.class, ApiDefinitionMapper.class, RulesRegistry.class})
@ActiveProfiles({"test"})
@Slf4j
class GroupAssignmentTest extends GroupAssignment {

    @Autowired
    public GroupAssignmentTest(RulesRegistry registry) {
        super(registry);
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGroupAssignementSuccess() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> groups = new HashSet<>();
        groups.add("mon-group");
        apiDef.setGroups(groups);
        GroupAssignment groupRule = new GroupAssignment(new RulesRegistry());
        RuleResult result = apiDef.accept(groupRule);
        log.info(result.getTimestamp());

        assertEquals(super.getName(), result.getRuleName());
        assertTrue(result.isSuccess());
        assertEquals(SUCCESS_MSG, result.getMessage());
    }

    // Verify when groups field is null
    @Test()
    void testGroupIsNull() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        GroupAssignment groupRule = new GroupAssignment(new RulesRegistry());
        RuleResult result = apiDef.accept(groupRule);

        assertFalse(result.isSuccess());
        assertEquals(FAILURE_MSG, result.getMessage());
    }

    @Test
    void testGroupListIsEmpty() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> groups = new HashSet<>();
        apiDef.setGroups(groups);
        GroupAssignment groupRule = new GroupAssignment(new RulesRegistry());
        RuleResult result = apiDef.accept(groupRule);

        assertFalse(result.isSuccess());
    }

    @Test
    void testGroupMultiple() {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        Set<String> groups = new HashSet<>();
        groups.add("1st-group");
        groups.add("2nd-group");
        apiDef.setGroups(groups);
        GroupAssignment groupRule = new GroupAssignment(new RulesRegistry());
        RuleResult result = apiDef.accept(groupRule);

        assertTrue(result.isSuccess());
    }
}