package com.castlight.dataversioningpoc.automaticsemanticversions;

import com.castlight.dataversioningpoc.hibernateenvers.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.Query;
import java.util.List;


public class TestJsonObjectDetailsDAO {

    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public List<String> getAllTestJsonForSchema(Long jsonSchemaId) {
        Session session = sessionFactory.openSession();
        Query query = session.createNamedQuery(TestJsonObjectDetails.GET_ALL_TEST_JSON_BY_SCHEMA_ID);
        query.setParameter("jsonSchemaId", jsonSchemaId);
        return (List<String>) query.getResultList();
    }
}
