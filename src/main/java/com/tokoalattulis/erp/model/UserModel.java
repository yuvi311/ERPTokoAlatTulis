package com.tokoalattulis.erp.model;

import com.tokoalattulis.erp.util.DatabaseConnection;
import java.sql.*;

public class UserModel {
    public boolean validasiLogin(String username, String password) {
        String sql = "SELECT 1 FROM users WHERE username=? AND password=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement q = c.prepareStatement(sql)) {
            q.setString(1, username);
            q.setString(2, password);
            try (ResultSet r = q.executeQuery()) {
                return r.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public int getUserId(String username) {
        String sql = "SELECT id FROM users WHERE username=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? r.getInt(1) : 1;
            }
        } catch (SQLException e) {
            return 1;
        }
    }
}
