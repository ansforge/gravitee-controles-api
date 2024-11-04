/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model.definition;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Group {

    private String name;
    private List<Endpoint> endpoints = new ArrayList<>();
    private HealthCheckService healthCheckService;
}
