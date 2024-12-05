/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.gouv.esante.apim.checkrules.model.definition.Filter;
import fr.gouv.esante.apim.checkrules.model.definition.Flow;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Plan;
import fr.gouv.esante.apim.checkrules.model.definition.Step;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class HealthcheckSecured extends AbstractRule {

    /**
     * Détails sur la cause de l'échec du contrôle
     */
    private String detailErrorMessage = "";


    @Autowired
    public HealthcheckSecured(RulesRegistry registry, MessageProvider messageProvider) {
        super(registry, messageProvider);
        super.register(this);
    }

    @Override
    public String getName() {
        return messageProvider.getMessage("rule.healthchecksecured.name");
    }

    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {
        Set<Plan> plans = apiDefinition.getPlans();
        boolean success = verify(plans);
        logResults(apiDefinition.getApiName(), success);
        return new RuleResult(
                getName(),
                success,
                success ? messageProvider.getMessage("rule.healthchecksecured.msg.success") : messageProvider.getMessage("rule.healthchecksecured.msg.failure") + detailErrorMessage
        );
    }

    private boolean verify(Set<Plan> plans) {
        // Controle qu'au moins un plan existe
        if (plans == null || plans.isEmpty()) {
            setDetailErrorMessage(String.format(messageProvider.getMessage("rule.healthchecksecured.msg.detail.noplan"), System.lineSeparator()));
            return false;
        }

        // Controle qu'au moins un plan contient -HealthCheck dans son nom, est en mode de sécurité KEYLESS
        // et contient un unique flow paramétré
        for (Plan plan : plans) {
            if (isValidHealthcheckPlan(plan)) {
                //On cherche un flow contenant au moins un pre-step
                for (Flow flow : plan.getFlows()) {
                    // On cherche un pre-step ayant une policy ressource-filtering correctement configurée,
                    // i.e. contenant une whitelist qui autorise l'accès à un endpoint unique en GET uniquement
                    if (flow.getPreSteps().stream().anyMatch(this::isRessourceFiltering)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isValidHealthcheckPlan(Plan plan) {
        // Controle que le plan contient -HealthCheck dans son nom
        if (!plan.getName().toLowerCase().contains("-healthcheck")) {
            setDetailErrorMessage(String.format(messageProvider.getMessage("rule.healthchecksecured.msg.detail.nohealthcheckplan"),
                    System.lineSeparator())
            );
            return false;
        }
        // Controle le type d'authentification qui doit etre KEY_LESS pour un plan healthcheck
        if (!"KEY_LESS".equals(plan.getAuthMechanism())) {
            setDetailErrorMessage(String.format(messageProvider.getMessage("rule.healthchecksecured.msg.detail.authmechanismnotallowed"),
                    System.lineSeparator())
            );
            return false;
        }
        // On cherche un flow unique
        return plan.getFlows().size() == 1;
    }

    private boolean isRessourceFiltering(Step step) {
        if (!"resource-filtering".equals(step.getPolicy())) {
            setDetailErrorMessage(String.format(
                    messageProvider.getMessage("rule.healthchecksecured.msg.detail.noresourcefiltering"),
                    System.lineSeparator())
            );
            return false;
        }

        // On vérifie qu'il y a une whitelist dans la configuration
        List<Filter> whitelist = step.getConfiguration().getWhitelist();
        if (whitelist == null || whitelist.isEmpty()) {
            setDetailErrorMessage(String.format(
                    messageProvider.getMessage("rule.healthchecksecured.msg.detail.emptywhitelist"),
                    System.lineSeparator())
            );
            return false;
        }

        // On controle que la whitelist ne contient qu'un seul endpoint
        // accessible par une seule méthode
        if (whitelist.size() != 1 || whitelist.get(0).getMethods().size() != 1) {
            setDetailErrorMessage(String.format(
                    messageProvider.getMessage("rule.healthchecksecured.msg.detail.accessunauthorized"),
                    System.lineSeparator())
            );
            return false;
        }

        // On controle que la seule méthode HTTP accessible est GET
        if (!"GET".equals(whitelist.get(0).getMethods().get(0))) {
            setDetailErrorMessage(String.format(
                    messageProvider.getMessage("rule.healthchecksecured.msg.detail.methodnotallowed"),
                    System.lineSeparator(),
                    whitelist.get(0).getMethods().get(0))
            );
            return false;
        }

        return true;
    }
}
