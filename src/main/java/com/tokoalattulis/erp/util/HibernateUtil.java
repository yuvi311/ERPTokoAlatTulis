package com.tokoalattulis.erp.util;

import com.tokoalattulis.erp.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.function.Function;

/** Konfigurasi Hibernate (ORM) + helper session/transaksi. */
public class HibernateUtil {
    private static SessionFactory factory;

    public static synchronized SessionFactory getSessionFactory() {
        if (factory == null) {
            Configuration cfg = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC")
                .setProperty("hibernate.connection.url", "jdbc:sqlite:tokoalattulis.db?foreign_keys=true")
                .setProperty("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect")
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.connection.pool_size", "1") // SQLite = satu penulis
                .setProperty("hibernate.show_sql", "false");
            cfg.addAnnotatedClass(User.class);
            cfg.addAnnotatedClass(Barang.class);
            cfg.addAnnotatedClass(Transaksi.class);
            cfg.addAnnotatedClass(DetailTransaksi.class);
            cfg.addAnnotatedClass(MutasiStok.class);
            factory = cfg.buildSessionFactory();
        }
        return factory;
    }

    /** Menjalankan kerja baca-saja. */
    public static <T> T read(Function<Session, T> work) {
        try (Session s = getSessionFactory().openSession()) {
            return work.apply(s);
        }
    }

    /** Menjalankan kerja dalam satu transaksi (commit / rollback otomatis). */
    public static <T> T tx(Function<Session, T> work) {
        try (Session s = getSessionFactory().openSession()) {
            Transaction t = s.beginTransaction();
            try {
                T r = work.apply(s);
                t.commit();
                return r;
            } catch (RuntimeException e) {
                if (t.isActive()) t.rollback();
                throw e;
            }
        }
    }

    public static void initDatabase() {
        tx(s -> {
            Long n = s.createQuery("select count(u) from User u where u.username = :n", Long.class)
                      .setParameter("n", "admin").uniqueResult();
            if (n == 0) s.persist(new User("admin", "admin123", "Administrator", "admin"));

            Object[][] data = {
                {"ATK001", "Buku Tulis", "Buku", 5000L, 50},
                {"ATK002", "Pulpen Hitam", "Pulpen", 3000L, 100},
                {"ATK003", "Pensil 2B", "Pensil", 2500L, 75},
                {"ATK004", "Penghapus", "Aksesori", 2000L, 40},
                {"ATK005", "Penggaris 30 cm", "Alat Ukur", 4000L, 30}
            };
            for (Object[] d : data) {
                if (s.get(Barang.class, (String) d[0]) == null) {
                    s.persist(new Barang((String) d[0], (String) d[1], (String) d[2], (long) d[3], (int) d[4]));
                }
            }
            return null;
        });
        System.out.println("Database SQLite (Hibernate ORM) siap: tokoalattulis.db");
    }
}
