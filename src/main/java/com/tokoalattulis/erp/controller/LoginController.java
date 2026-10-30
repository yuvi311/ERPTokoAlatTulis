package com.tokoalattulis.erp.controller;

import com.tokoalattulis.erp.model.UserModel;
import com.tokoalattulis.erp.view.LoginForm;
import com.tokoalattulis.erp.view.MenuUtamaView;

public class LoginController {
    private final LoginForm view;
    private final UserModel userModel;

    public LoginController(LoginForm view, UserModel userModel) {
        this.view = view;
        this.userModel = userModel;

        this.view.addLoginListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (userModel.validasiLogin(username, password)) {
            view.showMessage("Login berhasil!");
            view.dispose();

            MenuUtamaView menuView = new MenuUtamaView(username);
            new MenuUtamaController(menuView, userModel);
            menuView.setVisible(true);
        } else {
            view.showError("Username atau password salah!");
        }
    }
}
