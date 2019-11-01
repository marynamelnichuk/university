package dao.impl;

import dao.LectorDao;
import exception.DaoOperationException;
import exception.EntityNotFoundException;
import model.Degree;
import model.Lector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utills.HikariCPDataSource;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;

public class LectorDaoImpl implements LectorDao {


    private static final Logger logger = LoggerFactory.getLogger(LectorDaoImpl.class);

    private final String SELECT_ALL_LECTORS_SQL = "SELECT * FROM lectors;";
    private final String INSERT_LECTOR_SQL = "INSERT INTO lectors(name, surname, degree, salary) VALUES(?,?,?,?);";
    private final String DELETE_LECTOR_SQL = "DELETE FROM lectors WHERE id = ?";
    private final String UPDATE_LECTOR_SQL = "UPDATE lectors SET name =?, surname = ?, degree = ?, salary = ? WHERE id = ?;";
    private final String SELECT_NAME_LECTORS_SQL = "SELECT * FROM lectors WHERE name LIKE ";
    private final String SELECT_SURNAME_LECTORS_SQL = "SELECT * FROM lectors WHERE surname LIKE ";
    private final String SELECT_HEAD_OF_DEPARTMANT_SQL = "SELECT lectors.* FROM departments INNER JOIN lectors " +
            "ON departments.head_of_department = lectors.id WHERE departments.name = ?;";

    public List<Lector> findByInfo(String info) {
        logger.info("Trying search by template : " + info);
        List<Lector> foundedLectors = new ArrayList<>(findByParameter(info, SELECT_NAME_LECTORS_SQL));
        foundedLectors.addAll(findByParameter(info, SELECT_SURNAME_LECTORS_SQL));
        return foundedLectors;
    }

    public Lector findHeadOfDepartmentByName(String departmentName) throws DaoOperationException {
        logger.info("Trying find head of department with name : " + departmentName);
        try (Connection connection = HikariCPDataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_HEAD_OF_DEPARTMANT_SQL);
            statement.setString(1, departmentName);
            ResultSet rs = statement.executeQuery();
            Lector lector = null;
            while (rs.next()) {
                lector = parseRow(rs);
            }
            if(lector == null) {
                logger.warn("Can't find head of department with name of department :" + departmentName);
                throw new EntityNotFoundException("Can't find head of department with name of department: " + departmentName);
            }
            return lector;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoOperationException(e.getMessage(), e);
        }
    }

    private List<Lector> findByParameter(String parameter, String query) throws DaoOperationException {
        try (Connection connection = HikariCPDataSource.getConnection()) {
            Statement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery(query + "'%" + parameter + "%'");
            return collectToList(rs);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoOperationException(e.getMessage(), e);
        }
    }

    public void save(Lector lector) throws DaoOperationException {
        logger.info("Trying save lector : " + lector);
        try (Connection connection = HikariCPDataSource.getConnection()) {
            saveLector(lector, connection);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoOperationException("Can` save lector: " + lector, e);
        }
    }

    private void saveLector(Lector lector, Connection connection) throws SQLException {
        PreparedStatement insertStatement = prepareInsertStatement(connection, lector);
        executeUpdate(insertStatement, "Lector was not created");
        Integer id = fetchGeneratedId(insertStatement);
        lector.setId(id);
    }

    private PreparedStatement prepareInsertStatement(Connection connection, Lector lector) {
        try {
            PreparedStatement insertStatement = connection.prepareStatement(INSERT_LECTOR_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            return fillStatementWithLectorData(insertStatement, lector);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoOperationException("Cannot prepare statement to insert lector", e);
        }
    }

    private Integer fetchGeneratedId(PreparedStatement insertStatement) throws SQLException {
        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            logger.warn("Can not obtain an lector ID");
            throw new DaoOperationException("Can not obtain an lector ID");
        }
    }

    private void executeUpdate(PreparedStatement insertStatement, String errorMessage) throws SQLException {
        int rowsAffected = insertStatement.executeUpdate();
        if (rowsAffected == 0) {
            logger.warn(errorMessage);
            throw new DaoOperationException(errorMessage);
        }
    }

    private PreparedStatement fillStatementWithLectorData(PreparedStatement insertStatement, Lector lector)
            throws SQLException {
        insertStatement.setString(1, lector.getName());
        insertStatement.setString(2, lector.getSurname());
        insertStatement.setString(3, lector.getDegree() + "");
        insertStatement.setDouble(4, lector.getSalary());
        return insertStatement;
    }


    public List<Lector> findAll() throws DaoOperationException {
        logger.info("Trying find all lectors. ");
        try (Connection connection = HikariCPDataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SELECT_ALL_LECTORS_SQL);
            return collectToList(rs);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoOperationException("Can't find all lectors ", e);
        }
    }

    public void delete(Integer id) throws DaoOperationException {
        logger.info("Trying delete lector with id: " + id);
        try (Connection connection = HikariCPDataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE_LECTOR_SQL);
            statement.setInt(1, id);
            int check = statement.executeUpdate();
            if (check == 0) {
                logger.warn("Can't deleted lector");
            } else logger.info("Lector was deleted successful");
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoOperationException("Can't delete lector with id: " + id, e);
        }
    }

    public void update(Lector lector) {
        logger.info("Trying update lector: " + lector);
        try (Connection connection = HikariCPDataSource.getConnection()) {
            PreparedStatement updateStatement = prepareUpdateStatement(lector, connection);
            executeUpdate(updateStatement, "Lector was not updated");
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoOperationException(String.format("Cannot update lector with id = %d", lector.getId()), e);
        }

    }

    private PreparedStatement prepareUpdateStatement(Lector lector, Connection connection) {
        try {
            PreparedStatement updateStatement = connection.prepareStatement(UPDATE_LECTOR_SQL);
            fillStatementWithLectorData(updateStatement, lector);
            updateStatement.setInt(5, lector.getId());
            return updateStatement;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DaoOperationException(String.format("Cannot prepare update statement for lector id = %d", lector.getId()), e);
        }
    }

    private Lector parseRow(ResultSet rs) throws SQLException {
        Lector lector = new Lector();
        lector.setId(rs.getInt("id"));
        lector.setName(rs.getString("name"));
        lector.setSurname(rs.getString("surname"));
        lector.setDegree(Degree.valueOf(rs.getString("degree")));
        lector.setSalary(rs.getDouble("salary"));
        return lector;
    }

   private List<Lector> collectToList(ResultSet rs) throws SQLException {
        List<Lector> lectorList = new ArrayList<>();
        while (rs.next()) {
            Lector lector = parseRow(rs);
            lectorList.add(lector);
        }
        return lectorList;
    }


}
