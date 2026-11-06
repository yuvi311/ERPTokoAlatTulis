package com.tokoalattulis.erp.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transaksi")
public class Transaksi {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(insertable = false, updatable = false,
            columnDefinition = "TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private String tanggal;

    @Column(nullable = false)
    private long total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "transaksi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailTransaksi> details = new ArrayList<>();

    public Transaksi() {}

    public void addDetail(DetailTransaksi d) {
        d.setTransaksi(this);
        details.add(d);
    }

    public Integer getId() { return id; }
    public String getTanggal() { return tanggal; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<DetailTransaksi> getDetails() { return details; }
}
