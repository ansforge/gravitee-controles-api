/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model;

import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import fr.gouv.esante.apim.client.model.ApiEntrypointEntityGravitee;
import fr.gouv.esante.apim.client.model.HealthCheckServiceGravitee;
import fr.gouv.esante.apim.client.model.LoggingGravitee;
import fr.gouv.esante.apim.client.model.PlanEntityGravitee;

import fr.gouv.esante.apim.client.model.VirtualHostGravitee;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;


/**
 * Représentation simplifiée d'une API definition récupérée sur l'API Manager
 * Ne contient que les données utiles au contrôle des règles d'implémentation
 */
@Slf4j
@Getter
@Setter
public class GraviteeApiDefinition {

    String apiName;
    Set<String> groups;
    Set<PlanEntityGravitee> plans;
    Set<String> tags;
    List<ApiEntrypointEntityGravitee> entrypoints;
    List<VirtualHostGravitee> virtualHosts;
    HealthCheckServiceGravitee healthCheck;
    LoggingGravitee logging;


    public RuleResult accept(ApiDefinitionQualityRule rule) {
        return rule.visit(this);
    }
}
