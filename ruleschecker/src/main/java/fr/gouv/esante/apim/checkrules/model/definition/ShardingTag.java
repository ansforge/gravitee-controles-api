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

    private String id;
    private List<String> entrypointMappings = new ArrayList<>();

    public ShardingTag(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShardingTag that = (ShardingTag) o;
        return Objects.equals(id, that.id) && Objects.equals(entrypointMappings, that.entrypointMappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entrypointMappings);
    }
}
