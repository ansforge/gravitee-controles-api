/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model.definition;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
public class ShardingTag {

    private String name;
    private List<String> restrictedGroups = new ArrayList<>();
    private List<String> entrypointMappings = new ArrayList<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShardingTag that = (ShardingTag) o;
        return Objects.equals(name, that.name) && Objects.equals(restrictedGroups, that.restrictedGroups) && Objects.equals(entrypointMappings, that.entrypointMappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, restrictedGroups, entrypointMappings);
    }
}
