package ba.unsa.etf.si.persistance;

import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.utility.HibernateFactory;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class ProductRepository implements Repository<Product> {
    @Override
    public void update(Product item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Product item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.delete(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public void add(Product item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public Product get(Long id) {
        Product product;
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            product = session.get(Product.class, id);
            session.getTransaction().commit();
        }
        return product;
    }

    @Override
    public List<Product> getAll() {
        List<Product> list;
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Product> cq = cb.createQuery(Product.class);
            Root<Product> root = cq.from(Product.class);
            CriteriaQuery<Product> all = cq.select(root);
            TypedQuery<Product> allQuery = session.createQuery(all);
            list = allQuery.getResultList();
            session.getTransaction().commit();
        }
        return list;
    }
}
