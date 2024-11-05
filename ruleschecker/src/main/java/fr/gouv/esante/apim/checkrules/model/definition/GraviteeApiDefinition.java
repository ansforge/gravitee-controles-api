/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model.definition;

import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
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
    Set<String> adminGroups = new HashSet<>();
    List<Group> groups = new ArrayList<>();
    Set<Plan> plans = new HashSet<>();
    List<ShardingTag> shardingTags = new ArrayList<>();
    List<Entrypoint> entrypoints = new ArrayList<>();
    List<VirtualHost> virtualHosts = new ArrayList<>();
    HealthCheckService healthCheck;
    Logging logging;


    public RuleResult accept(ApiDefinitionQualityRule rule) {
        return rule.visit(this);
    }
}
