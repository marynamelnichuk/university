package utills;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class JdbcUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/db_university?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static Connection connection;

    private JdbcUtil(){}

    private static Connection createConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static Connection getConnection() {
        if(connection == null) {
            connection = createConnection();
        }
        return connection;
    }

}
