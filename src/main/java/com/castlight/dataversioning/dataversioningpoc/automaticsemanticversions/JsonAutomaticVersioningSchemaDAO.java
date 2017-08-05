package com.castlight.dataversioning.dataversioningpoc.automaticsemanticversions;

import com.castlight.dataversioning.dataversioningpoc.hibernateenvers.HibernateUtil;
import com.castlight.dataversioning.dataversioningpoc.hibernateenvers.JsonUtil;
import com.castlight.dataversioning.dataversioningpoc.manualsemanticversions.JsonManualVersioningSchemaDetails;
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
public class JsonAutomaticVersioningSchemaDAO {

    //Create session factory object
    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();


    public void create(String name, String jsonSchema, String description, String version) throws IOException, ProcessingException {

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        JsonAutomaticVersioningSchemaDetails jsonSchemaDetails = new JsonAutomaticVersioningSchemaDetails();
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
        JsonAutomaticVersioningSchemaDetails jsonSchemaDetails = session.load(JsonAutomaticVersioningSchemaDetails.class, id);
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

    public JsonAutomaticVersioningSchemaDetails get(String name, String version) {
        Session session = sessionFactory.openSession();
        Query query = session.createNamedQuery(JsonAutomaticVersioningSchemaDetails.GET_JSON_SCHEMA_DETAILS_BY_NAME_VERSION);
        query.setParameter("name", name);
        query.setParameter("version", version);
        return (JsonAutomaticVersioningSchemaDetails) query.getSingleResult();
    }

    public List<JsonAutomaticVersioningSchemaDetails> getAllVersions(String name) {

        Session session = sessionFactory.openSession();
        Query query = session.createNamedQuery(JsonAutomaticVersioningSchemaDetails.GET_ALL_JSON_SCHEMA_DETAILS_BY_NAME);
        query.setParameter("name", name);
        return (List<JsonAutomaticVersioningSchemaDetails>) query.getResultList();
    }

    public List<JsonAutomaticVersioningSchemaDetails> getAllModificationsOnVersion(String name, String version) {
        List<JsonAutomaticVersioningSchemaDetails> jsonSchemaVersionList = new ArrayList<>();
        Session session = sessionFactory.openSession();
        Long id = getIdByNameAndVersion(name, session, version);
        AuditReader auditReader = AuditReaderFactory.get(session);
        List<Number> revisionNumbers = auditReader.getRevisions(JsonAutomaticVersioningSchemaDetails.class, id);
        for (Number rev : revisionNumbers) {
            JsonAutomaticVersioningSchemaDetails auditedSchema = auditReader.find(JsonAutomaticVersioningSchemaDetails.class, id, rev);
            jsonSchemaVersionList.add(auditedSchema);
        }
        return jsonSchemaVersionList;
    }

    public String getLatestVersionForSchema(String name) {
        Session session = sessionFactory.openSession();
        Query query = session.createNamedQuery(JsonAutomaticVersioningSchemaDetails.GET_LATEST_VERSION_BY_NAME);
        query.setParameter("name", name);
        query.setMaxResults(1);
        if(query.getResultList().size() == 0) {
            return new SemanticVersion().toString();
        }
        return (String)query.getResultList().get(0);
    }

    private Long getIdByNameAndVersion(String name, Session session, String version) {
        Query query = session.createNamedQuery(JsonAutomaticVersioningSchemaDetails.GET_ID_BY_NAME_VERSION);
        query.setParameter("name", name);
        query.setParameter("version", version);
        return (Long) query.getSingleResult();
    }

}


