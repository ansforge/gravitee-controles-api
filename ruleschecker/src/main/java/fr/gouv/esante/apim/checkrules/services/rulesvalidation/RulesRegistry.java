/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.rulesvalidation;

import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


@Getter
@Component
@Slf4j
public class RulesRegistry {

    private final Set<ApiDefinitionQualityRule> rules = new HashSet<>();

}
