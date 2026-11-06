package com.tokoalattulis.erp.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "barang")
@Check(constraints = "harga>=0 AND stok>=0")
public class Barang {
    @Id
    private String kode;

    @Column(nullable = false)
    private String nama;

    @Column(nullable = false)
    private String kategori;

    @Column(nullable = false)
    private long harga;

    @Column(nullable = false)
    private int stok;

    // Diisi database (DEFAULT CURRENT_TIMESTAMP), hanya dibaca oleh ORM.
    @Column(name = "created_at", insertable = false, updatable = false,
            columnDefinition = "TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private String createdAt;

    public Barang() {}

    public Barang(String kode, String nama, String kategori, long harga, int stok) {
        this.kode = kode;
        this.nama = nama;
        this.kategori = kategori;
        this.harga = harga;
        this.stok = stok;
    }

    public String getKode() { return kode; }
    public void setKode(String kode) { this.kode = kode; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public long getHarga() { return harga; }
    public void setHarga(long harga) { this.harga = harga; }
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }
    public String getCreatedAt() { return createdAt; }
}
