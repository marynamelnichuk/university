package dao.impl;

import dao.DepartmentDao;
import exception.DaoOperationException;
import model.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utills.HikariCPDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentDaoImpl implements DepartmentDao {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentDaoImpl.class);

    private final String SELECT_ALL_DEPARTMENTS_SQL = "SELECT * FROM departments";
    private final String INSERT_DEPARTMENT_SQL ="INSERT INTO departments(name, institute, head_of_department) VALUES(?,?,);";
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
    private final String INSERT_DAPARTMENTS_AND_LECTOR = "INSERT INTO department_lector VALUES(?,?);";

    public List<Department> findAll() {
        logger.info("Trying find all departments.");
        try (Connection connection = HikariCPDataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SELECT_ALL_DEPARTMENTS_SQL);
            return collectToList(rs);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoOperationException("Can't find all departments", e);
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
        logger.info("Trying save department : " + department);
        try (Connection connection = HikariCPDataSource.getConnection()) {
            saveDepartment(department, connection);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoOperationException("Can`t add department: " + department, e);
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
            logger.error(e.getMessage());
            throw new DaoOperationException("Cannot prepare statement to insert department", e);
        }
    }

    private void executeUpdate(PreparedStatement insertStatement, String errorMessage) throws SQLException {
        int rowsAffected = insertStatement.executeUpdate();
        if (rowsAffected == 0) {
            logger.warn(errorMessage);
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
            logger.warn("Can not obtain an lector ID");
            throw new DaoOperationException("Can not obtain department ID");
        }
    }

    public void delete(Integer id) {
        logger.info("Trying delete department with id: " + id);
        try (Connection connection = HikariCPDataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE_DEPARTMENT_SQL);
            statement.setInt(1, id);
            int check = statement.executeUpdate();
            if (check == 0) {
                logger.warn("Can't deleted department");
            } else logger.info("Department was deleted successful");
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoOperationException("Can't delete department with id: " + id, e);
        }
    }

    public void update(Department department) {
        logger.info("Trying update department: " + department);
        try (Connection connection = HikariCPDataSource.getConnection()) {
            PreparedStatement updateStatement = prepareUpdateStatement(department, connection);
            executeUpdate(updateStatement, "Department was not updated");
        } catch (SQLException e) {
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
            throw new DaoOperationException(String.format("Cannot prepare update statement for department id = %d", department.getId()), e);
        }
    }

    public Double getAverageSalaryForDepartment(String departmentName) {
        logger.info("Trying get average salary for department: " + departmentName);
        try (Connection connection = HikariCPDataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_AVERAGE_SALARY);
            statement.setString(1, departmentName);
            ResultSet rs = statement.executeQuery();
            double averageSalary = 0;
            while (rs.next()) {
                averageSalary = rs.getDouble("salary_avg");
            }
            if(averageSalary == 0) {
                logger.info("Departments don`t have lectors.");
            }
            return averageSalary;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoOperationException("Can`t get average salary for department: " + departmentName, e);
        }
    }

    public Integer getCountOfEmployeeForDepartment(String departmentName) {
        logger.info("Trying get count of employee for department: " + departmentName);
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
            logger.error(e.getMessage());
            throw new DaoOperationException("Can`t get count of employee for department: " + departmentName, e);
        }
    }

    public Map<String, Integer> getStatisticForDepartment(String departmentName) {
        logger.info("Trying get statistic for department: " + departmentName);
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
            logger.error(e.getMessage());
            throw new DaoOperationException("Can`t get statistic for department: " + departmentName, e);
        }
    }

    @Override
    public void addLectorToDepartment(Integer departmentId, Integer lectorId) throws DaoOperationException {
        logger.info("Trying add lector with id : " + lectorId + " and department id : " + departmentId);
        try (Connection connection = HikariCPDataSource.getConnection()) {
            PreparedStatement insertStatement = connection.prepareStatement(INSERT_DAPARTMENTS_AND_LECTOR);
            insertStatement.setInt(1, departmentId);
            insertStatement.setInt(2, lectorId);
            executeUpdate(insertStatement, "Department was not created");
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoOperationException("Can`t add lector with id : " + lectorId + " and department id : " + departmentId, e);
        }

    }
}




