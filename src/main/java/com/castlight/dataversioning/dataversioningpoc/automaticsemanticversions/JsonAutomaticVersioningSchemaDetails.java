package com.castlight.dataversioning.dataversioningpoc.automaticsemanticversions;

import com.castlight.dataversioning.dataversioningpoc.hibernateenvers.JsonUtil;
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
@Table(name = "json_automatic_versioning_schema_details", uniqueConstraints = { @UniqueConstraint( columnNames = { "name", "version" } ) })
@AuditTable(value = "json_automatic_versioning_schema_details_aud")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NamedQueries({@NamedQuery(name= JsonAutomaticVersioningSchemaDetails.GET_ID_BY_NAME_VERSION, query = "SELECT jsd.id FROM JsonSchemaDetails jsd WHERE jsd.name=:name AND jsd.version=:version"),
        @NamedQuery(name= JsonAutomaticVersioningSchemaDetails.GET_LATEST_VERSION_BY_NAME, query = "SELECT jsd.version FROM JsonSchemaDetails jsd WHERE jsd.name=:name"),
        @NamedQuery(name= JsonAutomaticVersioningSchemaDetails.GET_ALL_JSON_SCHEMA_DETAILS_BY_NAME, query = "SELECT jsd FROM JsonSchemaDetails jsd WHERE jsd.name=:name ORDER BY jsd.id DESC"),
        @NamedQuery(name= JsonAutomaticVersioningSchemaDetails.GET_JSON_SCHEMA_DETAILS_BY_NAME_VERSION, query = "SELECT jsd FROM JsonSchemaDetails jsd WHERE jsd.name=:name AND jsd.version=:version")})
public class JsonAutomaticVersioningSchemaDetails {

    public static final String GET_ID_BY_NAME_VERSION = "getAutomaticSchemaetailsIdByNameVersion";
    public static final String GET_JSON_SCHEMA_DETAILS_BY_NAME_VERSION = "getAutomaticJsonSchemaDetailsByNameVersion";
    public static final String GET_ALL_JSON_SCHEMA_DETAILS_BY_NAME = "getAllAutomaticJsonSchemaDetailsByName";
    public static final String GET_LATEST_VERSION_BY_NAME = "getAutomaticSchemaetailsLatestVersionByName";

    @Id
    @GeneratedValue
    @JsonProperty
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    @JsonProperty
    private Date date;

    @JsonProperty
    @Column(name="name", nullable = false)
    private String name;

    @JsonProperty
    private String description;

    @Lob
    @Column(name = "json_schema", nullable = false)
    @JsonProperty
    private String jsonSchema;

    @Version
    @Column(name = "change_number", nullable = false)
    @JsonProperty
    private Integer changeNumber;

    @JsonProperty
    @Column(name="version", nullable = false)
    private String version;

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

    public Integer getChangeNumber() {
        return changeNumber;
    }

    public void setChangeNumber(Integer changeNumber) {
        this.changeNumber = changeNumber;
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

