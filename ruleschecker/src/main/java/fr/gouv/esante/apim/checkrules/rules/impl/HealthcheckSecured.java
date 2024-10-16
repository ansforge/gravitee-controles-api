/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.definition.Filter;
import fr.gouv.esante.apim.checkrules.model.definition.Flow;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Plan;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.model.definition.Step;
import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
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
        Set<Plan> plans = apiDefinition.getPlans();
        boolean success = verify(plans);
        return new RuleResult(
                getName(),
                success,
                success ? SUCCESS_MSG : FAILURE_MSG
        );
    }

    private boolean verify(Set<Plan> plans) {
        // Controle qu'au moins un plan existe
        if (plans == null || plans.isEmpty()) {
            return false;
        }

        // Controle qu'au moins un plan contient -HealthCheck dans son nom
        for (Plan plan : plans) {
            if (plan.getName().toLowerCase().contains("-healthcheck")) {
                // Controle le type d'authentification qui doit etre KEY_LESS pour un plan healthcheck
                if ("KEY_LESS".equals(plan.getAuthMechanism())) {
                    // On cherche un flow unique
                    if (plan.getFlows().size() == 1) {
                        // On cherche un flow contenant au moins un pre-step
                        for (Flow flow : plan.getFlows()) {
                            if (!flow.getPreSteps().isEmpty()) {//
                                // On cherche un pre-step ayant une policy ressource-filtering
                                for (Step step : flow.getPreSteps()) {
                                    if ("resource-filtering".equals(step.getPolicy())) {
                                        // On vérifie qu'il y a une whitelist dans la configuration
                                        List<Filter> whitelist = step.getConfiguration().getWhitelist();
                                        if (whitelist != null && !whitelist.isEmpty()) {
                                            // On controle que la whitelist ne contient qu'un seul endpoint
                                            // accessible par une seule méthode
                                            if (whitelist.size() == 1 && whitelist.get(0).getMethods().size() == 1) {
                                                // On controle que la seule méthode HTTP accessible est GET
                                                if ("GET".equals(whitelist.get(0).getMethods().get(0))) {
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
//        // Controle qu'au moins un plan contient -HealthCheck dans son nom
//        for (Plan plan : plans) {
//            if (plan.getName().toLowerCase().contains("healthcheck")) {
//                // Controle le type d'authentification qui doit etre KEY_LESS pour un plan healthcheck
//                if (plan.getAuthMechanism().equals("KEY_LESS")) {
//                    // On cherche un flow unique
//                    if (plan.getFlows().size() == 1) {
//                        // On cherche un flow contenant une policy ressource-filtering
//                        for (Flow flow : plan.getFlows()) {
//                            if (flow.getPolicy().equals("resource-filtering")) {
//                                // On vérifie qu'il y a une whitelist dans la configuration du flow
//                                List<Filter> whitelist = flow.getConfiguration().getWhitelist();
//                                if (whitelist != null && !whitelist.isEmpty()) {
//                                    // On controle que la whitelist ne contient qu'un seul endpoint
//                                    // accessible par une seule méthode
//                                    if (whitelist.size() == 1 && whitelist.get(0).getMethods().size() == 1) {
//                                        // On controle que la seule méthode HTTP accessible est GET
//                                        if (whitelist.get(0).getMethods().get(0).equals("GET")) {
//                                            return true;
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
        return false;
    }

}
