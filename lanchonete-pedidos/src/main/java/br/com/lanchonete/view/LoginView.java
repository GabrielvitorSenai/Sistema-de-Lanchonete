package br.com.lanchonete.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import br.com.lanchonete.controller.ClienteController;
import br.com.lanchonete.controller.LoginController;
import br.com.lanchonete.model.entity.Cliente;
import br.com.lanchonete.model.entity.Usuario;
import br.com.lanchonete.model.enums.PerfilUsuario;
import br.com.lanchonete.model.repository.impl.ClienteRepositoryImpl;
import br.com.lanchonete.model.repository.impl.UsuarioRepositoryImpl;

public class LoginView extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private final LoginController loginController;
    private final ClienteController clienteController;

    public LoginView() {
        this.loginController = new LoginController(new UsuarioRepositoryImpl());
        this.clienteController = new ClienteController(new ClienteRepositoryImpl());
        initComponents();
    }

    private void initComponents() {
        setTitle("Login - Lanchonete");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 220);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));

        panel.add(new JLabel("E-mail:"));
        txtEmail = new JTextField();
        panel.add(txtEmail);

        panel.add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        panel.add(txtSenha);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.addActionListener(e -> autenticar());
        panel.add(btnEntrar);

        JButton btnCadastrarCliente = new JButton("Sou novo cliente");
        btnCadastrarCliente.addActionListener(e -> abrirCadastroCliente());
        panel.add(btnCadastrarCliente);

        JButton btnSair = new JButton("Sair");
        btnSair.addActionListener(e -> System.exit(0));
        panel.add(btnSair);

        add(panel, BorderLayout.CENTER);
    }

    private void autenticar() {
        String email = txtEmail.getText();
        String senha = new String(txtSenha.getPassword());

        System.out.println("DEBUG LoginView.autenticar()");
        System.out.println("  E-mail digitado: [" + email + "]");
        System.out.println("  Senha digitada: [" + senha + "]");

        try {
            // 1) Tenta logar como usuário (gerente, garçom, cozinha, caixa, cliente-usuário)
            Optional<Usuario> optUsuario = loginController.autenticar(email, senha);
            System.out.println("  Resultado autenticar Usuario: " +
                    (optUsuario.isPresent() ? "ENCONTRADO" : "NAO ENCONTRADO"));

            if (optUsuario.isPresent()) {
                Usuario usuario = optUsuario.get();
                System.out.println("  Usuario encontrado: id=" + usuario.getId()
                        + ", email=" + usuario.getEmail()
                        + ", perfil=" + usuario.getPerfil()
                        + ", ativo=" + usuario.isAtivo());

                abrirTelaPorPerfil(usuario);
                dispose();
                return;
            }

            // 2) Se não achou usuário, tenta logar como CLIENTE (tabela cliente)
            Optional<Cliente> optCliente = clienteController.buscarPorEmail(email);
            System.out.println("  Resultado buscar Cliente por email: " +
                    (optCliente.isPresent() ? "ENCONTRADO" : "NAO ENCONTRADO"));

            if (optCliente.isPresent()) {
                Cliente cliente = optCliente.get();
                System.out.println("  Cliente encontrado: id=" + cliente.getId()
                        + ", email=" + cliente.getEmail()
                        + ", ativo=" + cliente.isAtivo()
                        + ", senhaBD=" + cliente.getSenha());

                if (cliente.getSenha().equals(senha) && cliente.isAtivo()) {
                    System.out.println("  Senha do cliente confere. Abrindo tela do cliente...");
                    PedidoClienteView telaCliente = new PedidoClienteView(cliente);
                    telaCliente.setVisible(true);
                    dispose();
                } else {
                    System.out.println("  Senha do cliente NAO confere ou cliente inativo.");
                    JOptionPane.showMessageDialog(this,
                            "Senha inválida para cliente.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                System.out.println("  Nenhum Usuario ou Cliente encontrado com esse e-mail.");
                JOptionPane.showMessageDialog(this,
                        "Credenciais inválidas.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            System.out.println("ERRO em LoginView.autenticar(): " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao tentar autenticar:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirCadastroCliente() {
        ClienteCadastroView cad = new ClienteCadastroView();
        cad.setVisible(true);
    }

    private void abrirTelaPorPerfil(Usuario usuario) {
        PerfilUsuario perfil = usuario.getPerfil();
        System.out.println("DEBUG abrirTelaPorPerfil() - Perfil: " + perfil);

        switch (perfil) {
            case GERENTE:
                System.out.println("  Abrindo tela de Produtos (GERENTE)");
                new ProdutoView().setVisible(true);
                break;
            case COZINHA:
                System.out.println("  Abrindo tela de Cozinha");
                new CozinhaView().setVisible(true);
                break;
            case GARCOM:
                System.out.println("  Abrindo tela de Garçom");
                new GarcomView().setVisible(true);
                break;
            case CAIXA:
                System.out.println("  Abrindo tela de Caixa");
                new CaixaView().setVisible(true);
                break;
            case CLIENTE:
                System.out.println("  Perfil CLIENTE via tabela usuario, procurando na tabela cliente...");
                ClienteRepositoryImpl cliRepo = new ClienteRepositoryImpl();
                Optional<Cliente> optCli = cliRepo.findByEmail(usuario.getEmail());
                if (optCli.isPresent()) {
                    System.out.println("  Cliente encontrado para esse e-mail. Abrindo tela de pedidos do cliente.");
                    new PedidoClienteView(optCli.get()).setVisible(true);
                } else {
                    System.out.println("  Nenhum cliente encontrado para esse e-mail.");
                    JOptionPane.showMessageDialog(this,
                            "Usuário CLIENTE sem cadastro em tabela cliente.",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                }
                break;
            default:
                System.out.println("  Perfil não suportado: " + perfil);
                JOptionPane.showMessageDialog(this,
                        "Perfil não suportado.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
}
