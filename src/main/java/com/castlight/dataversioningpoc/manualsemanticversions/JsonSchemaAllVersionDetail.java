package com.castlight.dataversioningpoc.manualsemanticversions;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by anantm on 8/10/17.
 */
public class JsonSchemaAllVersionDetail {

    @JsonProperty
    private List<JsonSchemaDetail> jsonSchemaVersions;

    public JsonSchemaAllVersionDetail(List<JsonSchemaDetail> jsonSchemaVersions) {
        this.jsonSchemaVersions = jsonSchemaVersions;
    }
}
