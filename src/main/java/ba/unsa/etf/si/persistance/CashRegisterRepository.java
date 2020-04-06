package ba.unsa.etf.si.persistance;

import ba.unsa.etf.si.models.CashRegister;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.utility.HibernateFactory;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class CashRegisterRepository implements Repository<CashRegister> {
    @Override
    public void update(CashRegister item) {

    }

    @Override
    public void delete(CashRegister item) {

    }

    @Override
    public void add(CashRegister item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public CashRegister get(Long id) {
        List<CashRegister> list = getAll();
        return (list.size() != 0) ? list.get(0) : null;
    }

    @Override
    public List<CashRegister> getAll() {
        List<CashRegister> list;
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<CashRegister> cq = cb.createQuery(CashRegister.class);
            Root<CashRegister> root = cq.from(CashRegister.class);
            CriteriaQuery<CashRegister> all = cq.select(root);
            TypedQuery<CashRegister> allQuery = session.createQuery(all);
            list = allQuery.getResultList();
            session.getTransaction().commit();
        }
        return list;
    }
}
