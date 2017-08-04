package com.castlight.dataversioning.dataversioningpoc.automaticsemanticversions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Table(name = "test_json_object_details")
@NamedQueries({
        @NamedQuery(name= TestJsonObjectDetails.GET_ALL_TEST_JSON_BY_SCHEMA_ID, query = "SELECT jsd.jsonObject FROM TestJsonObjectDetails jsd WHERE jsd.jsonSchemaId=:jsonSchemaId ")})
public class TestJsonObjectDetails {

    public static final String GET_ALL_TEST_JSON_BY_SCHEMA_ID = "getAllTestJsonBySchemaId";


    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "json_schema_id", nullable = false)
    private Long jsonSchemaId;

    @Lob
    @Column(name = "json_object", nullable = false)
    private String jsonObject;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJsonSchemaId() {
        return jsonSchemaId;
    }

    public void setJsonSchemaId(Long jsonSchemaId) {
        this.jsonSchemaId = jsonSchemaId;
    }

    public String getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }
}
