/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VirtualHost {

    private String host;
    private String path;
    private boolean overrideEntrypoint;
}
