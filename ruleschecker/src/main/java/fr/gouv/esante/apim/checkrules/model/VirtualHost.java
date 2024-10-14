/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model;


import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class VirtualHost {

    private String host;
    private String path;
    private boolean overrideEntrypoint;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VirtualHost that = (VirtualHost) o;
        return overrideEntrypoint == that.overrideEntrypoint && Objects.equals(host, that.host) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, path, overrideEntrypoint);
    }
}
