/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services;

import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.client.api.ApisApi;
import fr.gouv.esante.apim.client.model.ApiEntityGravitee;
import fr.gouv.esante.apim.client.model.ApiListItemGravitee;
import fr.gouv.esante.apim.client.model.ExecutionModeGravitee;
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
    @Value("${env}")
    private String envId;

    /**
     * Token d'accès à l'API de gestion de Gravitee
     */
    @Value("${apikey}")
    private String apiKey;

    /**
     * Client API
     */
    private final ApisApi apisApi;

    /**
     * Service chargé de simplifier la définition des APIs
     * en amont du contrôle des règles d'implémentation
     */
    private final ApiDefinitionMapper mapper;


    public ApiDefinitionLoader(ApisApi apisApi, ApiDefinitionMapper mapper) {
        this.apisApi = apisApi;
        this.mapper = mapper;
    }

    public List<GraviteeApiDefinition> loadApiDefinitions() {
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

            for (ApiListItemGravitee apiListItem : apisResponse) {
                // Get each individual API Definition
                ApiEntityGravitee apiEntity = apisApi.getApi(apiListItem.getId(), envId, orgId);

                // Build an object GraviteeApiDefinition and add it to returned list
                apiDefinitions.add(mapper.map(apiEntity));
            }
        } catch (Exception e) {
            log.error("CAUGHT ERROR");
            log.error(e.getMessage());
            log.error("END ERROR");
        }

        return apiDefinitions;
    }
}
