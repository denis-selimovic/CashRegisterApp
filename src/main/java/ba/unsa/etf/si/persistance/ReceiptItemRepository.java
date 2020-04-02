package ba.unsa.etf.si.persistance;

import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.ReceiptItem;
import ba.unsa.etf.si.utility.HibernateFactory;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class ReceiptItemRepository implements Repository<ReceiptItem> {
    @Override
    public void update(ReceiptItem item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(ReceiptItem item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.delete(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public void add(ReceiptItem item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public ReceiptItem get(Long id) {
        ReceiptItem receiptItem;
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            receiptItem = session.get(ReceiptItem.class, id);
            session.getTransaction().commit();
        }
        return receiptItem;
    }

    @Override
    public List<ReceiptItem> getAll() {
        List<ReceiptItem> list;
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ReceiptItem> cq = cb.createQuery(ReceiptItem.class);
            Root<ReceiptItem> root = cq.from(ReceiptItem.class);
            CriteriaQuery<ReceiptItem> all = cq.select(root);
            TypedQuery<ReceiptItem> allQuery = session.createQuery(all);
            list = allQuery.getResultList();
            session.getTransaction().commit();
        }
        return list;
    }
}
