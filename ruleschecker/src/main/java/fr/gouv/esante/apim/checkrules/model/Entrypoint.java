/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model;


import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class Entrypoint {

    private String host;
    private Set<String> tags;
    private String target;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entrypoint that = (Entrypoint) o;
        return Objects.equals(host, that.host) && Objects.equals(tags, that.tags) && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, tags, target);
    }
}
