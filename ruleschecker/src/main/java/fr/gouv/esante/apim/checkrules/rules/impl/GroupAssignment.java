/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;


/**
 * Règle d'implémentation d'une API dans l'API Manager
 * Vérifie que l'API est affectée à un groupe d'utilisateur.
 */
@Component
@Slf4j
public class GroupAssignment extends AbstractRule {

    @Autowired
    public GroupAssignment(RulesRegistry registry, MessageProvider messageProvider) {
        super(registry, messageProvider);
        super.register(this);
    }

    @Override
    public String getName() {
        return messageProvider.getMessage("rule.groupassignment.name");
    }

    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {
        Set<String> groups = apiDefinition.getAdminGroups();
        boolean success = verify(groups);
        logResults(apiDefinition.getApiName(), success);
        return new RuleResult(
                getName(),
                success,
                success ? messageProvider.getMessage("rule.groupassignment.msg.success") : messageProvider.getMessage("rule.groupassignment.msg.failure")
        );
    }

    private boolean verify(Set<String> groups) {
        if (groups == null) {
            return false;
        }
        return !groups.isEmpty();
    }
}
