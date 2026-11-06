package com.tokoalattulis.erp.model;

import com.tokoalattulis.erp.util.HibernateUtil;
import java.util.List;

public class BarangModel {
    public List<Barang> getAllBarang(String keyword) {
        String q = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        return HibernateUtil.read(s -> s.createQuery(
                "from Barang where kode like :q or nama like :q order by nama", Barang.class)
            .setParameter("q", q)
            .list());
    }

    public Barang getBarangByKode(String kode) {
        return HibernateUtil.read(s -> s.get(Barang.class, kode));
    }

    public void insertBarang(Barang b) {
        HibernateUtil.tx(s -> { s.persist(b); return null; });
    }

    public void updateBarang(Barang b) {
        HibernateUtil.tx(s -> {
            Barang db = s.get(Barang.class, b.getKode());
            if (db == null) throw new IllegalStateException("Barang tidak ditemukan.");
            db.setNama(b.getNama());
            db.setKategori(b.getKategori());
            db.setHarga(b.getHarga());
            db.setStok(b.getStok());
            return null;
        });
    }

    public void deleteBarang(String kode) {
        HibernateUtil.tx(s -> {
            Barang db = s.get(Barang.class, kode);
            if (db != null) s.remove(db);
            return null;
        });
    }

    public List<Barang> getBarangTersedia() {
        return HibernateUtil.read(s -> s.createQuery(
                "from Barang where stok > 0 order by nama", Barang.class).list());
    }

    public List<Barang> getStokMenipis(int limit) {
        return HibernateUtil.read(s -> s.createQuery(
                "from Barang where stok <= :l order by stok", Barang.class)
            .setParameter("l", limit)
            .list());
    }

    public long hitungJenisBarang() {
        return HibernateUtil.read(s -> s.createQuery("select count(b) from Barang b", Long.class).uniqueResult());
    }

    public long totalUnitPersediaan() {
        return HibernateUtil.read(s -> s.createQuery("select coalesce(sum(b.stok),0) from Barang b", Long.class).uniqueResult());
    }
}
