package com.tokoalattulis.erp.controller;

import com.tokoalattulis.erp.model.*;
import com.tokoalattulis.erp.view.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MenuUtamaController {
    private final MenuUtamaView view;
    private final UserModel userModel;
    private final BarangModel barangModel;
    private final MutasiStokModel mutasiModel;
    private final TransaksiModel transaksiModel;
    private final NumberFormat rupiah;

    public MenuUtamaController(MenuUtamaView view, UserModel userModel) {
        this.view = view;
        this.userModel = userModel;
        this.barangModel = new BarangModel();
        this.mutasiModel = new MutasiStokModel();
        this.transaksiModel = new TransaksiModel();
        this.rupiah = view.getRupiahFormatter();

        this.view.addDataBarangListener(e -> openDataBarang());
        this.view.addPersediaanListener(e -> openPersediaan());
        this.view.addTransaksiListener(e -> openTransaksi());
        this.view.addRiwayatListener(e -> openRiwayat());
        this.view.addLaporanListener(e -> openLaporan());
        this.view.addLogoutListener(e -> handleLogout());
    }

    private DefaultTableModel createNonEditableModel(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private JTable createTable(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setRowHeight(24);
        t.setAutoCreateRowSorter(true);
        return t;
    }

    private void handleLogout() {
        LoginForm loginForm = new LoginForm();
        new LoginController(loginForm, userModel);
        loginForm.setVisible(true);
        view.dispose();
    }

    // --- DATA BARANG ---
    private void openDataBarang() {
        JDialog d = new JDialog(view, "Pengelolaan Data Barang", true);
        d.setSize(820, 480);
        d.setLocationRelativeTo(view);

        DefaultTableModel m = createNonEditableModel("Kode", "Nama Barang", "Kategori", "Harga", "Stok");
        JTable t = createTable(m);
        JTextField cari = new JTextField(18);

        Runnable load = () -> {
            m.setRowCount(0);
            try {
                List<Barang> list = barangModel.getAllBarang(cari.getText());
                for (Barang b : list) {
                    m.addRow(new Object[]{b.getKode(), b.getNama(), b.getKategori(), rupiah.format(b.getHarga()), b.getStok()});
                }
            } catch (Exception ex) {
                view.showError(ex.getMessage());
            }
        };

        JButton add = new JButton("Tambah"), edit = new JButton("Edit"), del = new JButton("Hapus"), refresh = new JButton("Refresh");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Cari:")); top.add(cari); top.add(refresh);
        JPanel bottom = new JPanel();
        bottom.add(add); bottom.add(edit); bottom.add(del);
        bottom.add(new JButton("Tutup") {{ addActionListener(e -> d.dispose()); }});

        cari.addActionListener(e -> load.run());
        refresh.addActionListener(e -> load.run());

        add.addActionListener(e -> barangForm(d, null, load));
        edit.addActionListener(e -> {
            int r = t.getSelectedRow();
            if (r < 0) { view.showMsg("Pilih barang yang ingin diedit."); return; }
            String kode = m.getValueAt(t.convertRowIndexToModel(r), 0).toString();
            barangForm(d, kode, load);
        });

        del.addActionListener(e -> {
            int r = t.getSelectedRow();
            if (r < 0) { view.showMsg("Pilih barang yang ingin dihapus."); return; }
            String kode = m.getValueAt(t.convertRowIndexToModel(r), 0).toString();
            if (JOptionPane.showConfirmDialog(d, "Hapus barang " + kode + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    barangModel.deleteBarang(kode);
                    load.run();
                } catch (Exception ex) {
                    view.showError("Barang tidak dapat dihapus jika sudah digunakan dalam transaksi.");
                }
            }
        });

        d.add(top, BorderLayout.NORTH);
        d.add(new JScrollPane(t), BorderLayout.CENTER);
        d.add(bottom, BorderLayout.SOUTH);
        load.run();
        d.setVisible(true);
    }

    private void barangForm(JDialog parent, String kode, Runnable reload) {
        JTextField fKode = new JTextField(15), fNama = new JTextField(18), fKat = new JTextField(18), fHarga = new JTextField(12), fStok = new JTextField(8);
        if (kode != null) {
            try {
                Barang b = barangModel.getBarangByKode(kode);
                if (b != null) {
                    fKode.setText(b.getKode());
                    fNama.setText(b.getNama());
                    fKat.setText(b.getKategori());
                    fHarga.setText(String.valueOf(b.getHarga()));
                    fStok.setText(String.valueOf(b.getStok()));
                }
            } catch (Exception ex) {
                view.showError(ex.getMessage());
            }
        }
        fKode.setEnabled(kode == null);
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("Kode barang")); form.add(fKode);
        form.add(new JLabel("Nama barang")); form.add(fNama);
        form.add(new JLabel("Kategori")); form.add(fKat);
        form.add(new JLabel("Harga (angka)")); form.add(fHarga);
        form.add(new JLabel("Stok awal")); form.add(fStok);

        int ok = JOptionPane.showConfirmDialog(parent, form, kode == null ? "Tambah Barang" : "Edit Barang", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            if (fNama.getText().isBlank() || fKat.getText().isBlank()) {
                throw new IllegalArgumentException("Nama dan kategori wajib diisi.");
            }
            Barang b = new Barang(
                fKode.getText().trim(),
                fNama.getText().trim(),
                fKat.getText().trim(),
                Long.parseLong(fHarga.getText().trim()),
                Integer.parseInt(fStok.getText().trim())
            );
            if (kode == null) {
                barangModel.insertBarang(b);
            } else {
                barangModel.updateBarang(b);
            }
            reload.run();
        } catch (Exception ex) {
            view.showError("Data tidak valid atau kode barang sudah digunakan. Periksa harga dan stok harus berupa angka non-negatif.");
        }
    }

    // --- PERSEDIAAN ---
    private void openPersediaan() {
        JDialog d = new JDialog(view, "Pengelolaan Persediaan", true);
        d.setSize(760, 440);
        d.setLocationRelativeTo(view);
        DefaultTableModel m = createNonEditableModel("Tanggal", "Kode", "Nama Barang", "Jenis", "Jumlah", "Keterangan");
        JTable t = createTable(m);

        Runnable load = () -> {
            m.setRowCount(0);
            try {
                List<Object[]> list = mutasiModel.getAllMutasi();
                for (Object[] row : list) m.addRow(row);
            } catch (Exception ex) {
                view.showError(ex.getMessage());
            }
        };

        JButton masuk = new JButton("Stok Masuk"), keluar = new JButton("Stok Keluar"), close = new JButton("Tutup");
        JPanel b = new JPanel();
        b.add(masuk); b.add(keluar); b.add(close);
        masuk.addActionListener(e -> mutasiStok(d, "MASUK", load));
        keluar.addActionListener(e -> mutasiStok(d, "KELUAR", load));
        close.addActionListener(e -> d.dispose());

        d.add(new JScrollPane(t), BorderLayout.CENTER);
        d.add(b, BorderLayout.SOUTH);
        load.run();
        d.setVisible(true);
    }

    private void mutasiStok(JDialog parent, String jenis, Runnable reload) {
        JComboBox<String> cb = new JComboBox<>();
        try {
            List<Barang> list = barangModel.getAllBarang("");
            for (Barang b : list) cb.addItem(b.getKode() + " - " + b.getNama());
        } catch (Exception ex) {
            view.showError(ex.getMessage());
            return;
        }

        JTextField qty = new JTextField("1", 8), ket = new JTextField(20);
        JPanel p = new JPanel(new GridLayout(3, 2, 8, 8));
        p.add(new JLabel("Barang")); p.add(cb);
        p.add(new JLabel("Jumlah")); p.add(qty);
        p.add(new JLabel("Keterangan")); p.add(ket);

        if (JOptionPane.showConfirmDialog(parent, p, "Catat Stok " + jenis, JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (cb.getSelectedItem() == null) return;
        String kode = cb.getSelectedItem().toString().split(" - ")[0];

        try {
            int n = Integer.parseInt(qty.getText().trim());
            if (n <= 0) throw new Exception("Jumlah harus lebih dari 0.");
            mutasiModel.simpanMutasi(kode, jenis, n, ket.getText());
            reload.run();
            view.showMsg("Mutasi stok berhasil disimpan.");
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
    }

    // --- TRANSAKSI ---
    private void openTransaksi() {
        JDialog d = new JDialog(view, "Transaksi Penjualan", true);
        d.setSize(780, 500);
        d.setLocationRelativeTo(view);
        DefaultTableModel m = createNonEditableModel("Kode", "Nama", "Harga", "Jumlah", "Subtotal");
        JTable t = createTable(m);
        List<TransaksiModel.CartItem> cart = new ArrayList<>();
        JLabel total = new JLabel("Total: Rp0");

        Runnable refresh = () -> {
            m.setRowCount(0);
            long sum = 0;
            for (TransaksiModel.CartItem x : cart) {
                m.addRow(new Object[]{x.kode, x.nama, rupiah.format(x.harga), x.qty, rupiah.format(x.subtotal())});
                sum += x.subtotal();
            }
            total.setText("Total: " + rupiah.format(sum));
        };

        JButton add = new JButton("Tambah Barang"), remove = new JButton("Hapus dari Keranjang"), save = new JButton("Simpan Transaksi"), close = new JButton("Tutup");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(add); bottom.add(remove); bottom.add(total); bottom.add(save); bottom.add(close);

        add.addActionListener(e -> {
            JComboBox<String> cb = new JComboBox<>();
            try {
                List<Barang> list = barangModel.getBarangTersedia();
                for (Barang b : list) cb.addItem(b.getKode() + " - " + b.getNama() + " (stok: " + b.getStok() + ")");
            } catch (Exception ex) {
                view.showError(ex.getMessage());
                return;
            }
            if (cb.getItemCount() == 0) {
                view.showMsg("Tidak ada barang dengan stok tersedia.");
                return;
            }
            JPanel pilihPanel = new JPanel(new BorderLayout(6, 6));
            pilihPanel.add(new JLabel("Pilih barang"), BorderLayout.NORTH);
            pilihPanel.add(cb, BorderLayout.CENTER);

            if (JOptionPane.showConfirmDialog(d, pilihPanel, "Tambah Barang ke Keranjang", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
            String q = String.valueOf(cb.getSelectedItem());
            String kode = q.split(" - ")[0];
            String jumlah = JOptionPane.showInputDialog(d, "Jumlah barang:", "1");
            if (jumlah == null) return;

            try {
                int n = Integer.parseInt(jumlah);
                if (n <= 0) throw new Exception();
                Barang b = barangModel.getBarangByKode(kode);
                if (b == null) throw new Exception();

                int currentInCart = 0;
                for (TransaksiModel.CartItem it : cart) if (it.kode.equals(kode)) currentInCart = it.qty;

                if (n + currentInCart > b.getStok()) {
                    view.showMsg("Jumlah melebihi stok tersedia.");
                    return;
                }

                TransaksiModel.CartItem found = null;
                for (TransaksiModel.CartItem it : cart) if (it.kode.equals(kode)) found = it;

                if (found == null) cart.add(new TransaksiModel.CartItem(kode, b.getNama(), (int)b.getHarga(), n));
                else found.qty += n;

                refresh.run();
            } catch (Exception ex) {
                view.showMsg("Jumlah harus berupa angka bulat lebih dari 0.");
            }
        });

        remove.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) { view.showMsg("Pilih item pada keranjang."); return; }
            cart.remove(t.convertRowIndexToModel(row));
            refresh.run();
        });

        save.addActionListener(e -> {
            if (cart.isEmpty()) { view.showMsg("Keranjang masih kosong."); return; }
            long sum = cart.stream().mapToLong(TransaksiModel.CartItem::subtotal).sum();
            if (JOptionPane.showConfirmDialog(d, "Simpan transaksi sebesar " + rupiah.format(sum) + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

            try {
                int uId = userModel.getUserId(view.getUsername());
                long id = transaksiModel.simpanTransaksi(cart, uId);
                view.showMsg("Transaksi #" + id + " berhasil disimpan. Total: " + rupiah.format(sum));
                cart.clear();
                refresh.run();
            } catch (Exception ex) {
                view.showError(ex.getMessage());
            }
        });

        close.addActionListener(e -> d.dispose());
        d.add(new JScrollPane(t), BorderLayout.CENTER);
        d.add(bottom, BorderLayout.SOUTH);
        refresh.run();
        d.setVisible(true);
    }

    // --- RIWAYAT TRANSAKSI ---
    private void openRiwayat() {
        JDialog d = new JDialog(view, "Riwayat Transaksi Penjualan", true);
        d.setSize(760, 440);
        d.setLocationRelativeTo(view);
        DefaultTableModel m = createNonEditableModel("ID", "Tanggal", "Total", "Kasir");
        JTable t = createTable(m);

        try {
            List<Object[]> list = transaksiModel.getRiwayatTransaksi();
            for (Object[] row : list) {
                m.addRow(new Object[]{row[0], row[1], rupiah.format((long)row[2]), row[3]});
            }
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }

        JButton detail = new JButton("Lihat Detail"), close = new JButton("Tutup");
        detail.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) { view.showMsg("Pilih transaksi."); return; }
            int id = (int) m.getValueAt(t.convertRowIndexToModel(row), 0);
            DefaultTableModel dm = createNonEditableModel("Kode", "Nama Barang", "Harga Satuan", "Jumlah", "Subtotal");

            try {
                List<Object[]> details = transaksiModel.getDetailTransaksi(id);
                for (Object[] r : details) {
                    dm.addRow(new Object[]{r[0], r[1], rupiah.format((long)r[2]), r[3], rupiah.format((long)r[4])});
                }
            } catch (Exception ex) {
                view.showError(ex.getMessage());
            }

            JOptionPane.showMessageDialog(d, new JScrollPane(createTable(dm)), "Detail Transaksi #" + id, JOptionPane.INFORMATION_MESSAGE);
        });

        close.addActionListener(e -> d.dispose());
        JPanel b = new JPanel();
        b.add(detail); b.add(close);
        d.add(new JScrollPane(t), BorderLayout.CENTER);
        d.add(b, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // --- LAPORAN ---
    private void openLaporan() {
        JDialog d = new JDialog(view, "Laporan Persediaan dan Penjualan", true);
        d.setSize(820, 500);
        d.setLocationRelativeTo(view);
        JTabbedPane tabs = new JTabbedPane();

        JPanel ringkas = new JPanel(new GridLayout(3, 2, 10, 10));
        ringkas.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        try {
            ringkas.add(new JLabel("Jumlah jenis barang"));
            ringkas.add(new JLabel(String.valueOf(transaksiModel.getScalarCount("SELECT COUNT(*) FROM barang"))));
            ringkas.add(new JLabel("Total unit persediaan"));
            ringkas.add(new JLabel(String.valueOf(transaksiModel.getScalarCount("SELECT COALESCE(SUM(stok),0) FROM barang"))));
            ringkas.add(new JLabel("Total omzet penjualan"));
            ringkas.add(new JLabel(rupiah.format(transaksiModel.getScalarCount("SELECT COALESCE(SUM(total),0) FROM transaksi"))));
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
        tabs.addTab("Ringkasan", ringkas);

        DefaultTableModel sm = createNonEditableModel("Kode", "Nama Barang", "Kategori", "Harga", "Stok", "Nilai Persediaan");
        try {
            List<Barang> list = barangModel.getAllBarang("");
            for (Barang b : list) {
                sm.addRow(new Object[]{b.getKode(), b.getNama(), b.getKategori(), rupiah.format(b.getHarga()), b.getStok(), rupiah.format(b.getHarga() * b.getStok())});
            }
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
        tabs.addTab("Laporan Stok", new JScrollPane(createTable(sm)));

        DefaultTableModel tm = createNonEditableModel("ID Transaksi", "Tanggal", "Total");
        try {
            List<Object[]> list = transaksiModel.getRiwayatTransaksi();
            for (Object[] r : list) {
                tm.addRow(new Object[]{r[0], r[1], rupiah.format((long)r[2])});
            }
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
        tabs.addTab("Laporan Penjualan", new JScrollPane(createTable(tm)));

        DefaultTableModel low = createNonEditableModel("Kode", "Nama Barang", "Stok");
        try {
            List<Barang> list = barangModel.getStokMenipis(10);
            for (Barang b : list) {
                low.addRow(new Object[]{b.getKode(), b.getNama(), b.getStok()});
            }
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
        tabs.addTab("Stok Menipis (<=10)", new JScrollPane(createTable(low)));

        JButton close = new JButton("Tutup");
        close.addActionListener(e -> d.dispose());
        d.add(tabs, BorderLayout.CENTER);
        d.add(close, BorderLayout.SOUTH);
        d.setVisible(true);
    }
}
