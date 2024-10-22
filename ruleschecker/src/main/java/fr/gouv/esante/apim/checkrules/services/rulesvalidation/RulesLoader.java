/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services.rulesvalidation;

import fr.gouv.esante.apim.checkrules.exception.ApimRulecheckerException;
import fr.gouv.esante.apim.checkrules.rules.ApiDefinitionQualityRule;
import fr.gouv.esante.apim.checkrules.services.notification.EmailNotifier;
import lombok.Getter;
import lombok.Setter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;


@Setter
@Getter
@Component
@Slf4j
public class RulesLoader {

    @Value("${envid}")
    private String envId;

    private static final String PACKAGE_NAME = "fr.gouv.esante.apim.checkrules.rules.impl";

    private Set<ApiDefinitionQualityRule> rules = new HashSet<>();

    private EmailNotifier emailNotifier;

    public RulesLoader(EmailNotifier emailNotifier) {
        this.emailNotifier = emailNotifier;
        try {
            findAllClassesUsingClassLoader();
        } catch (ApimRulecheckerException e) {
            log.error(e.getMessage());
            emailNotifier.notifyError(e, envId);
            throw new RuntimeException(e);
        }
    }

    private void findAllClassesUsingClassLoader() throws ApimRulecheckerException {
        String path = PACKAGE_NAME.replaceAll("[.]", "/");
        log.info("Finding all classes using classloader {}", path);
        InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(path);
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                log.debug("Parsing line : {}", line);
                if(line.endsWith(".class")) {
                    rules.add(getRule(line));
                }
            }
        } catch (IOException e) {
                throw new ApimRulecheckerException("Erreur au chargement des règles", e);
            }
        }

    private ApiDefinitionQualityRule getRule(String className) throws ApimRulecheckerException {
        try {
            String simpleClassName = PACKAGE_NAME + "." + className.substring(0, className.lastIndexOf('.'));
            ApiDefinitionQualityRule apiDefinitionQualityRule = (ApiDefinitionQualityRule) Class.forName(simpleClassName)
                    .getDeclaredConstructor().newInstance();
            log.info("Found rule {}", apiDefinitionQualityRule.getClass().getSimpleName());
            return apiDefinitionQualityRule;
        } catch (ClassNotFoundException e) {
            log.error("ClassNotFoundException", e);
            throw new ApimRulecheckerException("ClassNotFoundException", e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            log.error("Unexpected Exception", e);
            throw new ApimRulecheckerException("ClassNotFoundException", e);
        }
    }
}
