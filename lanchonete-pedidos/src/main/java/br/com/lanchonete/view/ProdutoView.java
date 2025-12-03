package br.com.lanchonete.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import br.com.lanchonete.model.entity.Produto;
import br.com.lanchonete.model.enums.CategoriaProduto;
import br.com.lanchonete.model.repository.impl.ProdutoRepositoryImpl;

public class ProdutoView extends JFrame {

    private JTextField txtNome;
    private JTextField txtDescricao;
    private JTextField txtPreco;
    private JTextField txtEstoque;
    private JComboBox<CategoriaProduto> cbCategoria;
    private JCheckBox chkAtivo;

    private JTable tabela;
    private DefaultTableModel model;

    private final ProdutoRepositoryImpl produtoRepo = new ProdutoRepositoryImpl();

    // id do produto em edição (null = novo)
    private Long idEmEdicao = null;

    // label para indicar modo atual
    private JLabel lblModo;

    public ProdutoView() {
        initComponents();
        carregarProdutos();
    }

    private void initComponents() {
    setTitle("Cadastro de Produtos");
    setSize(900, 500);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(new BorderLayout());

    // ---------- FORMULÁRIO ----------
    JPanel painelForm = new JPanel();
    painelForm.setLayout(null);
    painelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // >>> ADICIONE ESTA LINHA <<<
    painelForm.setPreferredSize(new Dimension(900, 130));
    // ------------------------------

    lblModo = new JLabel("Modo: Novo produto");
    lblModo.setBounds(10, 0, 300, 20);
    painelForm.add(lblModo);

        JLabel lblNome = new JLabel("Nome:");
        lblNome.setBounds(10, 20, 80, 25);
        painelForm.add(lblNome);

        txtNome = new JTextField();
        txtNome.setBounds(100, 20, 760, 25);
        painelForm.add(txtNome);

        JLabel lblDescricao = new JLabel("Descrição:");
        lblDescricao.setBounds(10, 55, 80, 25);
        painelForm.add(lblDescricao);

        txtDescricao = new JTextField();
        txtDescricao.setBounds(100, 55, 760, 25);
        painelForm.add(txtDescricao);

        JLabel lblCategoria = new JLabel("Categoria:");
        lblCategoria.setBounds(10, 90, 80, 25);
        painelForm.add(lblCategoria);

        cbCategoria = new JComboBox<>(CategoriaProduto.values());
        cbCategoria.setBounds(100, 90, 200, 25);
        painelForm.add(cbCategoria);

        JLabel lblPreco = new JLabel("Preço:");
        lblPreco.setBounds(320, 90, 50, 25);
        painelForm.add(lblPreco);

        txtPreco = new JTextField();
        txtPreco.setBounds(370, 90, 100, 25);
        painelForm.add(txtPreco);

        JLabel lblEstoque = new JLabel("Estoque:");
        lblEstoque.setBounds(490, 90, 60, 25);
        painelForm.add(lblEstoque);

        txtEstoque = new JTextField();
        txtEstoque.setBounds(550, 90, 80, 25);
        painelForm.add(txtEstoque);

        chkAtivo = new JCheckBox("Ativo");
        chkAtivo.setBounds(650, 90, 70, 25);
        chkAtivo.setSelected(true);
        painelForm.add(chkAtivo);

        add(painelForm, BorderLayout.NORTH);

        // ---------- TABELA ----------
        model = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Categoria", "Preço", "Estoque", "Ativo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabela só leitura
            }
        };

        tabela = new JTable(model);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ao selecionar linha, já tenta carregar para edição
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarProdutoSelecionadoNoFormulario();
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        add(scroll, BorderLayout.CENTER);

        // ---------- BOTÕES ----------
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        //JButton btnNovo = new JButton("Novo");
        //btnNovo.addActionListener(e -> limparFormulario());

       // JButton btnEditar = new JButton("Editar selecionado");
        //btnEditar.addActionListener(e -> carregarProdutoSelecionadoNoFormulario());

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvarProduto());

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirProduto());

        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.addActionListener(e -> carregarProdutos());

        JButton btnRelatorios = new JButton("Relatórios");
        btnRelatorios.addActionListener(e -> {
            RelatorioGerenteView rel = new RelatorioGerenteView();
            rel.setVisible(true);
        });




        //painelBotoes.add(btnNovo);
        //painelBotoes.add(btnEditar);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnRelatorios);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    // --------- CARREGAR / LIMPAR ---------

    private void carregarProdutos() {
        model.setRowCount(0);
        List<Produto> produtos = produtoRepo.findAll();
        for (Produto p : produtos) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getNome(),
                    p.getCategoria(),
                    p.getPreco(),
                    p.getEstoque(),
                    p.isAtivo()
            });
        }
        idEmEdicao = null;
        lblModo.setText("Modo: Novo produto");
    }

    private void limparFormulario() {
        idEmEdicao = null;
        txtNome.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        txtEstoque.setText("");
        cbCategoria.setSelectedIndex(0);
        chkAtivo.setSelected(true);
        tabela.clearSelection();
        lblModo.setText("Modo: Novo produto");
        setTitle("Cadastro de Produtos");
    }

    private void carregarProdutoSelecionadoNoFormulario() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            return;
        }

        Object valorId = tabela.getValueAt(row, 0);
        if (valorId == null) return;

        Long id;
        if (valorId instanceof Long) {
            id = (Long) valorId;
        } else if (valorId instanceof Integer) {
            id = ((Integer) valorId).longValue();
        } else {
            id = Long.valueOf(valorId.toString());
        }

        Produto p = produtoRepo.findById(id).orElse(null);
        if (p == null) {
            JOptionPane.showMessageDialog(this,
                    "Produto não encontrado para edição (ID " + id + ").",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        idEmEdicao = p.getId();
        txtNome.setText(p.getNome());
        txtDescricao.setText(p.getDescricao());
        cbCategoria.setSelectedItem(p.getCategoria());
        txtPreco.setText(p.getPreco().toString());
        txtEstoque.setText(String.valueOf(p.getEstoque()));
        chkAtivo.setSelected(p.isAtivo());

        lblModo.setText("Modo: Editando produto ID " + idEmEdicao);
        setTitle("Cadastro de Produtos - Editando ID " + idEmEdicao);
    }

    // --------- PARSE DE PREÇO ---------

    private BigDecimal parsePreco(String texto) {
        if (texto == null) return BigDecimal.ZERO;

        texto = texto.trim();
        if (texto.isEmpty()) return BigDecimal.ZERO;

        // aceita vírgula ou ponto
        char decimalSep = DecimalFormatSymbols.getInstance(new Locale("pt", "BR")).getDecimalSeparator();
        if (decimalSep == ',') {
            texto = texto.replace(",", ".");
        }
        try {
            return new BigDecimal(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Preço inválido: " + texto);
        }
    }

    // --------- SALVAR (INSERT / UPDATE) ---------

    private void salvarProduto() {
        try {
            String nome = txtNome.getText().trim();
            String descricao = txtDescricao.getText().trim();
            String precoStr = txtPreco.getText().trim();
            String estoqueStr = txtEstoque.getText().trim();
            CategoriaProduto categoria = (CategoriaProduto) cbCategoria.getSelectedItem();
            boolean ativo = chkAtivo.isSelected();

            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o nome do produto.",
                        "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BigDecimal preco = parsePreco(precoStr);
            int estoque = 0;
            if (!estoqueStr.isEmpty()) {
                try {
                    estoque = Integer.parseInt(estoqueStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Estoque inválido.",
                            "Validação", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            Produto produto = new Produto();
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setCategoria(categoria);
            produto.setPreco(preco);
            produto.setEstoque(estoque);
            produto.setAtivo(ativo);

            if (idEmEdicao == null) {
                // INSERT
                produtoRepo.save(produto);
                JOptionPane.showMessageDialog(this,
                        "Produto cadastrado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // UPDATE
                produto.setId(idEmEdicao);
                produtoRepo.update(produto);
                JOptionPane.showMessageDialog(this,
                        "Produto atualizado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }

            carregarProdutos();
            limparFormulario();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar produto: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // --------- EXCLUIR ---------

    private void excluirProduto() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um produto na tabela para excluir.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object valorId = tabela.getValueAt(row, 0);
        if (valorId == null) return;

        Long id;
        if (valorId instanceof Long) {
            id = (Long) valorId;
        } else if (valorId instanceof Integer) {
            id = ((Integer) valorId).longValue();
        } else {
            id = Long.valueOf(valorId.toString());
        }

        int opc = JOptionPane.showConfirmDialog(this,
                "Confirma exclusão do produto ID " + id + "?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION);

        if (opc != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            produtoRepo.delete(id);
            JOptionPane.showMessageDialog(this,
                    "Produto excluído com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarProdutos();
            limparFormulario();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao excluir produto: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
