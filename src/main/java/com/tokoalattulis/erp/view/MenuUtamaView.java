package com.tokoalattulis.erp.view;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Locale;

public class MenuUtamaView extends JFrame {
    private final String username;
    private final NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    private JButton btnDataBarang;
    private JButton btnPersediaan;
    private JButton btnTransaksi;
    private JButton btnRiwayat;
    private JButton btnLaporan;
    private JButton btnLogout;

    public MenuUtamaView(String username) {
        this.username = username;
        setTitle("ERP Toko Alat Tulis - Menu Utama (MVC)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(560, 430);
        setLocationRelativeTo(null);
        build();
    }

    private void build() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel("ERP TOKO ALAT TULIS", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel sub = new JLabel("Selamat datang, " + username, SwingConstants.CENTER);
        sub.setForeground(new Color(90, 90, 90));

        JPanel head = new JPanel(new GridLayout(2, 1, 2, 2));
        head.add(title);
        head.add(sub);
        root.add(head, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(3, 2, 12, 12));

        btnDataBarang = createMenuButton("Data Barang", "Kelola data barang");
        btnPersediaan = createMenuButton("Persediaan Barang", "Stok masuk dan keluar");
        btnTransaksi = createMenuButton("Transaksi Penjualan", "Buat transaksi baru");
        btnRiwayat = createMenuButton("Riwayat Transaksi", "Lihat transaksi tersimpan");
        btnLaporan = createMenuButton("Laporan", "Ringkasan stok dan penjualan");
        btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 15));

        menu.add(btnDataBarang);
        menu.add(btnPersediaan);
        menu.add(btnTransaksi);
        menu.add(btnRiwayat);
        menu.add(btnLaporan);
        menu.add(btnLogout);

        root.add(menu, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JButton createMenuButton(String title, String desc) {
        JButton b = new JButton("<html><center><b>" + title + "</b><br><small>" + desc + "</small></center></html>");
        b.setFont(new Font("SansSerif", Font.PLAIN, 15));
        return b;
    }

    public String getUsername() { return username; }
    public NumberFormat getRupiahFormatter() { return rupiah; }

    public void addDataBarangListener(ActionListener l) { btnDataBarang.addActionListener(l); }
    public void addPersediaanListener(ActionListener l) { btnPersediaan.addActionListener(l); }
    public void addTransaksiListener(ActionListener l) { btnTransaksi.addActionListener(l); }
    public void addRiwayatListener(ActionListener l) { btnRiwayat.addActionListener(l); }
    public void addLaporanListener(ActionListener l) { btnLaporan.addActionListener(l); }
    public void addLogoutListener(ActionListener l) { btnLogout.addActionListener(l); }

    public void showMsg(String msg) { JOptionPane.showMessageDialog(this, msg); }
    public void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Kesalahan", JOptionPane.ERROR_MESSAGE); }
}
