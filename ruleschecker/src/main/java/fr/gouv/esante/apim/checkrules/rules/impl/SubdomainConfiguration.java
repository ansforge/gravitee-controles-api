/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.Entrypoint;
import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.RuleResult;
import fr.gouv.esante.apim.checkrules.model.VirtualHost;
import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;


@Slf4j
public class SubdomainConfiguration implements ApiDefinitionQualityRule {

    protected static final String FAILURE_MSG = "Erreur dans la configuration de sous-domaine d'accès à cette API";
    protected static final String SUCCESS_MSG = "Sous-domaine d'accès à cette API correctement configuré";

    @Override
    public String getName() {
        return "3.3 - Un sous-domaine spécifique doit être configuré pour accéder à l’API";
    }

    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {
        log.info("SubdomainConfiguration visit");
        Set<String> tags = apiDefinition.getTags();
        List<Entrypoint> entrypoints = apiDefinition.getEntrypoints();
        List<VirtualHost> virtualHosts = apiDefinition.getVirtualHosts();
        boolean success = verify(tags, entrypoints, virtualHosts);

        return new RuleResult(
                getName(),
                success,
                success ? SUCCESS_MSG : FAILURE_MSG
        );
    }

    private boolean verify(
            Set<String> tags,
            List<Entrypoint> entrypoints,
            List<VirtualHost> virtualHosts
    ) {
        if (tags == null || tags.isEmpty()) {
            return false;
        }

        if (entrypoints == null || entrypoints.isEmpty()) {
            return false;
        }
        for (Entrypoint entrypoint : entrypoints) {
            if (entrypoint.getHost() == null || entrypoint.getHost().isEmpty()) {
                return false;
            }
        }

        if (virtualHosts == null || virtualHosts.isEmpty()) {
            return false;
        }
        for (VirtualHost virtualHost : virtualHosts) {
            if (virtualHost.getHost() == null || virtualHost.getHost().isEmpty()) {
                return false;
            }
        }

        List<String> epHosts = entrypoints.stream().map(Entrypoint::getHost).toList();
        List<String> vhHosts = virtualHosts.stream().map(VirtualHost::getHost).toList();
        List<String> epOnlyHosts = epHosts.stream().filter(epHost -> !vhHosts.contains(epHost)).toList();
        List<Entrypoint> entrypointsWithoutVirtualHost = entrypoints.stream()
                .filter(ep -> epOnlyHosts.contains(ep.getHost())).toList();

        return entrypointsWithoutVirtualHost.isEmpty();
    }
}
