ERP TOKO ALAT TULIS - PATTERN MVC (MODEL-VIEW-CONTROLLER)

Aplikasi ERP Toko Alat Tulis ini telah direfaktorisasi penuh menggunakan Arsitektur MVC (Model-View-Controller) murni tanpa mengubah fungsionalitas dan fitur aslinya.

STRUKTUR ARCHITECTURE (MVC):
1. com.tokoalattulis.erp.model:
   - User.java, Barang.java, Transaksi.java, DetailTransaksi.java, MutasiStok.java (Entity Models)
   - UserModel.java, BarangModel.java, TransaksiModel.java, MutasiStokModel.java (Data Access & SQL Logic)
2. com.tokoalattulis.erp.view:
   - LoginForm.java (Form Login)
   - MenuUtamaView.java (Tampilan Antarmuka Utama & Dialog GUI)
3. com.tokoalattulis.erp.controller:
   - LoginController.java (Pengendali Autentikasi)
   - MenuUtamaController.java (Pengendali Alur Bisnis antara View & Model)
4. com.tokoalattulis.erp.util:
   - DatabaseConnection.java (Koneksi SQLite & DDL Initialization)

CARA MEMBUKA DI APACHE NETBEANS:
1. Extract ZIP ini.
2. Buka NetBeans > File > Open Project.
3. Pilih folder ERPTokoAlatTulis_MVC (folder yang berisi pom.xml).
4. Klik kanan project > Clean and Build.
5. Klik kanan project > Run.

LOGIN DEFAULT:
Username: admin
Password: admin123
