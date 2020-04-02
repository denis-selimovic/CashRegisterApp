package ba.unsa.etf.si.persistance;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.util.List;

public interface Repository<T> {

    void update(T item);
    void delete(T item);
    void add(T item);
    T get(Long id);
    List<T> getAll();
}
