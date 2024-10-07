/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import fr.gouv.esante.apim.client.model.HealthCheckServiceGravitee;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class HealthcheckActivation implements ApiDefinitionQualityRule {

    protected static final String FAILURE_MSG = "Aucun healthcheck n'est actif sur cette API";
    protected static final String SUCCESS_MSG = "Healthcheck correctement activé sur cette API";

    @Override
    public String getName() {
        return "6.1 - Un HealthCheck doit être configuré pour vérifier la disponibilité du Backend de l’API";
    }

    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {
        log.info("HealthcheckActivation visit");
        HealthCheckServiceGravitee healthCheck = apiDefinition.getHealthCheck();
        boolean success = verify(healthCheck);
        return new RuleResult(
                getName(),
                success,
                success ? SUCCESS_MSG : FAILURE_MSG
        );
    }

    private boolean verify(HealthCheckServiceGravitee healthCheck) {
        if (healthCheck == null) {
            return false;
        }
        return Boolean.TRUE.equals(healthCheck.getEnabled());
    }
}
