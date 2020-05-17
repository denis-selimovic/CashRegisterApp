package ba.unsa.etf.si.persistance.repository;

import ba.unsa.etf.si.models.Credentials;
import ba.unsa.etf.si.persistance.utility.HibernateFactory;
import ba.unsa.etf.si.persistance.utility.Repository;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class CredentialsRepository implements Repository<Credentials> {
    @Override
    public void update(Credentials item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Credentials item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.delete(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public void add(Credentials item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public Credentials get(Long id) {
        Credentials credentials = null;
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            credentials = session.get(Credentials.class, id);
            session.getTransaction().commit();
        }
        return credentials;
    }

    @Override
    public List<Credentials> getAll() {
        return null;
    }

    public Credentials getByUsername(String username) {
        List<Credentials> list = null;
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Credentials> cq = cb.createQuery(Credentials.class);
            Root<Credentials> root = cq.from(Credentials.class);
            CriteriaQuery<Credentials> query = cq.select(root).where(cb.equal(root.get("username"), username));
            list = session.createQuery(query).getResultList();
            session.getTransaction().commit();
        }
        return (list.size() == 0) ? null : list.get(0);
    }
}
