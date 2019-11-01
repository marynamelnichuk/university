package dao;

import exception.DaoOperationException;
import model.Lector;

import java.util.List;

public interface LectorDao {

    Lector findHeadOfDepartmentByName(String departmentName) throws DaoOperationException;

    List<Lector> findByInfo(String info) throws DaoOperationException;

    void save(Lector lector) throws DaoOperationException;

    List<Lector> findAll() throws DaoOperationException;

    void delete(Integer id) throws DaoOperationException;

    void update(Lector lector) throws DaoOperationException;

}
