package com.tokoalattulis.erp.model;

import com.tokoalattulis.erp.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangModel {
    public List<Barang> getAllBarang(String keyword) throws SQLException {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT * FROM barang WHERE kode LIKE ? OR nama LIKE ? ORDER BY nama";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            String q = "%" + (keyword == null ? "" : keyword.trim()) + "%";
            p.setString(1, q);
            p.setString(2, q);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    list.add(new Barang(
                        r.getString("kode"),
                        r.getString("nama"),
                        r.getString("kategori"),
                        r.getLong("harga"),
                        r.getInt("stok")
                    ));
                }
            }
        }
        return list;
    }

    public Barang getBarangByKode(String kode) throws SQLException {
        String sql = "SELECT * FROM barang WHERE kode=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, kode);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return new Barang(
                        r.getString("kode"),
                        r.getString("nama"),
                        r.getString("kategori"),
                        r.getLong("harga"),
                        r.getInt("stok")
                    );
                }
            }
        }
        return null;
    }

    public void insertBarang(Barang b) throws SQLException {
        String sql = "INSERT INTO barang(kode,nama,kategori,harga,stok) VALUES(?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, b.getKode());
            p.setString(2, b.getNama());
            p.setString(3, b.getKategori());
            p.setLong(4, b.getHarga());
            p.setInt(5, b.getStok());
            p.executeUpdate();
        }
    }

    public void updateBarang(Barang b) throws SQLException {
        String sql = "UPDATE barang SET nama=?,kategori=?,harga=?,stok=? WHERE kode=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, b.getNama());
            p.setString(2, b.getKategori());
            p.setLong(3, b.getHarga());
            p.setInt(4, b.getStok());
            p.setString(5, b.getKode());
            p.executeUpdate();
        }
    }

    public void deleteBarang(String kode) throws SQLException {
        String sql = "DELETE FROM barang WHERE kode=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, kode);
            p.executeUpdate();
        }
    }

    public List<Barang> getBarangTersedia() throws SQLException {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT * FROM barang WHERE stok>0 ORDER BY nama";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                list.add(new Barang(
                    r.getString("kode"),
                    r.getString("nama"),
                    r.getString("kategori"),
                    r.getLong("harga"),
                    r.getInt("stok")
                ));
            }
        }
        return list;
    }

    public List<Barang> getStokMenipis(int limit) throws SQLException {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT kode,nama,stok FROM barang WHERE stok<=? ORDER BY stok";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, limit);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    Barang b = new Barang();
                    b.setKode(r.getString("kode"));
                    b.setNama(r.getString("nama"));
                    b.setStok(r.getInt("stok"));
                    list.add(b);
                }
            }
        }
        return list;
    }
}
