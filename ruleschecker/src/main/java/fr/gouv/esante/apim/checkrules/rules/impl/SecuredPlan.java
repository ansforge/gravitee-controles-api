/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Plan;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;


@Component
@Slf4j
public class SecuredPlan extends AbstractRule {

    protected static final String FAILURE_MSG = "Aucun plan ne dispose d'un moyen d’authentification et " +
            "d’identification de l’utilisateur sur cette API";
    protected static final String SUCCESS_MSG = "Plan sécurisé présent sur cette API";


    @Autowired
    public SecuredPlan(RulesRegistry registry) {
        super(registry);
        super.register(this);
    }

    @Override
    public String getName() {
        return "3.1.1 - Paramétrer au moins un moyen d’authentification et d’identification de l’utilisateur";
    }

    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {
        Set<Plan> plans = apiDefinition.getPlans();
        boolean success = verify(plans);
        logResults(apiDefinition.getApiName(), success);
        return new RuleResult(
                getName(),
                success,
                success ? SUCCESS_MSG : FAILURE_MSG
        );
    }

    private boolean verify(Set<Plan> plans) {
        boolean success = false;
        if (plans == null || plans.isEmpty()) {
            return false;
        }
        for (Plan plan : plans) {
            if (Arrays.asList("API_KEY", "OAUTH2").contains(plan.getAuthMechanism())) {
                success = true;
                break;
            }
        }
        return success;
    }
}
