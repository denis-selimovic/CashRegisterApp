package ba.unsa.etf.si.persistance.repository;

import ba.unsa.etf.si.App;
import ba.unsa.etf.si.models.CashRegister;
import ba.unsa.etf.si.models.Credentials;
import ba.unsa.etf.si.persistance.utility.HibernateFactory;
import ba.unsa.etf.si.persistance.utility.Repository;
import org.hibernate.Session;

import java.util.List;

public class CashRegisterRepository implements Repository<CashRegister> {

    @Override
    public void update(CashRegister item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(CashRegister item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.delete(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public void add(CashRegister item) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public CashRegister get(Long id) {
        CashRegister cashRegister = null;
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            cashRegister = session.get(CashRegister.class, id);
            session.getTransaction().commit();
        }
        return cashRegister;
    }

    @Override
    public List<CashRegister> getAll() {
        return null;
    }

    public void setCashRegister() {
        if(get(App.cashRegister.getId()) == null) add(App.cashRegister);
    }
}
