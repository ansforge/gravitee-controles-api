/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.services;

import fr.gouv.esante.apim.checkrules.model.Configuration;
import fr.gouv.esante.apim.checkrules.model.Entrypoint;
import fr.gouv.esante.apim.checkrules.model.Filter;
import fr.gouv.esante.apim.checkrules.model.Flow;
import fr.gouv.esante.apim.checkrules.model.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.HealthCheckRequest;
import fr.gouv.esante.apim.checkrules.model.HealthCheckService;
import fr.gouv.esante.apim.checkrules.model.Logging;
import fr.gouv.esante.apim.checkrules.model.Plan;
import fr.gouv.esante.apim.checkrules.model.Step;
import fr.gouv.esante.apim.checkrules.model.VirtualHost;
import fr.gouv.esante.apim.client.model.ApiEntityGravitee;

import fr.gouv.esante.apim.client.model.ApiEntrypointEntityGravitee;
import fr.gouv.esante.apim.client.model.FlowGravitee;
import fr.gouv.esante.apim.client.model.HealthCheckServiceGravitee;
import fr.gouv.esante.apim.client.model.HealthCheckStepGravitee;
import fr.gouv.esante.apim.client.model.LoggingGravitee;
import fr.gouv.esante.apim.client.model.PlanEntityGravitee;
import fr.gouv.esante.apim.client.model.StepGravitee;
import fr.gouv.esante.apim.client.model.VirtualHostGravitee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Service chargé de simplifier la définition des APIs
 * en amont du contrôle des règles d'implémentation
 */
@Service
@Slf4j
public class ApiDefinitionMapper {

    public GraviteeApiDefinition map(ApiEntityGravitee apiEntity) {

        GraviteeApiDefinition apiDef = new GraviteeApiDefinition();

        apiDef.setApiName(apiEntity.getName());

        apiDef.setGroups(apiEntity.getGroups());

        if (apiEntity.getPlans() != null) {
            Set<Plan> plans = new HashSet<>();
            for (PlanEntityGravitee plan : apiEntity.getPlans()) {
                plans.add(mapPlan(plan));
            }
            apiDef.setPlans(plans);
        }

        apiDef.setTags(apiEntity.getTags());

        if (apiEntity.getEntrypoints() != null) {
            List<Entrypoint> entrypoints = new ArrayList<>();
            for (ApiEntrypointEntityGravitee entrypointGravitee : apiEntity.getEntrypoints()) {
                entrypoints.add(mapEntrypoint(entrypointGravitee));
            }
            apiDef.setEntrypoints(entrypoints);
        }

        if (apiEntity.getProxy().getVirtualHosts() != null) {
            List<VirtualHost> virtualHosts = new ArrayList<>();
            for (VirtualHostGravitee virtualHostGravitee : apiEntity.getProxy().getVirtualHosts()) {
                virtualHosts.add(mapVirtualHost(virtualHostGravitee));
            }
            apiDef.setVirtualHosts(virtualHosts);
        }

        if (apiEntity.getServices() != null && apiEntity.getServices().getHealthCheck() != null) {
            apiDef.setHealthCheck(mapHealthCheck(apiEntity.getServices().getHealthCheck()));
        }

        if (apiEntity.getProxy().getLogging() != null) {
            apiDef.setLogging(mapLogging(apiEntity.getProxy().getLogging()));
        }

        return apiDef;
    }

    private Plan mapPlan(PlanEntityGravitee planGravitee) {
        Plan plan = new Plan();
        plan.setName(planGravitee.getName());
        plan.setAuthMechanism(planGravitee.getSecurity().getValue());
        List<Flow> flows = new ArrayList<>();
        for (FlowGravitee flowGravitee : planGravitee.getFlows()) {
            flows.add(mapFlow(flowGravitee));
        }
        plan.setFlows(flows);
        return plan;
    }

    private Flow mapFlow(FlowGravitee flowGravitee) {
        Flow flow = new Flow();
        if (flowGravitee.getPre() != null) {
            List<Step> preSteps = new ArrayList<>();
            for (StepGravitee stepGravitee : flowGravitee.getPre()) {
                preSteps.add(mapStep(stepGravitee));
            }
            flow.setPreSteps(preSteps);
        }
        if (flowGravitee.getPost() != null) {
            List<Step> postSteps = new ArrayList<>();
            for (StepGravitee stepGravitee : flowGravitee.getPost()) {
                postSteps.add(mapStep(stepGravitee));
            }
            flow.setPostSteps(postSteps);
        }
        return flow;
    }

    private Step mapStep(StepGravitee stepGravitee) {
        Step step = new Step();
        step.setPolicy(stepGravitee.getPolicy());
        if(stepGravitee.getConfiguration() != null) {
            step.setConfiguration(mapConfig(stepGravitee.getConfiguration()));
        }
        return step;
    }

    private Configuration mapConfig(Object configGravitee) {
        // On construit une configuration et et les objets qu'elle contient
        // à partir du contenu de l'objet configuration obtenu de Gravitee
        Configuration configuration = new Configuration();
        // On cast le contenu du champ configuration en map pour accéder
        // aux champs whitelist et blacklist
        Map<String, Object> config = (Map<String, Object>) configGravitee;
        List<Object> whitelistObject = (List<Object>) config.get("whitelist");
        List<Object> blacklistObject = (List<Object>) config.get("blacklist");
        // On cast le contenu des 2 listes en objets Filter du modèle
        List<Filter> whitelist = buildFilterList(whitelistObject);
        List<Filter> blacklist = buildFilterList(blacklistObject);

        configuration.setWhitelist(whitelist);
        configuration.setBlacklist(blacklist);
        return configuration;
    }

    private List<Filter> buildFilterList(List<Object> objectlist) {
        List<Filter> filters = new ArrayList<>();
        for (Object object : objectlist) {
            // On essaie de construire un Filter à partir des objets de la liste d'entrée
            // en loggant les cas d'échec.
            try {
                Map<String, Object> stringObjectMap = (Map<String, Object>) object;
                Filter endpointFilter = new Filter();
                endpointFilter.setPattern((String) stringObjectMap.get("pattern"));
                endpointFilter.setMethods((ArrayList) stringObjectMap.get("methods"));

                filters.add(endpointFilter);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
        return filters;
    }

    private Entrypoint mapEntrypoint(ApiEntrypointEntityGravitee entrypointGravitee) {
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setHost(entrypointGravitee.getHost());
        entrypoint.setTags(entrypointGravitee.getTags());
        entrypoint.setTarget(entrypointGravitee.getTarget());
        return entrypoint;
    }

    private VirtualHost mapVirtualHost(VirtualHostGravitee virtualHostGravitee) {
        VirtualHost virtualHost = new VirtualHost();
        virtualHost.setHost(virtualHostGravitee.getHost());
        virtualHost.setPath(virtualHostGravitee.getPath());
        virtualHost.setOverrideEntrypoint(Boolean.TRUE.equals(virtualHostGravitee.getOverrideEntrypoint()));
        return virtualHost;
    }

    private HealthCheckService mapHealthCheck(HealthCheckServiceGravitee healthCheckServiceGravitee) {
        HealthCheckService healthCheckService = new HealthCheckService();
        healthCheckService.setEnabled(Boolean.TRUE.equals(healthCheckServiceGravitee.getEnabled()));
        final List<HealthCheckRequest> paths = mapPaths(healthCheckServiceGravitee);
        healthCheckService.setPaths(paths);
        return healthCheckService;
    }

    private List<HealthCheckRequest> mapPaths(HealthCheckServiceGravitee healthCheckServiceGravitee) {
        List<HealthCheckRequest> paths = new ArrayList<>();
        if (healthCheckServiceGravitee.getSteps() != null) {
            for (HealthCheckStepGravitee healthCheckStep : healthCheckServiceGravitee.getSteps()) {
                HealthCheckRequest request = new HealthCheckRequest();
                if (healthCheckStep.getRequest() != null && healthCheckStep.getRequest().getMethod() != null) {
                    request.setMethod(healthCheckStep.getRequest().getMethod().getValue());
                }
                if (healthCheckStep.getRequest() != null) {
                    request.setPath(healthCheckStep.getRequest().getPath());
                }
                paths.add(request);
            }
        }
        return paths;
    }

    private Logging mapLogging(LoggingGravitee loggingGravitee) {
        Logging logging = new Logging();
        logging.setContent(loggingGravitee.getContent() != null ? loggingGravitee.getContent().getValue() : null);
        logging.setMode(loggingGravitee.getMode() != null ? loggingGravitee.getMode().getValue() : null);
        logging.setScope(loggingGravitee.getScope() != null ? loggingGravitee.getScope().getValue() : null);
        return logging;
    }

}
