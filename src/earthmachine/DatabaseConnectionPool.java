/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Christopher Brett
 */
public class DatabaseConnectionPool extends ObjectPool<Connection> {

    private static final String DATABASE_DRIVER = ServerEnvironmentVariables.DATABASE_DRIVER;
    private static final String DATABASE_URL = ServerEnvironmentVariables.DATABASE_URL;
    private static final String USERNAME = ServerEnvironmentVariables.DATABASE_USERNAME;
    private static final String PASSWORD = ServerEnvironmentVariables.DATABASE_PASSWORD;
    private static final String MAX_POOL = ServerEnvironmentVariables.DATABASE_MAX_POOL;

    // init properties object
    private Properties properties;

    public DatabaseConnectionPool() {
        super();
        properties = getProperties();
        try {
            Class.forName(DATABASE_DRIVER);
            System.out.println("Created Database Connection Pool!!!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(System.out);
            System.out.println("An Error Occurred Creating Database Connection Pool");
        }
    }

    // create properties
    private Properties getProperties() {
        properties = new Properties();
        properties.setProperty("user", USERNAME);
        properties.setProperty("password", PASSWORD);
        properties.setProperty("MaxPooledStatements", MAX_POOL);
        return properties;
    }

    @Override
    protected Connection create() {
        try {
            return DriverManager.getConnection(DATABASE_URL, properties);
        } catch (SQLException e) {
            e.printStackTrace(System.out);
            System.out.println("An Error Occurred Creating Database Connection");
            return null;
        }
    }

    @Override
    public boolean validate(Connection o) {
        try {
            return (!((Connection) o).isClosed());
        } catch (SQLException e) {
            e.printStackTrace(System.out);
            System.out.println("An Error Occured Validating Database Connection");
            return false;
        }
    }

    @Override
    public void expire(Connection o) {
        try {
            ((Connection) o).close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
            System.out.println("An Error Occurred Closing Database Connection");
        }
    }
}

