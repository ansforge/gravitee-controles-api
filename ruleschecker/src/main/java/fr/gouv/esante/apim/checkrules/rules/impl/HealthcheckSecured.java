/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import fr.gouv.esante.apim.client.model.PlanEntityGravitee;
import fr.gouv.esante.apim.client.model.PlanSecurityTypeGravitee;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;


@Slf4j
public class HealthcheckSecured implements ApiDefinitionQualityRule {

    protected static final String FAILURE_MSG = "Les plans affectés aux healthchecks de cette API ne sont pas " +
                                                "sécurisés correctement";
    protected static final String SUCCESS_MSG = "Les plans affectés aux healthchecks de cette API sont " +
                                                "correctement sécurisés";


    @Override
    public String getName() {
        return "6.2 - Les endpoints HealthCheck exposés à l’extérieur doivent avoir un plan spécifique, " +
               "dont le nom est suffixé avec « -HealthCheck »";
    }

    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {
        log.info("HealthcheckSecured visit");
        Set<PlanEntityGravitee> plans = apiDefinition.getPlans();
        boolean success = verify(plans);
        return new RuleResult(
                getName(),
                success,
                success ? SUCCESS_MSG : FAILURE_MSG
        );
    }

    private boolean verify(Set<PlanEntityGravitee> plans) {
        if (plans == null || plans.isEmpty()) {
            return false;
        }
        List<PlanEntityGravitee> healthcheckPlans = plans.stream()
                .filter(p -> p.getName().endsWith("-HealthCheck"))
                .filter(p -> p.getSecurity() == PlanSecurityTypeGravitee.KEY_LESS)
                // Une restriction d'accès doit exister...
//                .filter(p -> !Objects.requireNonNull(p.getExcludedGroups()).isEmpty())
//                .filter(p -> !Objects.requireNonNull(p.getSelectionRule()).isEmpty())
                .toList();
        return !healthcheckPlans.isEmpty();
    }
}
