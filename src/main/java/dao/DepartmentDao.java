package dao;

import model.Department;
import model.Lector;

import java.util.List;
import java.util.Map;

public interface DepartmentDao {

    String findHeadOfDepartmentByName(String departmentName);

    List<Department> findAll();

    void save(Department department);

    void delete(Integer id);

    void update(Department departmentName, Integer id);

    Double getAverageSalaryForDepartment(String departmentName);

    Integer getCountOfEmployeeForDepartment(String departmentName);

    Map<String, List<Lector>> getStatisticForDepartment(String departmentName);






}
