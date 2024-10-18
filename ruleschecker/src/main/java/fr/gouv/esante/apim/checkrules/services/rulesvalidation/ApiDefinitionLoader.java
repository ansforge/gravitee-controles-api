/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.rulesvalidation;

import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.client.api.ApisApi;
import fr.gouv.esante.apim.client.api.ConfigurationApi;
import fr.gouv.esante.apim.client.api.GatewayApi;
import fr.gouv.esante.apim.client.model.ApiEntityGravitee;
import fr.gouv.esante.apim.client.model.ApiListItemGravitee;
import fr.gouv.esante.apim.client.model.ExecutionModeGravitee;
import fr.gouv.esante.apim.client.model.PageInstanceListItemGravitee;
import fr.gouv.esante.apim.client.model.TagEntityGravitee;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Composant chargé d'appeler l'API management de Gravitee
 * pour récupérer la définition de chaque API gu'il héberge
 */
@Component
@Slf4j
public class ApiDefinitionLoader {

    /**
     * Identifiant de l'organisation administrant Gravitee
     */
    @Value("${apim.org.id}")
    private String orgId;

    /**
     * Identifiant de l'environnement où se trouve Gravitee
     */
    @Value("${envid}")
    private String envId;

    /**
     * Client API
     */
    private final ApisApi apisApi;

    private final ConfigurationApi configurationApi;

    private final GatewayApi gatewayApi;

    /**
     * Service chargé de simplifier la définition des APIs
     * en amont du contrôle des règles d'implémentation
     */
    private final ApiDefinitionMapper mapper;


    public ApiDefinitionLoader(ApisApi apisApi,
                               ConfigurationApi configurationApi,
                               GatewayApi gatewayApi,
                               ApiDefinitionMapper mapper) {
        this.apisApi = apisApi;
        this.configurationApi = configurationApi;
        this.gatewayApi = gatewayApi;
        this.mapper = mapper;
    }

    public List<GraviteeApiDefinition> loadApiDefinitions() {
        List<TagEntityGravitee> tagEntities = configurationApi.getTags1(envId, orgId);
        log.debug("Found {} Sharding tags on environment {}", tagEntities.size(), envId);

        PageInstanceListItemGravitee gatewayInstances = gatewayApi.getInstances(
                envId,
                orgId,
                false,
                0L,
                0L,
                1,
                100
        );
        log.debug("Found {} Gateway instances", gatewayInstances.getTotalElements());

        List<GraviteeApiDefinition> apiDefinitions = new ArrayList<>();
        try {
            List<ApiListItemGravitee> apisResponse = apisApi.getApis(
                    envId,
                    orgId,
                    "",
                    "",
                    false,
                    "",
                    "",
                    "",
                    "",
                    "",
                    ExecutionModeGravitee.V3,
                    "",
                    "",
                    false,
                    "",
                    Collections.emptyList()
            );
            log.debug("Found {} API definitions", apisResponse.size());

            for (ApiListItemGravitee apiListItem : apisResponse) {
                // Get each individual API Definition
                ApiEntityGravitee apiEntity = apisApi.getApi(apiListItem.getId(), envId, orgId);

                // Build an object GraviteeApiDefinition and add it to returned list
                apiDefinitions.add(mapper.map(apiEntity, tagEntities, gatewayInstances.getContent()));
            }
        } catch (Exception e) {
            log.error("CAUGHT ERROR");
            log.error(e.getMessage());
        }

        return apiDefinitions;
    }
}
