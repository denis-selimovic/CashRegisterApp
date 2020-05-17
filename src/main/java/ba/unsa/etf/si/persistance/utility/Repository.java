package ba.unsa.etf.si.persistance.utility;

import java.util.List;

public interface Repository<T> {

    void update(T item);

    void delete(T item);

    void add(T item);

    T get(Long id);

    List<T> getAll();
}
