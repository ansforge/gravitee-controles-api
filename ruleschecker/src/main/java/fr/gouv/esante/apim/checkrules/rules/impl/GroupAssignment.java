package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;

import lombok.extern.slf4j.Slf4j;
import java.util.Set;


/**
 * Règle d'implémentation d'une API dans l'API Manager
 * Vérifie que l'API est affectée à un groupe d'utilisateur.
 */
@Slf4j
public class GroupAssignment implements ApiDefinitionQualityRule {

    protected static final String FAILURE_MSG = "Aucun groupe d'administration n'est affecté à cette API";
    protected static final String SUCCESS_MSG = "Groupe d'administration présent";

    @Override
    public String getName() {
        return "2.1 - L’API doit être affectée à un groupe d’administration";
    }

    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {
        log.info("GroupAssignment visit");
        Set<String> groups = apiDefinition.getGroups();
        boolean success = verify(groups);
        return new RuleResult(
                getName(),
                success,
                success ? SUCCESS_MSG : FAILURE_MSG
        );
    }

    private boolean verify(Set<String> groups) {
        if(groups == null) {
            return false;
        }
        return !groups.isEmpty();
    }
}
