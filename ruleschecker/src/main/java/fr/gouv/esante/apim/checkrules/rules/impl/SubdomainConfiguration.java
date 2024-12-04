/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.rules.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.definition.ShardingTag;
import fr.gouv.esante.apim.checkrules.model.definition.VirtualHost;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import fr.gouv.esante.apim.checkrules.services.MessageProvider;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesRegistry;
import lombok.Getter;
import lombok.Setter;


@Component
@Getter
@Setter
public class SubdomainConfiguration extends AbstractRule {

    /**
     * Détails sur la cause de l'échec du contrôle
     */
    private String detailErrorMessage = "";

    @Autowired
    public SubdomainConfiguration(RulesRegistry registry, MessageProvider messageProvider) {
        super(registry, messageProvider);
        super.register(this);
    }

    @Override
    public String getName() {
        return messageProvider.getMessage("rule.subdomainconfig.name");
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
                success ? messageProvider.getMessage("rule.subdomainconfig.msg.success") : messageProvider.getMessage("rule.subdomainconfig.msg.failure") + detailErrorMessage
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
            apiDefinition.getEntrypoints().forEach(ep ->
                    sb.append("targetHostName [ ")
                    .append(ep.getTargetHostName())
                    .append(" ] pour le Vhost [ ")
                    .append(ep.getHost()).append(" ]."));
            setDetailErrorMessage(String.format(
                    messageProvider.getMessage("rule.subdomainconfig.msg.noshardingtag"),
                    System.lineSeparator(),
                    sb
            ));
            return false;
        }
        // On controle que pour chaque sharding tag associé à l'API, tous les attributs sont renseignés
        for (ShardingTag shardingTag : shardingTags) {
            if (shardingTag.getEntrypointMappings() == null || shardingTag.getEntrypointMappings().isEmpty()) {
                setDetailErrorMessage(String.format(
                        messageProvider.getMessage("rule.subdomainconfig.msg.nohostnamemapping"),
                        System.lineSeparator()
                ));
                return false;
            }

        }

        // On controle que l'API est en mode virtual host et que ceux-ci sont bien définis
        if (virtualHosts == null || virtualHosts.isEmpty()) {
            setDetailErrorMessage(String.format(
                    messageProvider.getMessage("rule.subdomainconfig.msg.novhost"),
                    System.lineSeparator()
            ));
            return false;
        }

        for (VirtualHost virtualHost : virtualHosts) {
            // Si on est en mode context-path le host est nul
            if (virtualHost.getHost() == null) {
                setDetailErrorMessage(String.format(
                        messageProvider.getMessage("rule.subdomainconfig.msg.notvhostmode"),
                        System.lineSeparator()
                ));
                return false;
            }
            // On vérifie si le virtual host correspond à un de ceux associés aux sharding tags de l'API
            boolean isMapped = shardingTags.stream()
                    .flatMap(shardingTag -> shardingTag.getEntrypointMappings().stream())
                    .anyMatch(entrypoint -> entrypoint.equals(virtualHost.getHost()));

            if (!isMapped) {
                setDetailErrorMessage(String.format(
                        messageProvider.getMessage("rule.subdomainconfig.msg.novhostmapping"),
                        System.lineSeparator(),
                        virtualHost.getHost()
                ));
                return false;
            }

        }
        return true;
    }
}
