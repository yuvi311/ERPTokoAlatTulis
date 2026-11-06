package com.tokoalattulis.erp.model;

import com.tokoalattulis.erp.util.HibernateUtil;
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

    public long simpanTransaksi(List<CartItem> cart, int userId) {
        long totalSum = cart.stream().mapToLong(CartItem::subtotal).sum();
        return HibernateUtil.tx(s -> {
            Transaksi t = new Transaksi();
            t.setTotal(totalSum);
            t.setUser(s.getReference(User.class, userId));
            for (CartItem it : cart) {
                // Pengurangan stok atomik: gagal bila stok tidak cukup.
                int ok = s.createMutationQuery(
                        "update Barang set stok = stok - :q where kode = :k and stok >= :q")
                    .setParameter("q", it.qty)
                    .setParameter("k", it.kode)
                    .executeUpdate();
                if (ok != 1) throw new IllegalStateException("Stok barang " + it.nama + " tidak mencukupi.");
                t.addDetail(new DetailTransaksi(s.getReference(Barang.class, it.kode), it.qty, it.harga));
            }
            s.persist(t); // detail ikut tersimpan (cascade)
            return (long) t.getId();
        });
    }

    /** Kolom: id (Integer), tanggal, total (Long), username kasir. */
    public List<Object[]> getRiwayatTransaksi() {
        return HibernateUtil.read(s -> s.createQuery(
                "select t.id, t.tanggal, t.total, coalesce(u.username,'-') " +
                "from Transaksi t left join t.user u order by t.id desc", Object[].class)
            .list());
    }

    /** Kolom: kode, nama, harga satuan (Long), jumlah, subtotal (Long). */
    public List<Object[]> getDetailTransaksi(int idTransaksi) {
        return HibernateUtil.read(s -> s.createQuery(
                "select b.kode, b.nama, d.hargaSatuan, d.jumlah, d.subtotal " +
                "from DetailTransaksi d join d.barang b where d.transaksi.id = :id", Object[].class)
            .setParameter("id", idTransaksi)
            .list());
    }

    public long totalOmzet() {
        return HibernateUtil.read(s -> s.createQuery(
                "select coalesce(sum(t.total),0) from Transaksi t", Long.class).uniqueResult());
    }
}
