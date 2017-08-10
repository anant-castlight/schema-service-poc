package com.castlight.dataversioningpoc.manualsemanticversions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.InvalidSchemaException;
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

    private static ObjectMapper mapper = new ObjectMapper();

    @Id
    @GeneratedValue
    private Long id;

    @JsonProperty
    @Column(name="name", nullable = false)
    private String name;

    @JsonProperty
    @Column(name="version", nullable = false)
    private String version;

    @JsonProperty
    private String description;

    @Lob
    @Column(name = "json_schema", nullable = false)
    private String jsonSchemaToStore;

    @JsonProperty
    @Transient
    private JsonNode jsonSchema;

    public JsonSchemaDetail() {
    }

    public JsonSchemaDetail(String name, String description, String jsonSchemaToStore, String version) throws IOException, ProcessingException {
        this.name = name;
        this.description = description;
        if(JsonUtil.isJsonSchema(jsonSchemaToStore)) {
            this.jsonSchemaToStore = jsonSchemaToStore;
        }
        this.version = version;
    }

    @JsonIgnore
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

    @JsonIgnore
    public String getJsonSchemaToStore() {
        return jsonSchemaToStore;
    }

    public void setJsonSchemaToStore(String jsonSchemaToStore) throws IOException, ProcessingException {
        if(JsonUtil.isJsonSchema(jsonSchemaToStore)) {
            this.jsonSchemaToStore = jsonSchemaToStore;
        }
    }

    public JsonNode getJsonSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(JsonNode jsonSchema) {
        this.jsonSchema = jsonSchema;
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

    @PostLoad
    public void afterLoading() throws IOException {
        jsonSchema = mapper.readTree(jsonSchemaToStore);
    }
}

