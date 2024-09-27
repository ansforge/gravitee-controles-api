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

import java.util.Set;

@Slf4j
public class SecuredPlan implements ApiDefinitionQualityRule {

    protected static final String FAILURE_MSG = "Aucun plan ne dispose d'un moyen d’authentification et " +
            "d’identification de l’utilisateur sur cette API";
    protected static final String SUCCESS_MSG = "Plan sécurisé présent sur cette API";


    @Override
    public String getName() {
        return "3.1.1 - Paramétrer au moins un moyen d’authentification et d’identification de l’utilisateur";
    }

    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {
        log.info("SecuredPlan visit");
        Set<PlanEntityGravitee> plans = apiDefinition.getPlans();
        boolean success = verify(plans);
        return new RuleResult(
                getName(),
                success,
                success ? SUCCESS_MSG : FAILURE_MSG
        );
    }

    private boolean verify(Set<PlanEntityGravitee> plans) {
        boolean success = false;
        if (plans == null || plans.isEmpty()) {
            return false;
        }
        for (PlanEntityGravitee plan : plans) {
            if (plan.getSecurity() == PlanSecurityTypeGravitee.API_KEY
                    || plan.getSecurity() == PlanSecurityTypeGravitee.OAUTH2) {
                success = true;
                break;
            }
        }
        return success;
    }
}
