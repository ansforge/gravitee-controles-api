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
public class Configuration {

    private List<Filter> whitelist;
    private List<Filter> blacklist;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return Objects.equals(whitelist, that.whitelist) && Objects.equals(blacklist, that.blacklist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(whitelist, blacklist);
    }
}
