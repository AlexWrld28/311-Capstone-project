package org.csc311.capstone.db;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory(System.getProperty("user.dir"))
            .ignoreIfMissing()
            .load();

    public static Connection getConnection() throws SQLException {
        String host = dotenv.get("DB_HOST");
        String port = dotenv.get("DB_PORT", "5432");
        String name = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        if (host == null || name == null || user == null || password == null) {
            throw new SQLException("Missing DB_HOST, DB_NAME, DB_USER, or DB_PASSWORD.");
        }

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + name;
        return DriverManager.getConnection(url, user, password);
    }
}