/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model.definition;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
public class Entrypoint {

    private String targetHostName;
    private String host;


    public Entrypoint(String targetHostName, String host) {
        this.targetHostName = targetHostName;
        this.host = host;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entrypoint that = (Entrypoint) o;
        return Objects.equals(targetHostName, that.targetHostName) && Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetHostName, host);
    }
}
