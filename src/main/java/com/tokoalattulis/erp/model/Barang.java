package com.tokoalattulis.erp.model;

public class Barang {
    private String kode;
    private String nama;
    private String kategori;
    private long harga;
    private int stok;

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
}
