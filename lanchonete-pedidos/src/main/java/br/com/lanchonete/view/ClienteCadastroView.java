package br.com.lanchonete.view;

import javax.swing.*;
import java.awt.*;

import br.com.lanchonete.controller.ClienteController;
import br.com.lanchonete.model.repository.impl.ClienteRepositoryImpl;

public class ClienteCadastroView extends JFrame {

    private JTextField txtNome;
    private JTextField txtEmail;
    private JTextField txtDataNasc;
    private JTextField txtCpf;
    private JPasswordField txtSenha;
    private ClienteController controller;

    public ClienteCadastroView() {
        this.controller = new ClienteController(new ClienteRepositoryImpl());
        initComponents();
    }

    private void initComponents() {
        setTitle("Cadastro de Cliente");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        form.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        form.add(txtNome);

        form.add(new JLabel("E-mail:"));
        txtEmail = new JTextField();
        form.add(txtEmail);

        form.add(new JLabel("Data nasc. (dd/MM/yyyy):"));
        txtDataNasc = new JTextField();
        form.add(txtDataNasc);

        form.add(new JLabel("CPF:"));
        txtCpf = new JTextField();
        form.add(txtCpf);

        form.add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        form.add(txtSenha);

        JButton btnSalvar = new JButton("Cadastrar");
        btnSalvar.addActionListener(e -> cadastrar());
        form.add(btnSalvar);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        form.add(btnFechar);

        add(form, BorderLayout.CENTER);
    }

    private void cadastrar() {
        try {
            controller.cadastrarNovoCliente(
                    txtNome.getText(),
                    txtEmail.getText(),
                    txtDataNasc.getText(),
                    txtCpf.getText(),
                    new String(txtSenha.getPassword())
            );
            JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
