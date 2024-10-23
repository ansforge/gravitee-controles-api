/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import fr.gouv.esante.apim.checkrules.model.definition.Entrypoint;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.ShardingTag;
import fr.gouv.esante.apim.checkrules.model.definition.VirtualHost;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@Getter
@Setter
@Slf4j
public class SubdomainConfiguration extends AbstractRule {

    protected static final String FAILURE_MSG = "Erreur dans la configuration de sous-domaine d'accès à cette API";
    protected static final String SUCCESS_MSG = "Sous-domaine d'accès à cette API correctement configuré";
    /**
     * Détails sur la cause de l'échec du contrôle
     */
    private String detailErrorMessage = "";

    @Autowired
    public SubdomainConfiguration(RulesRegistry registry) {
        super(registry);
        super.register(this);
    }

    @Override
    public String getName() {
        return "3.3 - Un sous-domaine spécifique doit être configuré pour accéder à l’API";
    }


    @Override
    public RuleResult visit(GraviteeApiDefinition apiDefinition) {
        log.info("SubdomainConfiguration visit");
        List<ShardingTag> shardingTags = apiDefinition.getShardingTags();
        List<Entrypoint> entrypoints = apiDefinition.getEntrypoints();
        List<VirtualHost> virtualHosts = apiDefinition.getVirtualHosts();
        boolean success = verify(shardingTags, entrypoints, virtualHosts);

        return new RuleResult(
                getName(),
                success,
                success ? SUCCESS_MSG : FAILURE_MSG + detailErrorMessage
        );
    }

    private boolean verify(
            List<ShardingTag> shardingTags,
            List<Entrypoint> entrypoints,
            List<VirtualHost> virtualHosts
    ) {
        if (shardingTags == null || shardingTags.isEmpty()) {
            setDetailErrorMessage(" :\nAucun sharding tag n'est associé à cette API");
            return false;
        }
        for (ShardingTag shardingTag : shardingTags) {
            if (shardingTag.getName() == null || shardingTag.getName().isEmpty()) {
                setDetailErrorMessage(" :\nUn sharding tag associé à cette API n'a pas de nom");
                return false;
            }
            if (shardingTag.getHostname() == null || shardingTag.getHostname().isEmpty()) {
                setDetailErrorMessage(" :\nUn sharding tag associé à cette API n'a pas de domaine associé");
                return false;
            }
            if (shardingTag.getRestrictedGroups() == null || shardingTag.getRestrictedGroups().isEmpty()) {
                setDetailErrorMessage(" :\nUn sharding tag associé à cette API n'a pas de restriction d'accès");
                return false;
            }
        }

        if (entrypoints == null || entrypoints.isEmpty()) {
            setDetailErrorMessage(" :\nAucun entrypoint n'est associé à cette API");
            return false;
        }
        for (Entrypoint entrypoint : entrypoints) {
            if (entrypoint.getTarget() == null || entrypoint.getTarget().isEmpty()) {
                setDetailErrorMessage(" :\nUn entrypoint de cette API n'a pas de cible");
                return false;
            }
        }

        if (virtualHosts == null || virtualHosts.isEmpty()) {
            setDetailErrorMessage(" :\nAucun virtual host n'est associé à cette API");
            return false;
        }
        for (VirtualHost virtualHost : virtualHosts) {
            if (virtualHost.getHost() == null || virtualHost.getHost().isEmpty()) {
                setDetailErrorMessage(" :\nUn virtual host de cette API n'a pas de domaine associé");
                return false;
            }
            if (virtualHost.getPath() == null || virtualHost.getPath().isEmpty()) {
                setDetailErrorMessage(" :\nUn virtual host de cette API ne protège aucun path");
                return false;
            }
        }

        // On controle que chaque entrypoint est associé à un virtual host
        List<String> virtualHostListeningPaths = virtualHosts.stream().map(VirtualHost::getPath).toList();
        for (Entrypoint entrypoint : entrypoints) {
            // La cible de l'entrypoint doit être le path protégé d'un des virtual host
            if (!virtualHostListeningPaths.contains(entrypoint.getTarget())) {
                setDetailErrorMessage(" :\nUn des entrypoint de l'API n'est pas protégé par un virtual host :\n" +
                        entrypoint.getTarget());
                return false;
            }
            // On vérifie que le host de chaque virtual host correspond au host d'un sharding tag de l'API
            for (VirtualHost vhost : virtualHosts) {
                // On cherche le virtual host correspondant à l'entrypoint
                if (vhost.getPath().equals(entrypoint.getTarget())) {
                    String host = vhost.getHost();
                    List<ShardingTag> tagsForHost = new ArrayList<>();
                    // On cherche les sharding tags qui ont le même host que le virtual host
                    for (ShardingTag t : shardingTags) {
                        if (t.getHostname().equals(host)) {
                            tagsForHost.add(t);
                        }
                    }
                    // On controle que le host du virtual host est associé à au moins un sharding tag de l'API
                    if (tagsForHost.isEmpty()) {
                        setDetailErrorMessage(" :\nLe domaine du sharding tag " +
                                "ne correspond pas à celui du virtual host");
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
