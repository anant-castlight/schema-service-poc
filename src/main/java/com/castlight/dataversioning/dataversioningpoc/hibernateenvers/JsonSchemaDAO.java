package com.castlight.dataversioning.dataversioningpoc.hibernateenvers;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;




@Repository
public class JsonSchemaDAO {

    //Create session factory object
    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();


    public void create(String name, String jsonSchema, String description) throws IOException, ProcessingException {

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        JsonSchemaDetails jsonSchemaDetails = new JsonSchemaDetails();
        jsonSchemaDetails.setName(name);
        jsonSchemaDetails.setJsonSchema(jsonSchema);
        jsonSchemaDetails.setDescription(description);
        jsonSchemaDetails.setDate(new Date());
        session.save(jsonSchemaDetails);
        session.getTransaction().commit();
        session.close();
        System.out.println("Inserted Successfully");
    }

    public void update(String name, String jsonSchema, String description) throws IOException, ProcessingException {

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Long id = getIdByName(name, session);
        JsonSchemaDetails jsonSchemaDetails = session.load(JsonSchemaDetails.class, id);
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

    public JsonSchemaDetails get(String name) {
        Session session = sessionFactory.openSession();
        Query query = session.createNamedQuery(JsonSchemaDetails.GET_JSON_SCHEMA_DETAILS_BY_NAME);
        query.setParameter("name", name);
        return (JsonSchemaDetails) query.getSingleResult();
    }

    public List<JsonSchemaDetails> getAllVersions(String name) {
        List<JsonSchemaDetails> jsonSchemaVersionList = new ArrayList<>();
        Session session = sessionFactory.openSession();
        Long id = getIdByName(name, session);
        AuditReader auditReader = AuditReaderFactory.get(session);
        List<Number> revisionNumbers = auditReader.getRevisions(JsonSchemaDetails.class, id);
        for (Number rev : revisionNumbers) {
            JsonSchemaDetails auditedSchema = auditReader.find(JsonSchemaDetails.class, id, rev);
            System.out.println("JSON Schema versions-" + auditedSchema.toString());
            jsonSchemaVersionList.add(auditedSchema);
        }
        return jsonSchemaVersionList;
    }

    public JsonSchemaDetails getJsonByVersion(String name, Integer version) {
        Session session = sessionFactory.openSession();
        Long id = getIdByName(name, session);
        AuditReader auditReader = AuditReaderFactory.get(session);
        AuditQuery auditQuery = auditReader.createQuery().forRevisionsOfEntity(JsonSchemaDetails.class, true, false);
        auditQuery.add(AuditEntity.id().eq(id));
        auditQuery.add(AuditEntity.property("version").eq(version));
        return (JsonSchemaDetails)auditQuery.getSingleResult();
    }

    private Long getIdByName(String name, Session session) {
        Query query = session.createNamedQuery(JsonSchemaDetails.GET_ID_BY_NAME);
        query.setParameter("name", name);
        return (Long) query.getSingleResult();
    }

}


