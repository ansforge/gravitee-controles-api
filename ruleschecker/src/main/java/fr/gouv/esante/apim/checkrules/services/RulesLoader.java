/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services;

import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@Component
@Slf4j
public class RulesLoader {

    private static final String PACKAGE_NAME = "fr.gouv.esante.apim.checkrules.rules.impl";

    private Set<ApiDefinitionQualityRule> rules;

    public RulesLoader() {
        findAllClassesUsingClassLoader();
    }

    private void findAllClassesUsingClassLoader() {
        String path = PACKAGE_NAME.replaceAll("[.]", "/");
        log.debug("Finding all classes using classloader {}", path);
        InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(path);
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        rules = reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(this::getRule)
                .collect(Collectors.toSet());
    }

    private ApiDefinitionQualityRule getRule(String className) {
        try {
            String simpleClassName = PACKAGE_NAME + "." + className.substring(0, className.lastIndexOf('.'));
            ApiDefinitionQualityRule apiDefinitionQualityRule = (ApiDefinitionQualityRule) Class.forName(simpleClassName)
                    .getDeclaredConstructor().newInstance();
            log.info("Found rule {}", apiDefinitionQualityRule.getClass().getSimpleName());
            return apiDefinitionQualityRule;
        } catch (ClassNotFoundException e) {
            // handle the exception
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
