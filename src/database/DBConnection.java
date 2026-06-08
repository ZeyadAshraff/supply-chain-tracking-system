package database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {
    private static DBConnection instance;
    private final String url;
    private final String username;
    private final String password;

    private DBConnection() {
        try (InputStream input = loadDbProperties()) {
            if (input == null) {
                throw new IllegalStateException("db.properties was not found (tried db.properties and database/db.properties)");
            }
            Properties props = new Properties();
            props.load(input);
            this.url = getPropertySafe(props, "db.url");
            this.username = getPropertySafe(props, "db.username");
            this.password = getPropertySafe(props, "db.password");
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException("Failed to initialize DB connection", ex);
        }
    }

    private String getPropertySafe(Properties props, String key) {
        String value = props.getProperty(key);
        if (value != null) {
            return value;
        }
        // Handle UTF-8 BOM accidentally saved at start of properties file.
        return props.getProperty("\uFEFF" + key);
    }

    private InputStream loadDbProperties() {
        InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
        if (input != null) {
            return input;
        }
        return DBConnection.class.getClassLoader().getResourceAsStream("database/db.properties");
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
