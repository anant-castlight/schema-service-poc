package com.castlight.dataversioningpoc.hibernateenvers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;

/**
 * Created by anantm on 7/28/17.
 */

@Entity
@Audited
@Table(name = "json_schema")
@AuditTable(value = "json_schema_aud")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NamedQueries({@NamedQuery(name=JsonSchemaDetails.GET_ID_BY_NAME, query = "SELECT jsd.id FROM JsonSchemaDetails jsd WHERE jsd.name=:name"),
        @NamedQuery(name=JsonSchemaDetails.GET_JSON_SCHEMA_DETAILS_BY_NAME, query = "SELECT jsd FROM JsonSchemaDetails jsd WHERE jsd.name=:name")})
public class JsonSchemaDetails {

    public static final String GET_ID_BY_NAME = "getIdByName";
    public static final String GET_JSON_SCHEMA_DETAILS_BY_NAME = "getJsonSchemaDetailsByName";

    @Id
    @GeneratedValue
    @JsonProperty
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    @JsonProperty
    private Date date;

    @JsonProperty
    @Column(unique = true, nullable = false)
    private String name;

    @JsonProperty
    private String description;

    @Lob
    @Column(name = "json_schema", nullable = false)
    @JsonProperty
    private String jsonSchema;

    @Version
    @JsonProperty
    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
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

