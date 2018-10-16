package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Encapsulate JDBC settings for database access
 */
public class DB {
    private String driver;
    private String URLdb;
    private Connection connection;

    private static DB database = null;

    private DB(String dbName) throws ClassNotFoundException, SQLException {
        this.driver = "org.apache.derby.jdbc.ClientDriver";
        this.URLdb = "jdbc:derby://localhost:1527/" + dbName;
        Class.forName(driver); // driver loading
        this.connection = DriverManager.getConnection(URLdb);
    }

    public static DB getDB(String dbName) throws Exception {
        if (database == null) {
            database = new DB(dbName);
        }
        return database;
    }

    public PreparedStatement prepare(String stmtString)
            throws SQLException {
        PreparedStatement prepStmt
                = this.connection.prepareStatement(stmtString);
        return prepStmt;
    }
}
