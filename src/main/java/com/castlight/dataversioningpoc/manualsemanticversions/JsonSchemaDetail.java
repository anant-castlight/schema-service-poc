package com.castlight.dataversioningpoc.manualsemanticversions;

import com.castlight.dataversioningpoc.hibernateenvers.JsonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.IOException;

/**
 * Created by anantm on 7/28/17.
 */

@Entity
@Component
@Table(name = "json_schema_details", uniqueConstraints = { @UniqueConstraint( columnNames = { "name", "version" } ) })
public class JsonSchemaDetail {

    @Id
    @GeneratedValue
    @JsonProperty
    private Long id;

    @JsonProperty
    @Column(name="name", nullable = false)
    private String name;

    @JsonProperty
    private String description;

    @Lob
    @Column(name = "json_schema", nullable = false)
    @JsonProperty
    private String jsonSchema;

    @JsonProperty
    @Column(name="version", nullable = false)
    private String version;

    public JsonSchemaDetail() {
    }

    public JsonSchemaDetail(String name, String description, String jsonSchema, String version) {
        this.name = name;
        this.description = description;
        this.jsonSchema = jsonSchema;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJsonSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(String jsonSchema) throws IOException, ProcessingException {
        if(JsonUtil.isJsonSchema(jsonSchema)) {
            this.jsonSchema = jsonSchema;
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

