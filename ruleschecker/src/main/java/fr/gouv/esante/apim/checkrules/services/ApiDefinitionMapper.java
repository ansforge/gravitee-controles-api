/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services;

import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.client.model.ApiEntityGravitee;

import org.springframework.stereotype.Service;


/**
 * Service chargé de simplifier la définition des APIs
 * en amont du contrôle des règles d'implémentation
 */
@Service
public class ApiDefinitionMapper {

    public GraviteeApiDefinition map(ApiEntityGravitee apiEntity) {
        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();
        apiDef.setApiName(apiEntity.getName());
        apiDef.setGroups(apiEntity.getGroups());
        apiDef.setPlans(apiEntity.getPlans());
        apiDef.setTags(apiEntity.getTags());
        apiDef.setEntrypoints(apiEntity.getEntrypoints());
        apiDef.setVirtualHosts(apiEntity.getProxy().getVirtualHosts());
        if (apiEntity.getServices() == null) {
            apiDef.setHealthCheck(null);
        } else {
            apiDef.setHealthCheck(apiEntity.getServices().getHealthCheck());
        }
        apiDef.setLogging(apiEntity.getProxy().getLogging());
        return apiDef;
    }
}
