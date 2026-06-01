package com.fooddelivery.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class GenericRepository<T> {

    protected Connection connection;

    protected GenericRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Each subclass declares:  private static SubclassName instance;
    // and a public static getInstance() that creates it on first call.

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected int executeUpdate(String sql, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[" + getClass().getSimpleName() + "] executeUpdate failed: " + e.getMessage());
            return 0;
        }
    }

    // Caller is responsible for closing the returned ResultSet.
    // The PreparedStatement is kept open because closing it would invalidate the ResultSet.
    protected ResultSet executeQuery(String sql, Object... params) {
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            setParameters(stmt, params);
            return stmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("[" + getClass().getSimpleName() + "] executeQuery failed: " + e.getMessage());
            return null;
        }
    }

    private void setParameters(PreparedStatement stmt, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}
