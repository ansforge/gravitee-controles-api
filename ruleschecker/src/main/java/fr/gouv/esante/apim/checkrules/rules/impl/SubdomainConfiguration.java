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
        List<VirtualHost> virtualHosts = apiDefinition.getVirtualHosts();
        boolean success = verify(shardingTags, virtualHosts, apiDefinition);
        logResults(apiDefinition.getApiName(), success);

        return new RuleResult(
                getName(),
                success,
                success ? SUCCESS_MSG : FAILURE_MSG + detailErrorMessage
        );
    }

    private boolean verify(
            List<ShardingTag> shardingTags,
            List<VirtualHost> virtualHosts,
            GraviteeApiDefinition apiDefinition
    ) {
        // Vérification du mapping des Sharding tags de la définition de l'API du modèle interne
        // On controle que l'API a au moins un sharding tag associé
        if (shardingTags == null || shardingTags.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Entrypoint ep : apiDefinition.getEntrypoints()) {
                sb.append("targetHostName [ ");
                sb.append(ep.getTargetHostName());
                sb.append(" ] pour le Vhost [ ");
                sb.append(ep.getHost());
                sb.append(" ].");
            }
            log.error("Aucun sharding tag n'est associé à cette API, le default entrypoint {} est utilisé", sb);
            setDetailErrorMessage(String.format(
                    " : %sAucun sharding tag n'est associé à cette API, le default entrypoint est utilisé : %s",
                    System.lineSeparator(),
                    sb
            ));
            return false;
        }
        // On controle que pour chaque sharding tag associé à l'API, tous les attributs sont renseignés
        for (ShardingTag shardingTag : shardingTags) {
            if (shardingTag.getEntrypointMappings() == null || shardingTag.getEntrypointMappings().isEmpty()) {
                log.error("Un sharding tag associé à cette API n'a aucun hostname défini dans le mapping");
                setDetailErrorMessage(String.format(
                        " :%sUn sharding tag associé à cette API n'a aucun hostname défini dans le mapping",
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
            // Si on est en mode context-path le host est nul
            if (virtualHost.getHost() == null) {
                log.error("Cette API n'est pas configurée en mode Virtual Host");
                setDetailErrorMessage(String.format(
                        " :%sCette API n'est pas configurée en mode Virtual Host",
                        System.lineSeparator()
                ));
                return false;
                // On vérifie si le virtual host correspond à un de ceux associés aux sharding tags de l'API
            } else {
                boolean isMapped = shardingTags.stream()
                        .flatMap(shardingTag -> shardingTag.getEntrypointMappings().stream())
                        .anyMatch(entrypoint -> entrypoint.equals(virtualHost.getHost()));

                if (!isMapped) {
                    log.error("Le virtual host {} n'est pas mappé dans les entrypoint mappings", virtualHost.getHost());
                    setDetailErrorMessage(String.format(
                            " :%sLe virtual host %s n'est pas mappé dans les entrypoint mappings",
                            System.lineSeparator(),
                            virtualHost.getHost()
                    ));
                    return false;
                }
            }
            if (virtualHost.getPath() == null || virtualHost.getPath().isEmpty()) {
                log.error("Le virtual host {} de cette API ne protège aucun path", virtualHost.getHost());
                setDetailErrorMessage(String.format(
                        " :%sLe virtual host %s de cette API ne protège aucun path",
                        System.lineSeparator(),
                        virtualHost.getHost()
                ));
                return false;
            }
        }
        return true;
    }
}
