package dao;

import model.Lector;

import java.util.List;

public interface LectorDao {

    Lector findHeadOfDepartmentByName(String departmentName);

    List<Lector> findByInfo(String info);

    void save(Lector lector);

    List<Lector> findAll();

    void delete(Integer id);

    void update(Lector lector);

}
