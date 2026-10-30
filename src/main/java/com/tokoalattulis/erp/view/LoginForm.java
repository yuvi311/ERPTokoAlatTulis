package com.tokoalattulis.erp.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private final JTextField txtUsername = new JTextField(15);
    private final JPasswordField txtPassword = new JPasswordField(15);
    private final JButton btnLogin = new JButton("Login");

    public LoginForm() {
        setTitle("Login - ERP Toko Alat Tulis (MVC)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 8, 8));
        add(new JLabel("LOGIN ERP TOKO ALAT TULIS"));
        add(new JLabel(""));
        add(new JLabel("Username"));
        add(txtUsername);
        add(new JLabel("Password"));
        add(txtPassword);
        add(new JLabel(""));
        add(btnLogin);
        getRootPane().setDefaultButton(btnLogin);
        setSize(390, 210);
        setLocationRelativeTo(null);
    }

    public String getUsername() { return txtUsername.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }

    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Gagal", JOptionPane.ERROR_MESSAGE);
    }
}
