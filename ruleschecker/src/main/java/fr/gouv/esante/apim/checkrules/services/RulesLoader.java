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

    private Set<ApiDefinitionQualityRule> rules;

    public RulesLoader() {
        findAllClassesUsingClassLoader("fr.gouv.esante.apim.checkrules.rules.impl");
    }

    private void findAllClassesUsingClassLoader(String packageName) {
        String path = packageName.replaceAll("[.]", "/");
        log.debug("Finding all classes using classloader " + path);
        InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(path);
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        rules = reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getRule(line, packageName))
                .collect(Collectors.toSet());
    }

    private ApiDefinitionQualityRule getRule(String className, String packageName) {
        try {
            ApiDefinitionQualityRule apiDefinitionQualityRule = (ApiDefinitionQualityRule) Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.'))).getDeclaredConstructor().newInstance();
            log.info("Found rule " + apiDefinitionQualityRule.getClass().getSimpleName());
            return apiDefinitionQualityRule;
        } catch (ClassNotFoundException e) {
            // handle the exception
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
