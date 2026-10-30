package com.tokoalattulis.erp.util;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:tokoalattulis.db";

    public static Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement s = c.createStatement()) { 
            s.execute("PRAGMA foreign_keys=ON"); 
        }
        return c;
    }

    public static void initDatabase() {
        String[] ddl = {
            "CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL, nama_lengkap TEXT NOT NULL DEFAULT 'Administrator', role TEXT NOT NULL DEFAULT 'admin')",
            "CREATE TABLE IF NOT EXISTS barang(kode TEXT PRIMARY KEY, nama TEXT NOT NULL, kategori TEXT NOT NULL, harga INTEGER NOT NULL CHECK(harga>=0), stok INTEGER NOT NULL DEFAULT 0 CHECK(stok>=0), created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)",
            "CREATE TABLE IF NOT EXISTS transaksi(id INTEGER PRIMARY KEY AUTOINCREMENT, tanggal TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, total INTEGER NOT NULL CHECK(total>=0), user_id INTEGER, FOREIGN KEY(user_id) REFERENCES users(id))",
            "CREATE TABLE IF NOT EXISTS detail_transaksi(id INTEGER PRIMARY KEY AUTOINCREMENT, id_transaksi INTEGER NOT NULL, kode_barang TEXT NOT NULL, jumlah INTEGER NOT NULL CHECK(jumlah>0), harga_satuan INTEGER NOT NULL CHECK(harga_satuan>=0), subtotal INTEGER NOT NULL CHECK(subtotal>=0), FOREIGN KEY(id_transaksi) REFERENCES transaksi(id) ON DELETE CASCADE, FOREIGN KEY(kode_barang) REFERENCES barang(kode))",
            "CREATE TABLE IF NOT EXISTS mutasi_stok(id INTEGER PRIMARY KEY AUTOINCREMENT, tanggal TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, kode_barang TEXT NOT NULL, jenis TEXT NOT NULL CHECK(jenis IN ('MASUK','KELUAR')), jumlah INTEGER NOT NULL CHECK(jumlah>0), keterangan TEXT, FOREIGN KEY(kode_barang) REFERENCES barang(kode))"
        };
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            for (String q : ddl) s.execute(q);
            try (PreparedStatement p = c.prepareStatement("INSERT OR IGNORE INTO users(username,password,nama_lengkap,role) VALUES('admin','admin123','Administrator','admin')")) {
                p.executeUpdate();
            }
            String[][] data = {
                {"ATK001", "Buku Tulis", "Buku", "5000", "50"},
                {"ATK002", "Pulpen Hitam", "Pulpen", "3000", "100"},
                {"ATK003", "Pensil 2B", "Pensil", "2500", "75"},
                {"ATK004", "Penghapus", "Aksesori", "2000", "40"},
                {"ATK005", "Penggaris 30 cm", "Alat Ukur", "4000", "30"}
            };
            try (PreparedStatement p = c.prepareStatement("INSERT OR IGNORE INTO barang(kode,nama,kategori,harga,stok) VALUES(?,?,?,?,?)")) {
                for (String[] d : data) {
                    p.setString(1, d[0]);
                    p.setString(2, d[1]);
                    p.setString(3, d[2]);
                    p.setInt(4, Integer.parseInt(d[3]));
                    p.setInt(5, Integer.parseInt(d[4]));
                    p.addBatch();
                }
                p.executeBatch();
            }
            System.out.println("Database SQLite siap: tokoalattulis.db");
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menyiapkan database: " + e.getMessage(), e);
        }
    }
}
