package com.tokoalattulis.erp.model;

import com.tokoalattulis.erp.util.HibernateUtil;

public class UserModel {
    public boolean validasiLogin(String username, String password) {
        try {
            return HibernateUtil.read(s -> s.createQuery(
                    "select count(u) from User u where u.username = :u and u.password = :p", Long.class)
                .setParameter("u", username)
                .setParameter("p", password)
                .uniqueResult() > 0);
        } catch (RuntimeException e) {
            return false;
        }
    }

    public int getUserId(String username) {
        try {
            Integer id = HibernateUtil.read(s -> s.createQuery(
                    "select u.id from User u where u.username = :u", Integer.class)
                .setParameter("u", username)
                .uniqueResult());
            return id != null ? id : 1;
        } catch (RuntimeException e) {
            return 1;
        }
    }
}
