package ba.unsa.etf.si.persistance;


import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.utility.HibernateFactory;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class ReceiptRepository implements Repository<Receipt> {

    @Override
    public void update(Receipt item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Receipt item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.delete(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public void add(Receipt item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public Receipt get(Long id) {
        Receipt receipt = null;
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            receipt = session.get(Receipt.class, id);
            session.getTransaction().commit();
        }
        return receipt;
    }

    @Override
    public List<Receipt> getAll() {
        List<Receipt> list;
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Receipt> cq = cb.createQuery(Receipt.class);
            Root<Receipt> root = cq.from(Receipt.class);
            CriteriaQuery<Receipt> all = cq.select(root);
            TypedQuery<Receipt> allQuery = session.createQuery(all);
            list = allQuery.getResultList();
            session.getTransaction().commit();
        }
        return list;
    }
}
