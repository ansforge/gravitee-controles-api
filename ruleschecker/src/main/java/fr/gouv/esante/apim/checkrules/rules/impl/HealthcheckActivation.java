/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.gouv.esante.apim.checkrules.model.definition.Endpoint;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.Group;
import fr.gouv.esante.apim.checkrules.model.definition.HealthCheckService;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;

@Component
public class HealthcheckActivation extends AbstractRule {

    @Autowired
    public HealthcheckActivation(RulesRegistry registry, MessageProvider messageProvider) {
        super(registry, messageProvider);
        super.register(this);
    }

    @Override
    public String getName() {
        return messageProvider.getMessage("rule.healthcheckactivation.name");
    }

    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {

        boolean success = verify(apiDefinition);
        logResults(apiDefinition.getApiName(), success);
        return new RuleResult(
                getName(),
                success,
                success ? messageProvider.getMessage("rule.healthcheckactivation.msg.success") : messageProvider.getMessage("rule.healthcheckactivation.msg.failure")
        );
    }

    private boolean verify(GraviteeApiDefinition apiDefinition) {
        HealthCheckService healthCheck = apiDefinition.getHealthCheck();
        // On vérifie si le healthcheck global est activé
        if (apiDefinition.getHealthCheck() != null && healthCheck.isEnabled()) {
            return true;
        }
        // Si le healthcheck global n'est pas activé, on vérifie si tous les groupes de endpoints en ont un activé
        for (Group group : apiDefinition.getGroups()) {
            HealthCheckService healthCheckService = group.getHealthCheckService();
            if (healthCheckService == null || !group.getHealthCheckService().isEnabled()) {
                // Si il n'y a pas de healthcheck au niveau du groupe de endpoints, on vérifie si tous les endpoints
                // du groupe ont un healthcheck dédié activé
                for (Endpoint endpoint : group.getEndpoints()) {
                    if (!endpoint.getHealthCheckService().isEnabled()) {
                        return false;
                    }
                }
            }
        }
        if (apiDefinition.getHealthCheck() == null) return false;
        return !apiDefinition.getGroups().isEmpty();
    }
}
