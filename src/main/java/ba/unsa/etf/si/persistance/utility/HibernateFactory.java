package ba.unsa.etf.si.persistance.utility;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateFactory {

    private static final SessionFactory sessionFactory;

    private HibernateFactory() {
    }

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.createNativeQuery("use si2019").executeUpdate();
        session.getTransaction().commit();
        session.close();
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}