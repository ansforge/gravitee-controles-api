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
public class Flow {

    private List<Step> preSteps;
    private List<Step> postSteps;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flow flow = (Flow) o;
        return Objects.equals(preSteps, flow.preSteps) && Objects.equals(postSteps, flow.postSteps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(preSteps, postSteps);
    }
}
