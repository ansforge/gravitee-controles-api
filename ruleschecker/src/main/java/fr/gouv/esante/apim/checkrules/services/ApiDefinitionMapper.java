/*
 * (c) Copyright 1998-2024, ANS. All rights reserved.
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
        apiDef.setDescription(apiEntity.getDescription());
        apiDef.setVersion(apiEntity.getVersion());
        apiDef.setPlans(apiEntity.getPlans());
        apiDef.setCategories(apiEntity.getCategories());
        apiDef.setEntrypoints(apiEntity.getEntrypoints());
        apiDef.setGroups(apiEntity.getGroups());
        apiDef.setProxy(apiEntity.getProxy());
        apiDef.setVirtualHosts(apiEntity.getProxy().getVirtualHosts());
        if (apiEntity.getProxy().getVirtualHosts() == null) throw new AssertionError();
        apiDef.setHost(apiEntity.getProxy().getVirtualHosts().get(0).getHost());
        apiDef.setTags(apiEntity.getTags());
        return apiDef;
    }

}
