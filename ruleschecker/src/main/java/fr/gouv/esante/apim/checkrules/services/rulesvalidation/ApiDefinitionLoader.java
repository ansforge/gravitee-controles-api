/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.rulesvalidation;

import fr.gouv.esante.apim.checkrules.exception.ApimRulecheckerException;
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
import org.springframework.web.client.RestClientException;

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

    public List<GraviteeApiDefinition> loadApiDefinitions() throws ApimRulecheckerException {
        List<TagEntityGravitee> tagEntities = configurationApi.getTags1(envId, orgId);
        log.info("Found {} Sharding tags on environment {}", tagEntities.size(), envId);
        if (tagEntities.isEmpty()) {
            throw new ApimRulecheckerException("No Sharding tags found on environment " + envId);
        }

        PageInstanceListItemGravitee gatewayInstances = gatewayApi.getInstances(
                envId,
                orgId,
                false,
                0L,
                0L,
                1,
                100
        );
        log.info("Found {} Gateway instances", gatewayInstances.getTotalElements());
        if (gatewayInstances.getTotalElements() == 0) {
            throw new ApimRulecheckerException("No Gateway instances found on environment " + envId);
        }

        List<GraviteeApiDefinition> apiDefinitions = new ArrayList<>();
        try {
            List<ApiListItemGravitee> apisResponse = apisApi.getApis(
                    envId,
                    orgId,
                    null,
                    null,
                    false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    ExecutionModeGravitee.V3,
                    null,
                    null,
                    false,
                    null,
                    Collections.emptyList()
            );
            log.info("Found {} API definitions", apisResponse.size());
            if (apisResponse.isEmpty()) {
                throw new ApimRulecheckerException("No API definitions found on environment " + envId);
            }

            for (ApiListItemGravitee apiListItem : apisResponse) {
                // Get each individual API Definition
                ApiEntityGravitee apiEntity = apisApi.getApi(apiListItem.getId(), envId, orgId);

                // Build an object GraviteeApiDefinition and add it to returned list
                apiDefinitions.add(mapper.map(apiEntity, tagEntities, gatewayInstances.getContent()));
            }
        } catch (RestClientException e) {
            log.error("Error while querying APIM API :", e);
            throw new ApimRulecheckerException("Error while querying APIM API", e);
        }

        return apiDefinitions;
    }
}
