/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model.definition;


import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
public class Logging {

    private String content;
    private String mode;
    private String scope;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Logging logging = (Logging) o;
        return Objects.equals(content, logging.content) && Objects.equals(mode, logging.mode) && Objects.equals(scope, logging.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, mode, scope);
    }
}
