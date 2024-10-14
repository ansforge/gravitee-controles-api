/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;


@Getter
@Setter
public class Filter {

    private List<String> methods;
    private String pattern;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filter filter = (Filter) o;
        return Objects.equals(methods, filter.methods) && Objects.equals(pattern, filter.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methods, pattern);
    }
}
