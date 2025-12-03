package br.com.lanchonete;

import javax.swing.SwingUtilities;

import br.com.lanchonete.view.LoginView;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView login = new LoginView();
            login.setVisible(true);
        });
    }
}
