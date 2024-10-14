/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
public class Step {

    private String policy;
    private Configuration configuration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Step step = (Step) o;
        return Objects.equals(policy, step.policy) && Objects.equals(configuration, step.configuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policy, configuration);
    }
}
