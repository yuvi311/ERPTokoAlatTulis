package com.tokoalattulis.erp.model;

import com.tokoalattulis.erp.util.HibernateUtil;
import java.util.List;

public class MutasiStokModel {
    /** Kolom: tanggal, kode, nama, jenis, jumlah, keterangan. */
    public List<Object[]> getAllMutasi() {
        return HibernateUtil.read(s -> s.createQuery(
                "select m.tanggal, b.kode, b.nama, m.jenis, m.jumlah, coalesce(m.keterangan,'') " +
                "from MutasiStok m join m.barang b order by m.id desc", Object[].class)
            .list());
    }

    public void simpanMutasi(String kode, String jenis, int jumlah, String keterangan) {
        HibernateUtil.tx(s -> {
            Barang b = s.get(Barang.class, kode);
            if (b == null) throw new IllegalStateException("Barang tidak ditemukan.");
            if (jenis.equals("KELUAR") && b.getStok() < jumlah) {
                throw new IllegalStateException("Stok tidak mencukupi. Stok saat ini: " + b.getStok());
            }
            b.setStok(jenis.equals("MASUK") ? b.getStok() + jumlah : b.getStok() - jumlah);
            s.persist(new MutasiStok(b, jenis, jumlah, keterangan));
            return null;
        });
    }
}
