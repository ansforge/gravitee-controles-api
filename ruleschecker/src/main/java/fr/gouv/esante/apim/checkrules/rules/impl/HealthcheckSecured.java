/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.definition.Filter;
import fr.gouv.esante.apim.checkrules.model.definition.Flow;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Plan;
import fr.gouv.esante.apim.checkrules.model.definition.Step;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Getter
@Setter
@Slf4j
public class HealthcheckSecured extends AbstractRule {

    protected static final String FAILURE_MSG = "Les plans affectés aux healthchecks de cette API ne sont pas " +
                                                "sécurisés correctement";
    protected static final String SUCCESS_MSG = "Les plans affectés aux healthchecks de cette API sont " +
                                                "correctement sécurisés";
    /**
     * Détails sur la cause de l'échec du contrôle
     */
    private String detailErrorMessage = "";


    @Autowired
    public HealthcheckSecured(RulesRegistry registry) {
        super(registry);
        super.register(this);
    }

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
                success ? SUCCESS_MSG : FAILURE_MSG + detailErrorMessage
        );
    }

    private boolean verify(Set<Plan> plans) {
        // Controle qu'au moins un plan existe
        if (plans == null || plans.isEmpty()) {
            setDetailErrorMessage(" :\nAucun plan n'est associé à cette API");
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
                            if (!flow.getPreSteps().isEmpty()) {
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
                                                } else {
                                                    setDetailErrorMessage(" :\nLe endpoint healthcheck " +
                                                            "ne doit être accessible qu'en GET");
                                                }
                                            } else {
                                                setDetailErrorMessage(" :\nLa whitelist du plan healthcheck " +
                                                        "ne doit autoriser l'accès qu'au endpoit healthcheck");
                                            }
                                        } else {
                                            setDetailErrorMessage(" :\nLa whitelist du plan healthcheck est vide");
                                        }
                                    } else {
                                        setDetailErrorMessage(" :\nLe plan healthcheck doit inclure une restriction" +
                                                " de type Resource Filtering");
                                    }
                                }
                            }
                        }
                    }
                } else {
                    setDetailErrorMessage(" :\nLe type d'authentification du plan healthcheck doit être KEY_LESS");
                }
            } else {
                setDetailErrorMessage(" :\nAucun plan se terminant par -HealthCheck n'est associé à cette API");
            }
        }
        return false;
    }

}
