package org.example.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.Session;


public class HibernateUtil {
    private static SessionFactory sessionFactory;

    static {
        try {
            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure().build();
            Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();

            sessionFactory = metadata.getSessionFactoryBuilder().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    public static Session getSession() {
        return sessionFactory.openSession();
    }

    public static void closeSession(Session session) {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
