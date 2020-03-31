package ba.unsa.etf.si.persistance;

import java.util.List;

public interface Repository<T> {

    void save(T item);
    void update(T item);
    void delete(T item);
    void add(T item);
    void addAll(List<T> items);
    T get(Long id);
    List<T> getAll();
}
