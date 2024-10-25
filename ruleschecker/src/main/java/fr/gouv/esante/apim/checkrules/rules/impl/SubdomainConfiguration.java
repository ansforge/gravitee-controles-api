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
        List<ShardingTag> shardingTags = apiDefinition.getShardingTags();
        List<Entrypoint> entrypoints = apiDefinition.getEntrypoints();
        List<VirtualHost> virtualHosts = apiDefinition.getVirtualHosts();
        boolean success = verify(shardingTags, entrypoints, virtualHosts);
        logResults(apiDefinition.getApiName(), success);

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
        // Vérification du mapping des Sharding tags de la définition de l'API du modèle interne
        // On controle que l'API a au moins un sharding tag associé
        if (shardingTags == null || shardingTags.isEmpty()) {
            log.error("Aucun sharding tag n'est associé à cette API");
            setDetailErrorMessage(String.format(
                    " : %sAucun sharding tag n'est associé à cette API",
                    System.lineSeparator()
            ));
            return false;
        }
        // On controle que pour chaque sharding tag associé à l'API, tous les attributs sont renseignés
        for (ShardingTag shardingTag : shardingTags) {
            if (shardingTag.getName() == null || shardingTag.getName().isEmpty()) {
                log.error("Un sharding tag associé à cette API n'a pas de nom");
                setDetailErrorMessage(String.format(
                        " :%sUn sharding tag associé à cette API n'a pas de nom",
                        System.lineSeparator()
                ));
                return false;
            }
            if (shardingTag.getRestrictedGroups() == null || shardingTag.getRestrictedGroups().isEmpty()) {
                log.error("Un sharding tag associé à cette API n'a pas de groupe d'accès associé");
                setDetailErrorMessage(String.format(
                        " :%sUn sharding tag associé à cette API n'a pas de groupe d'accès associé",
                        System.lineSeparator()
                ));
                return false;
            }
            if (shardingTag.getEntrypointMappings() == null || shardingTag.getEntrypointMappings().isEmpty()) {
                log.error("Un sharding tag associé à cette API n'a aucun mapping vers un entrypoint");
                setDetailErrorMessage(String.format(
                        " :%sUn sharding tag associé à cette API n'a aucun mapping vers un entrypoint",
                        System.lineSeparator()
                ));
                return false;
            }

        }
        // Vérification du mapping des Sharding tags de la définition de l'API du modèle interne
        // On controle que l'API a au moins un entrypoint de son backend
        if (entrypoints == null || entrypoints.isEmpty()) {
            log.error("Aucun entrypoint du backend n'est associé à cette API");
            setDetailErrorMessage(String.format(
                    " :%sAucun entrypoint du backend n'est associé à cette API",
                    System.lineSeparator()
            ));
            return false;
        }
        // On controle que pour chaque entrypoint du backend de l'API, tous les attributs sont renseignés
        for (Entrypoint entrypoint : entrypoints) {
            if (entrypoint.getHost() == null || entrypoint.getHost().isEmpty()) {
                log.error("Un entrypoint du backend de cette API n'a pas de host");
                setDetailErrorMessage(String.format(
                        " :%sUn entrypoint du backend de cette API n'a pas de host",
                        System.lineSeparator()
                ));
                return false;
            }
            if (entrypoint.getTarget() == null || entrypoint.getTarget().isEmpty()) {
                log.error("Un entrypoint du backend de cette API n'a pas de target");
                setDetailErrorMessage(String.format(
                        " :%sUn entrypoint du backend de cette API n'a pas de target",
                        System.lineSeparator()
                ));
                return false;
            }
        }

        // On controle que l'API est en mode virtual host et que ceux-ci sont bien définis
        if (virtualHosts == null || virtualHosts.isEmpty()) {
            log.error("Aucun virtual host n'est associé à cette API");
            setDetailErrorMessage(String.format(
                    " :%sAucun virtual host n'est associé à cette API",
                    System.lineSeparator()
            ));
            return false;
        }
        for (VirtualHost virtualHost : virtualHosts) {
            if (virtualHost.getHost() == null || virtualHost.getHost().isEmpty()) {
                log.error("Un virtual host de cette API n'a pas de host associé");
                setDetailErrorMessage(String.format(
                        " :%sUn virtual host de cette API n'a pas de host associé",
                        System.lineSeparator()
                ));
                return false;
            }
            if (virtualHost.getPath() == null || virtualHost.getPath().isEmpty()) {
                log.error("Un virtual host de cette API ne protège aucun path");
                setDetailErrorMessage(String.format(
                        " :%sUn virtual host de cette API ne protège aucun path",
                        System.lineSeparator()
                ));
                return false;
            }
        }

        // On vérifie si tous les hosts des virtual hosts correspondent à ceux associés aux sharding tags de l'API
        List<String> virtualHostListeningHosts = virtualHosts.stream().map(VirtualHost::getHost).toList();
        for (String listeningHost : virtualHostListeningHosts) {
            boolean found = false;
            for (ShardingTag tag : shardingTags) {
                List<String> domains = new ArrayList<>();
                for (String entrypointUrl : tag.getEntrypointMappings()) {
                    domains.add(entrypointUrl.replaceAll("http(s)?://|www\\.|/.*", ""));
                }
                if (domains.contains(listeningHost)) {
                    found = true;
                    log.debug("Correspondance vérifiée entre virtualhost {} et sharding tag {} : {} inclus dans {}",
                            listeningHost,
                            tag.getName(),
                            listeningHost,
                            domains
                    );
                }
            }
            if (!found) {
                log.error("Le virtual host {} ne correspond à aucun sharding tag de cette API", listeningHost);
                setDetailErrorMessage(String.format(
                        " :%sLe virtual host %s ne correspond à aucun sharding tag de cette API",
                        System.lineSeparator(),
                        listeningHost
                ));
                return false;
            }
        }
        return true;
    }
}
