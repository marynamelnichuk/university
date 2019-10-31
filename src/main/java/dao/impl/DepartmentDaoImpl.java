package dao.impl;

import dao.DepartmentDao;
import model.Department;
import model.Lector;

import java.util.List;
import java.util.Map;

public class DepartmentDaoImpl implements DepartmentDao {
    public String findHeadOfDepartmentByName(String departmentName) {
        return null;
    }

    public List<Department> findAll() {
        return null;
    }

    public void save(Department department) {

    }

    public void delete(Integer id) {

    }

    public void update(Department departmentName, Integer id) {

    }

    public Double getAverageSalaryForDepartment(String departmentName) {
        return null;
    }

    public Integer getCountOfEmployeeForDepartment(String departmentName) {
        return null;
    }

    public Map<String, List<Lector>> getStatisticForDepartment(String departmentName) {
        return null;
    }
}
