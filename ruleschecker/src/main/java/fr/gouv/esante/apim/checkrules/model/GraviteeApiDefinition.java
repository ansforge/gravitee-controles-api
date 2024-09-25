package fr.gouv.esante.apim.checkrules.model;

import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import fr.gouv.esante.apim.client.model.ApiEntrypointEntityGravitee;
import fr.gouv.esante.apim.client.model.PlanEntityGravitee;

import fr.gouv.esante.apim.client.model.ProxyGravitee;
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
    String description;
    String version;
    Set<PlanEntityGravitee> plans;
    Set<String> categories;
    List<ApiEntrypointEntityGravitee> entrypoints;
    Set<String> groups;
    ProxyGravitee proxy;
    List<VirtualHostGravitee> virtualHosts;
    String host;
    Set<String> tags;

    public RuleResult accept(ApiDefinitionQualityRule rule) {
        return rule.visit(this);
    }
}
