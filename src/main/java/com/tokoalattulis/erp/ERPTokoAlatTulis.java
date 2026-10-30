package com.tokoalattulis.erp;

import com.tokoalattulis.erp.controller.LoginController;
import com.tokoalattulis.erp.model.UserModel;
import com.tokoalattulis.erp.util.DatabaseConnection;
import com.tokoalattulis.erp.view.LoginForm;
import javax.swing.SwingUtilities;

public class ERPTokoAlatTulis {
    public static void main(String[] args) {
        DatabaseConnection.initDatabase();
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            UserModel userModel = new UserModel();
            new LoginController(loginForm, userModel);
            loginForm.setVisible(true);
        });
    }
}
