package com.tokoalattulis.erp.model;

import com.tokoalattulis.erp.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaksiModel {
    public static class CartItem {
        public String kode, nama;
        public int harga, qty;
        public CartItem(String k, String n, int h, int q) {
            kode = k; nama = n; harga = h; qty = q;
        }
        public int subtotal() { return harga * qty; }
    }

    public long simpanTransaksi(List<CartItem> cart, int userId) throws Exception {
        long totalSum = cart.stream().mapToLong(CartItem::subtotal).sum();
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                long id;
                String sqlTx = "INSERT INTO transaksi(total,user_id) VALUES(?,?)";
                try (PreparedStatement p = c.prepareStatement(sqlTx, Statement.RETURN_GENERATED_KEYS)) {
                    p.setLong(1, totalSum);
                    p.setInt(2, userId);
                    p.executeUpdate();
                    try (ResultSet r = p.getGeneratedKeys()) {
                        if (!r.next()) throw new SQLException("ID transaksi tidak terbentuk.");
                        id = r.getLong(1);
                    }
                }
                for (CartItem it : cart) {
                    String sqlBarang = "UPDATE barang SET stok=stok-? WHERE kode=? AND stok>=?";
                    try (PreparedStatement p = c.prepareStatement(sqlBarang)) {
                        p.setInt(1, it.qty);
                        p.setString(2, it.kode);
                        p.setInt(3, it.qty);
                        if (p.executeUpdate() != 1) {
                            throw new SQLException("Stok barang " + it.nama + " tidak mencukupi.");
                        }
                    }
                    String sqlDetail = "INSERT INTO detail_transaksi(id_transaksi,kode_barang,jumlah,harga_satuan,subtotal) VALUES(?,?,?,?,?)";
                    try (PreparedStatement p = c.prepareStatement(sqlDetail)) {
                        p.setLong(1, id);
                        p.setString(2, it.kode);
                        p.setInt(3, it.qty);
                        p.setInt(4, it.harga);
                        p.setInt(5, it.subtotal());
                        p.executeUpdate();
                    }
                }
                c.commit();
                return id;
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            }
        }
    }

    public List<Object[]> getRiwayatTransaksi() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT tr.id,tr.tanggal,tr.total,COALESCE(u.username,'-') " +
                     "FROM transaksi tr LEFT JOIN users u ON u.id=tr.user_id ORDER BY tr.id DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                list.add(new Object[]{
                    r.getInt(1),
                    r.getString(2),
                    r.getLong(3),
                    r.getString(4)
                });
            }
        }
        return list;
    }

    public List<Object[]> getDetailTransaksi(int idTransaksi) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT dt.kode_barang,b.nama,dt.harga_satuan,dt.jumlah,dt.subtotal " +
                     "FROM detail_transaksi dt JOIN barang b ON b.kode=dt.kode_barang WHERE dt.id_transaksi=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, idTransaksi);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    list.add(new Object[]{
                        r.getString(1),
                        r.getString(2),
                        r.getLong(3),
                        r.getInt(4),
                        r.getLong(5)
                    });
                }
            }
        }
        return list;
    }

    public long getScalarCount(String sql) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            return r.next() ? r.getLong(1) : 0;
        }
    }
}
