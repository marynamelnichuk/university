package dao;

import model.Department;
import model.Lector;

import java.util.List;
import java.util.Map;

public interface DepartmentDao {

    List<Department> findAll();

    void save(Department department);

    void delete(Integer id);

    void update(Department departmentName);

    Double getAverageSalaryForDepartment(String departmentName);

    Integer getCountOfEmployeeForDepartment(String departmentName);

    Map<String, Integer> getStatisticForDepartment(String departmentName);






}
