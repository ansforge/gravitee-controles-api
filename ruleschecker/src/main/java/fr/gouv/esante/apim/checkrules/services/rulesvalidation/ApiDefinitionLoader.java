/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.rulesvalidation;

import fr.gouv.esante.apim.checkrules.exception.ApimRulecheckerException;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.client.api.ApisApi;
import fr.gouv.esante.apim.client.api.ConfigurationApi;
import fr.gouv.esante.apim.client.model.ApiEntityGravitee;
import fr.gouv.esante.apim.client.model.ApiListItemGravitee;
import fr.gouv.esante.apim.client.model.EntrypointEntityGravitee;
import fr.gouv.esante.apim.client.model.ExecutionModeGravitee;
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


    /**
     * Service chargé de simplifier la définition des APIs
     * en amont du contrôle des règles d'implémentation
     */
    private final ApiDefinitionMapper mapper;


    public ApiDefinitionLoader(ApisApi apisApi,
                               ConfigurationApi configurationApi,
                               ApiDefinitionMapper mapper) {
        this.apisApi = apisApi;
        this.configurationApi = configurationApi;
        this.mapper = mapper;
    }

    public List<GraviteeApiDefinition> loadApiDefinitions() throws ApimRulecheckerException {
        log.info("Loading api definitions from {}", apisApi.getApiClient().getBasePath());


        List<EntrypointEntityGravitee> entrypointEntities = configurationApi.getEntrypoints(orgId);
        log.info("Found {} entrypoint entities on environment {}", entrypointEntities.size(), envId);
        if (entrypointEntities.isEmpty()) {
            throw new ApimRulecheckerException("No entrypoint entity found on environment " + envId);
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
                apiDefinitions.add(mapper.map(apiEntity, entrypointEntities));
            }
        } catch (RestClientException e) {
            log.error("Error while querying APIM API :", e);
            throw new ApimRulecheckerException("Error while querying APIM API", e);
        }

        return apiDefinitions;
    }
}
