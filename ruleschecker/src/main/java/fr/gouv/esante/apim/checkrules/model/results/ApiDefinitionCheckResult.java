/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model.results;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


/**
 * Résultat des contrôles de règles sur une API hébergée sur l'API Manager
 */
@Getter
@Setter
@JsonPropertyOrder({"apiDefinitionName", "timestamp", "ruleResults"})
public class ApiDefinitionCheckResult {

    /**
     * Nom de l'API contrôlée
     */
    private String apiDefinitionName;

    /**
     * Date et heure du début de la vérification de l'API
     */
    private String timestamp;

    /**
     * Liste des résultats des vérifications de chaque règle
     */
    private List<RuleResult> ruleResults = new ArrayList<>();

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }

}
