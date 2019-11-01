package dao.impl;

import dao.DepartmentDao;
import exception.DaoOperationException;
import model.Department;
import utills.HikariCPDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentDaoImpl implements DepartmentDao {

    private final String SELECT_ALL_DEPARTMENTS_SQL = "SELECT * FROM departments";
    private final String INSERT_DEPARTMENT_SQL ="INSERT INTO departments(name, institute) VALUES(?,?);";
    private final String DELETE_DEPARTMENT_SQL = "DELETE FROM departments WHERE id = ?";
    private final String SELECT_AVERAGE_SALARY = "SELECT avg(lectors.salary) AS salary_avg FROM lectors INNER JOIN\n" +
            "department_lector ON department_lector.lector_id = lectors.id\n" +
            "INNER JOIN departments ON department_lector.department_id = departments.id\n" +
            "WHERE departments.name = ?;";
    private final String UPDATE_DEPARTMENT_SQL = "UPDATE departments SET name =?, institute = ?, head_of_department = ? WHERE id = ?;";
    private final String SELECT_COUNT_OF_EMPLOYEE = "SELECT COUNT(*) AS count_of_employee FROM department_lector INNER JOIN departments\n" +
            "ON department_lector.department_id = departments.id\n" +
            "WHERE departments.name= ?;";
    private final String SELECT_STATISTIC = "SELECT degree, COUNT(department_lector.lector_id) AS count_of_lectors FROM lectors INNER JOIN department_lector\n" +
            "ON lectors.id = department_lector.lector_id\n" +
            "INNER JOIN departments ON department_lector.department_id = departments.id\n" +
            "WHERE departments.name = ? GROUP BY degree;";

    public List<Department> findAll() {
        try (Connection connection = HikariCPDataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SELECT_ALL_DEPARTMENTS_SQL);
            return collectToList(rs);
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
    }

    private Department parseRow(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt("id"));
        department.setInstitute(rs.getString("institute"));
        department.setName(rs.getString("name"));
        department.setHeadOfDepartment(rs.getInt("head_of_department"));
        return department;
    }

    private List<Department> collectToList(ResultSet rs) throws SQLException {
        List<Department> departmentsList = new ArrayList<>();
        while (rs.next()) {
            Department department = parseRow(rs);
            departmentsList.add(department);
        }
        return departmentsList;
    }

    public void save(Department department) {
        try (Connection connection = HikariCPDataSource.getConnection()) {
            saveDepartment(department, connection);
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage(), e);
        }
    }

    private void saveDepartment(Department department, Connection connection) throws SQLException {
        PreparedStatement insertStatement = prepareInsertStatement(connection, department);
        executeUpdate(insertStatement, "Department was not created");
        Integer id = fetchGeneratedId(insertStatement);
        department.setId(id);
    }

    private PreparedStatement prepareInsertStatement(Connection connection, Department department) {
        try {
            PreparedStatement insertStatement = connection.prepareStatement(INSERT_DEPARTMENT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            return fillStatementWithDepartmentData(insertStatement, department);
        } catch (SQLException e) {
            throw new DaoOperationException("Cannot prepare statement to insert department", e);
        }
    }

    private void executeUpdate(PreparedStatement insertStatement, String errorMessage) throws SQLException {
        int rowsAffected = insertStatement.executeUpdate();
        if (rowsAffected == 0) {
            throw new DaoOperationException(errorMessage);
        }
    }

    private PreparedStatement fillStatementWithDepartmentData(PreparedStatement insertStatement, Department department)
            throws SQLException {
        insertStatement.setString(1, department.getName());
        insertStatement.setString(2, department.getInstitute());
        insertStatement.setInt(3, department.getHeadOfDepartment());
        return insertStatement;
    }

    private Integer fetchGeneratedId(PreparedStatement insertStatement) throws SQLException {
        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new DaoOperationException("Can not obtain department ID");
        }
    }

    public void delete(Integer id) {
        try (Connection connection = HikariCPDataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE_DEPARTMENT_SQL);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
    }

    public void update(Department department) {
        try (Connection connection = HikariCPDataSource.getConnection()) {
            PreparedStatement updateStatement = prepareUpdateStatement(department, connection);
            executeUpdate(updateStatement, "Department was not updated");
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot update department with id = %d", department.getId()), e);
        }

    }

    private PreparedStatement prepareUpdateStatement(Department department, Connection connection) {
        try {
            PreparedStatement updateStatement = connection.prepareStatement(UPDATE_DEPARTMENT_SQL);
            fillStatementWithDepartmentData(updateStatement, department);
            updateStatement.setInt(4, department.getId());
            return updateStatement;
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Cannot prepare update statement for department id = %d", department.getId()), e);
        }
    }

    public Double getAverageSalaryForDepartment(String departmentName) {
        try (Connection connection = HikariCPDataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_AVERAGE_SALARY);
            statement.setString(1, departmentName);
            ResultSet rs = statement.executeQuery();
            double averageSalary = 0;
            while (rs.next()) {
                averageSalary = rs.getDouble("salary_avg");
            }
            return averageSalary;
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
    }

    public Integer getCountOfEmployeeForDepartment(String departmentName) {
        try (Connection connection = HikariCPDataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_COUNT_OF_EMPLOYEE);
            statement.setString(1, departmentName);
            ResultSet rs = statement.executeQuery();
            int countOfEmployee = 0;
            while (rs.next()) {
                countOfEmployee = rs.getInt("count_of_employee");
            }
            return countOfEmployee;
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
    }

    public Map<String, Integer> getStatisticForDepartment(String departmentName) {
        try (Connection connection = HikariCPDataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_STATISTIC);
            statement.setString(1, departmentName);
            ResultSet rs = statement.executeQuery();
            Map<String, Integer> statistic = new HashMap<>();
            String degree = "";
            int countOfLectors = 0;
            while (rs.next()) {
                degree = rs.getString("degree");
                countOfLectors = rs.getInt("count_of_lectors");
                statistic.put(degree, countOfLectors);
            }
            return statistic;
        } catch (SQLException e) {
            throw new DaoOperationException(e.getMessage());
        }
    }
}




