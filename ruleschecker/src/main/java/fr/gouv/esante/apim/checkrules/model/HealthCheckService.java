/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model;


import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class HealthCheckService {

    private boolean enabled;
    private List<HealthCheckRequest> paths;
}
