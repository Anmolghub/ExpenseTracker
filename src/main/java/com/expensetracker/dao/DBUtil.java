package com.expensetracker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
    private static final String DB_URL = "jdbc:sqlite:expense.db";

    static {
        initDB();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private static void initDB() {
        String sql = "CREATE TABLE IF NOT EXISTS expenses ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "date TEXT NOT NULL,"
                   + "category TEXT NOT NULL,"
                   + "amount REAL NOT NULL,"
                   + "note TEXT"
                   + ");";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("DB initialization error: " + e.getMessage());
        }
    }
}
