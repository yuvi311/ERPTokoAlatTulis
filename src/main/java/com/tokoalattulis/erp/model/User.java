package com.tokoalattulis.erp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "nama_lengkap", nullable = false)
    private String namaLengkap = "Administrator";

    @Column(nullable = false)
    private String role = "admin";

    public User() {}
    public User(String username, String password, String namaLengkap, String role) {
        this.username = username; this.password = password;
        this.namaLengkap = namaLengkap; this.role = role;
    }

    public Integer getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
