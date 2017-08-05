package com.castlight.dataversioning.dataversioningpoc.manualsemanticversions;

import com.castlight.dataversioning.dataversioningpoc.hibernateenvers.HibernateUtil;
import com.castlight.dataversioning.dataversioningpoc.hibernateenvers.JsonSchemaDetails;
import com.castlight.dataversioning.dataversioningpoc.hibernateenvers.JsonUtil;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Repository
public class JsonManualVersioningSchemaDAO {

    //Create session factory object
    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();


    public void create(String name, String jsonSchema, String description, String version) throws IOException, ProcessingException {

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        JsonManualVersioningSchemaDetails jsonSchemaDetails = new JsonManualVersioningSchemaDetails();
        jsonSchemaDetails.setName(name);
        jsonSchemaDetails.setJsonSchema(jsonSchema);
        jsonSchemaDetails.setDescription(description);
        jsonSchemaDetails.setDate(new Date());
        jsonSchemaDetails.setVersion(version);
        session.save(jsonSchemaDetails);
        session.getTransaction().commit();
        session.close();
        System.out.println("Inserted Successfully");
    }

    public void update(String name, String jsonSchema, String description, String version) throws IOException, ProcessingException {

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Long id = getIdByNameAndVersion(name, session, version);
        JsonManualVersioningSchemaDetails jsonSchemaDetails = session.load(JsonManualVersioningSchemaDetails.class, id);
        if(JsonUtil.isJsonSchemaChanged(jsonSchemaDetails.getJsonSchema(),jsonSchema )) {
            jsonSchemaDetails.setDate(new Date());
            jsonSchemaDetails.setJsonSchema(jsonSchema);
            jsonSchemaDetails.setDescription(description);
            session.save(jsonSchemaDetails);
            session.getTransaction().commit();
            System.out.println("Updated Successfully");
        }
        session.close();

    }

    public JsonManualVersioningSchemaDetails get(String name, String version) {
        Session session = sessionFactory.openSession();
        Query query = session.createNamedQuery(JsonManualVersioningSchemaDetails.GET_JSON_SCHEMA_DETAILS_BY_NAME_VERSION);
        query.setParameter("name", name);
        query.setParameter("version", version);
        return (JsonManualVersioningSchemaDetails) query.getSingleResult();
    }

    public List<JsonManualVersioningSchemaDetails> getAllVersions(String name) {

        Session session = sessionFactory.openSession();
        Query query = session.createNamedQuery(JsonManualVersioningSchemaDetails.GET_ALL_JSON_SCHEMA_DETAILS_BY_NAME);
        query.setParameter("name", name);
        return (List<JsonManualVersioningSchemaDetails>) query.getResultList();
    }

    public List<JsonManualVersioningSchemaDetails> getAllModificationsOnVersion(String name, String version) {
        List<JsonManualVersioningSchemaDetails> jsonSchemaVersionList = new ArrayList<>();
        Session session = sessionFactory.openSession();
        Long id = getIdByNameAndVersion(name, session, version);
        AuditReader auditReader = AuditReaderFactory.get(session);
        List<Number> revisionNumbers = auditReader.getRevisions(JsonManualVersioningSchemaDetails.class, id);
        for (Number rev : revisionNumbers) {
            JsonManualVersioningSchemaDetails auditedSchema = auditReader.find(JsonManualVersioningSchemaDetails.class, id, rev);
            jsonSchemaVersionList.add(auditedSchema);
        }
        return jsonSchemaVersionList;
    }

    private Long getIdByNameAndVersion(String name, Session session, String version) {
        Query query = session.createNamedQuery(JsonManualVersioningSchemaDetails.GET_ID_BY_NAME_VERSION);
        query.setParameter("name", name);
        query.setParameter("version", version);
        return (Long) query.getSingleResult();
    }

}


