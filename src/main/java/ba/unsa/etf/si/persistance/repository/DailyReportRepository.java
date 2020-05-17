package ba.unsa.etf.si.persistance.repository;

import ba.unsa.etf.si.models.DailyReport;
import ba.unsa.etf.si.persistance.utility.HibernateFactory;
import ba.unsa.etf.si.persistance.utility.Repository;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class DailyReportRepository implements Repository<DailyReport> {

    @Override
    public void update(DailyReport dailyReport) {
        // forbidden
    }

    @Override
    public void delete(DailyReport dailyReport) {
        // forbidden
    }

    @Override
    public void add(DailyReport dailyReport) {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(dailyReport);
            session.getTransaction().commit();
        }
    }

    @Override
    public DailyReport get(Long id) {
        DailyReport dailyReport;
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            dailyReport = session.get(DailyReport.class, id);
            session.getTransaction().commit();
        }
        return dailyReport;
    }

    @Override
    public List<DailyReport> getAll() {
        List<DailyReport> list;
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<DailyReport> cq = cb.createQuery(DailyReport.class);
            Root<DailyReport> root = cq.from(DailyReport.class);
            CriteriaQuery<DailyReport> all = cq.select(root);
            TypedQuery<DailyReport> allQuery = session.createQuery(all);
            list = allQuery.getResultList();
            session.getTransaction().commit();
        }
        return list;
    }

    public DailyReport getMinDate() {
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<DailyReport> criteriaQuery = criteriaBuilder.createQuery(DailyReport.class);
            Root<DailyReport> root = criteriaQuery.from(DailyReport.class);

            criteriaQuery.select(root);
            criteriaQuery = criteriaQuery.orderBy(criteriaBuilder.desc(root.get("date")));

            DailyReport dailyReport = session.createQuery(criteriaQuery).getResultList().get(0);
            session.getTransaction().commit();

            return dailyReport;
        } catch (Exception e) {
            return new DailyReport();
        }
    }

    public DailyReport getByDate(String date) {
        List<DailyReport> list;
        try (Session session = HibernateFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<DailyReport> cq = cb.createQuery(DailyReport.class);
            Root<DailyReport> root = cq.from(DailyReport.class);
            CriteriaQuery<DailyReport> query = cq.select(root).where(cb.equal(root.get("date"), date));
            list = session.createQuery(query).getResultList();
            session.getTransaction().commit();
        }
        return (list.size() == 0) ? null : list.get(0);
    }
}
