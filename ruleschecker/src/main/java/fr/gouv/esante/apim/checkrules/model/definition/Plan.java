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
public class Plan {

    private String name;
    private String authMechanism;
    private String status;
    private List<Flow> flows;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plan plan = (Plan) o;
        return Objects.equals(name, plan.name) && Objects.equals(authMechanism, plan.authMechanism) && Objects.equals(status, plan.status) && Objects.equals(flows, plan.flows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, authMechanism, status, flows);
    }
}
