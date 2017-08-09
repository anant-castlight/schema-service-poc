package com.castlight.dataversioningpoc.manualsemanticversions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by anantm on 8/9/17.
 */
public enum ChangeType {

    @JsonProperty("Major")
    MAJOR("Major"),
    @JsonProperty("Minor")
    MINOR("Minor"),
    @JsonProperty("Patch")
    PATCH("Patch");

    private String value;

    ChangeType(@JsonProperty String value) {
        this.value = value;
    }


}
