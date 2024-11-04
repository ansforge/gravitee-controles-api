/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model.definition;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;


@Getter
@Setter
public class HealthCheckService {

    private boolean enabled;

    private List<HealthCheckRequest> paths;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthCheckService that = (HealthCheckService) o;
        return enabled == that.enabled && Objects.equals(paths, that.paths);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, paths);
    }
}
