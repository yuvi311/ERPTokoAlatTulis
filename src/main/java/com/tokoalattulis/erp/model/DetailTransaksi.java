package com.tokoalattulis.erp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detail_transaksi")
public class DetailTransaksi {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_transaksi", nullable = false)
    private Transaksi transaksi;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kode_barang", nullable = false)
    private Barang barang;

    @Column(nullable = false)
    private int jumlah;

    @Column(name = "harga_satuan", nullable = false)
    private long hargaSatuan;

    @Column(nullable = false)
    private long subtotal;

    public DetailTransaksi() {}
    public DetailTransaksi(Barang barang, int jumlah, long hargaSatuan) {
        this.barang = barang;
        this.jumlah = jumlah;
        this.hargaSatuan = hargaSatuan;
        this.subtotal = hargaSatuan * jumlah;
    }

    public Integer getId() { return id; }
    public Transaksi getTransaksi() { return transaksi; }
    public void setTransaksi(Transaksi transaksi) { this.transaksi = transaksi; }
    public Barang getBarang() { return barang; }
    public int getJumlah() { return jumlah; }
    public long getHargaSatuan() { return hargaSatuan; }
    public long getSubtotal() { return subtotal; }
}
