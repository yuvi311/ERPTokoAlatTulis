package com.tokoalattulis.erp.model;

import com.tokoalattulis.erp.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MutasiStokModel {
    public List<Object[]> getAllMutasi() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT ms.tanggal,ms.kode_barang,b.nama,ms.jenis,ms.jumlah,COALESCE(ms.keterangan,'') " +
                     "FROM mutasi_stok ms JOIN barang b ON b.kode=ms.kode_barang ORDER BY ms.id DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                list.add(new Object[]{
                    r.getString(1),
                    r.getString(2),
                    r.getString(3),
                    r.getString(4),
                    r.getInt(5),
                    r.getString(6)
                });
            }
        }
        return list;
    }

    public void simpanMutasi(String kode, String jenis, int jumlah, String keterangan) throws Exception {
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int stok;
                try (PreparedStatement q = c.prepareStatement("SELECT stok FROM barang WHERE kode=?")) {
                    q.setString(1, kode);
                    try (ResultSet r = q.executeQuery()) {
                        if (!r.next()) throw new Exception("Barang tidak ditemukan.");
                        stok = r.getInt(1);
                    }
                }
                if (jenis.equals("KELUAR") && stok < jumlah) {
                    throw new Exception("Stok tidak mencukupi. Stok saat ini: " + stok);
                }
                String updateSql = "UPDATE barang SET stok=stok " + (jenis.equals("MASUK") ? "+" : "-") + " ? WHERE kode=?";
                try (PreparedStatement q = c.prepareStatement(updateSql)) {
                    q.setInt(1, jumlah);
                    q.setString(2, kode);
                    q.executeUpdate();
                }
                String insertSql = "INSERT INTO mutasi_stok(kode_barang,jenis,jumlah,keterangan) VALUES(?,?,?,?)";
                try (PreparedStatement q = c.prepareStatement(insertSql)) {
                    q.setString(1, kode);
                    q.setString(2, jenis);
                    q.setInt(3, jumlah);
                    q.setString(4, keterangan);
                    q.executeUpdate();
                }
                c.commit();
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            }
        }
    }
}
