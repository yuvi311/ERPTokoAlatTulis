package com.tokoalattulis.erp.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "mutasi_stok")
@Check(constraints = "jenis IN ('MASUK','KELUAR') AND jumlah>0")
public class MutasiStok {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(insertable = false, updatable = false,
            columnDefinition = "TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private String tanggal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kode_barang", nullable = false)
    private Barang barang;

    @Column(nullable = false)
    private String jenis;

    @Column(nullable = false)
    private int jumlah;

    private String keterangan;

    public MutasiStok() {}
    public MutasiStok(Barang barang, String jenis, int jumlah, String keterangan) {
        this.barang = barang; this.jenis = jenis; this.jumlah = jumlah; this.keterangan = keterangan;
    }

    public Integer getId() { return id; }
    public String getTanggal() { return tanggal; }
    public Barang getBarang() { return barang; }
    public String getJenis() { return jenis; }
    public int getJumlah() { return jumlah; }
    public String getKeterangan() { return keterangan; }
}
