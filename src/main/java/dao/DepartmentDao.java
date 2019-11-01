package dao;

import exception.DaoOperationException;
import model.Department;
import model.Lector;

import java.util.List;
import java.util.Map;

public interface DepartmentDao {

    List<Department> findAll() throws DaoOperationException;

    void save(Department department) throws DaoOperationException;

    void delete(Integer id) throws DaoOperationException;

    void update(Department departmentName) throws DaoOperationException;

    Double getAverageSalaryForDepartment(String departmentName) throws DaoOperationException;

    Integer getCountOfEmployeeForDepartment(String departmentName) throws DaoOperationException;

    Map<String, Integer> getStatisticForDepartment(String departmentName) throws DaoOperationException;

    void addLectorToDepartment(Integer departmentId, Integer lectorId) throws DaoOperationException;




}
