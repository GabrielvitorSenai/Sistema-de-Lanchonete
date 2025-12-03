package br.com.lanchonete.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import br.com.lanchonete.controller.PedidoController;
import br.com.lanchonete.model.entity.Cliente;
import br.com.lanchonete.model.entity.ItemPedido;
import br.com.lanchonete.model.entity.Pedido;
import br.com.lanchonete.model.entity.Produto;
import br.com.lanchonete.model.enums.CategoriaProduto;
import br.com.lanchonete.model.repository.impl.ClienteRepositoryImpl;
import br.com.lanchonete.model.repository.impl.FidelidadeBeneficioRepositoryImpl;
import br.com.lanchonete.model.repository.impl.PedidoRepositoryImpl;
import br.com.lanchonete.model.repository.impl.ProdutoRepositoryImpl;
import br.com.lanchonete.service.FidelidadeService;
import br.com.lanchonete.service.PedidoService;

public class PedidoClienteView extends JFrame {

    private final Cliente cliente;

    private JTable tabelaProdutos;
    private JTable tabelaPedidos;
    private JTable tabelaCarrinho;

    private JComboBox<CategoriaProduto> cbCategoria;
    private JLabel lblPontos;
    private JTextField txtMesa; // campo da mesa

    private DefaultTableModel modelProdutos;
    private DefaultTableModel modelPedidos;
    private DefaultTableModel modelCarrinho;

    private final ProdutoRepositoryImpl produtoRepo = new ProdutoRepositoryImpl();
    private final PedidoRepositoryImpl pedidoRepo = new PedidoRepositoryImpl();
    private final FidelidadeBeneficioRepositoryImpl beneficioRepo = new FidelidadeBeneficioRepositoryImpl();
    private final ClienteRepositoryImpl clienteRepo = new ClienteRepositoryImpl();
    private final PedidoController pedidoController;

    // Carrinho de itens do pedido atual
    private final List<ItemPedido> carrinho = new ArrayList<>();

    // Timer para atualizar status dos pedidos periodicamente
    private javax.swing.Timer timerAtualizacao;

    public PedidoClienteView(Cliente cliente) {
        this.cliente = cliente;
        FidelidadeService fidelidadeService = new FidelidadeService(clienteRepo, beneficioRepo);
        PedidoService pedidoService = new PedidoService(pedidoRepo, produtoRepo);
        this.pedidoController = new PedidoController(pedidoService, pedidoRepo, fidelidadeService);

        initComponents();
        carregarProdutos();
        carregarPedidos();
        atualizarPontos();
        iniciarTimerAtualizacao();
    }

    private void initComponents() {
        setTitle("Área do Cliente - Pedidos");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        lblPontos = new JLabel("Pontos: 0");
        txtMesa = new JTextField(5);   // número da mesa

        // TOPO
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Cliente: " + cliente.getNome()));
        topo.add(lblPontos);
        topo.add(new JLabel("Mesa:"));
        topo.add(txtMesa);

        // ----------- PAINEL DE PRODUTOS + CARRINHO -------------
        JPanel painelProdutos = new JPanel(new BorderLayout());
        painelProdutos.setBorder(BorderFactory.createTitledBorder("Cardápio e Carrinho"));

        // Combobox de categoria
        cbCategoria = new JComboBox<>(CategoriaProduto.values());
        cbCategoria.addActionListener(e -> carregarProdutos());
        painelProdutos.add(cbCategoria, BorderLayout.NORTH);

        // Tabela de produtos (mantém ID, pois usamos para buscar no banco)
        modelProdutos = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Categoria", "Preço"}, 0);
        tabelaProdutos = new JTable(modelProdutos);
        JScrollPane scrollProdutos = new JScrollPane(tabelaProdutos);

        // Tabela de carrinho (AGORA SEM ID, mostra descrição amigável)
        modelCarrinho = new DefaultTableModel(
                new Object[]{"Produto", "Qtd", "Preço Unit.", "Subtotal"}, 0);
        tabelaCarrinho = new JTable(modelCarrinho);
        JScrollPane scrollCarrinho = new JScrollPane(tabelaCarrinho);

        JSplitPane splitProdutosCarrinho = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, scrollProdutos, scrollCarrinho);
        splitProdutosCarrinho.setResizeWeight(0.5);

        painelProdutos.add(splitProdutosCarrinho, BorderLayout.CENTER);

        // Botões do cardápio/carrinho
        JPanel painelBotoesCarrinho = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdicionar = new JButton("Adicionar item ao carrinho");
        JButton btnRemover = new JButton("Remover item do carrinho");
        JButton btnFinalizar = new JButton("Finalizar Pedido");

        btnAdicionar.addActionListener(e -> adicionarItemAoCarrinho());
        btnRemover.addActionListener(e -> removerItemDoCarrinho());
        btnFinalizar.addActionListener(e -> finalizarPedido());

        painelBotoesCarrinho.add(btnAdicionar);
        painelBotoesCarrinho.add(btnRemover);
        painelBotoesCarrinho.add(btnFinalizar);

        painelProdutos.add(painelBotoesCarrinho, BorderLayout.SOUTH);

        // ----------- PAINEL DE PEDIDOS DO CLIENTE -------------
        JPanel painelPedidos = new JPanel(new BorderLayout());
        painelPedidos.setBorder(BorderFactory.createTitledBorder("Meus Pedidos"));

        modelPedidos = new DefaultTableModel(
                new Object[]{"ID", "Data", "Status", "Total"}, 0);
        tabelaPedidos = new JTable(modelPedidos);
        JScrollPane scrollPedidos = new JScrollPane(tabelaPedidos);

        painelPedidos.add(scrollPedidos, BorderLayout.CENTER);

        JPanel painelBotoesPedidos = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnVerItens = new JButton("Ver itens do pedido");
        JButton btnAtualizarPedidos = new JButton("Atualizar pedidos");

        btnVerItens.addActionListener(e -> abrirDetalhesPedidoSelecionado());
        btnAtualizarPedidos.addActionListener(e -> carregarPedidos());

        painelBotoesPedidos.add(btnVerItens);
        painelBotoesPedidos.add(btnAtualizarPedidos);

        painelPedidos.add(painelBotoesPedidos, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, painelProdutos, painelPedidos);
        split.setResizeWeight(0.55);

        add(topo, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    private void iniciarTimerAtualizacao() {
        timerAtualizacao = new javax.swing.Timer(5000, e -> carregarPedidos());
        timerAtualizacao.start();
    }

    // ---------- PRODUTOS ----------
    private void carregarProdutos() {
        modelProdutos.setRowCount(0);
        CategoriaProduto cat = (CategoriaProduto) cbCategoria.getSelectedItem();
        if (cat == null) {
            return;
        }

        produtoRepo.findByCategoriaAtivos(cat).forEach(p -> {
            modelProdutos.addRow(new Object[]{
                    p.getId(),
                    p.getNome(),
                    p.getCategoria(),
                    p.getPreco()
            });
        });
    }

    // ---------- PEDIDOS DO CLIENTE ----------
    private void carregarPedidos() {
        modelPedidos.setRowCount(0);
        List<Pedido> pedidos = pedidoController.listarPedidosDoCliente(cliente);
        for (Pedido p : pedidos) {
            modelPedidos.addRow(new Object[]{
                    p.getId(), p.getDataCriacao(), p.getStatus(), p.getValorTotal()
            });
        }
    }

    private void atualizarPontos() {
        clienteRepo.findById(cliente.getId()).ifPresent(c -> {
            lblPontos.setText("Pontos: " + c.getPontosFidelidade());
        });
    }

    // ---------- CARRINHO ----------
    private void adicionarItemAoCarrinho() {
        int row = tabelaProdutos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Valor da primeira coluna pode vir como Long, Integer ou String -> convertemos com segurança
        Object valorId = tabelaProdutos.getValueAt(row, 0);
        Long idProduto;
        if (valorId instanceof Long) {
            idProduto = (Long) valorId;
        } else if (valorId instanceof Integer) {
            idProduto = ((Integer) valorId).longValue();
        } else {
            idProduto = Long.valueOf(valorId.toString());
        }

        Produto produto = produtoRepo.findById(idProduto)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        String quantidadeStr = JOptionPane.showInputDialog(this, "Quantidade:", "1");
        if (quantidadeStr == null) return;

        int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (quantidade <= 0) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(produto.getPreco());
        item.setSubtotal(produto.getPreco().multiply(BigDecimal.valueOf(quantidade)));

        carrinho.add(item);
        atualizarTabelaCarrinho();
    }

    private void removerItemDoCarrinho() {
        int row = tabelaCarrinho.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item do carrinho.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        carrinho.remove(row);
        atualizarTabelaCarrinho();
    }

    private void atualizarTabelaCarrinho() {
        modelCarrinho.setRowCount(0);
        for (ItemPedido item : carrinho) {
            Produto p = item.getProduto();
            String textoProduto = p.getNome();
            if (p.getDescricao() != null && !p.getDescricao().trim().isEmpty()) {
                textoProduto += " - " + p.getDescricao();
            }

            modelCarrinho.addRow(new Object[]{
                    textoProduto,                 // <--- em vez de ID, mostra nome + descrição
                    item.getQuantidade(),
                    item.getPrecoUnitario(),
                    item.getSubtotal()
            });
        }
    }

    // ---------- FINALIZAR PEDIDO ----------
    private void finalizarPedido() {
        if (carrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Adicione ao menos um item ao carrinho.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String numeroMesa = txtMesa.getText();
        if (numeroMesa == null || numeroMesa.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Informe o número da mesa antes de finalizar o pedido.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Pedido pedido = pedidoController.criarPedido(
                    cliente,
                    new ArrayList<>(carrinho)
            );

            JOptionPane.showMessageDialog(this,
                    "Pedido criado! ID: " + pedido.getId() +
                            " | Mesa: " + numeroMesa.trim() +
                            " | Status: " + pedido.getStatus());

            carrinho.clear();
            atualizarTabelaCarrinho();
            carregarPedidos();
            atualizarPontos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao criar pedido: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        if (timerAtualizacao != null) {
            timerAtualizacao.stop();
        }
        super.dispose();
    }

    // ---------- DETALHES DO PEDIDO ----------
    private void abrirDetalhesPedidoSelecionado() {
        int row = tabelaPedidos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um pedido na lista.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long pedidoId = (Long) tabelaPedidos.getValueAt(row, 0);

        try {
            Pedido pedido = pedidoController.buscarPorId(pedidoId);
            if (pedido == null) {
                JOptionPane.showMessageDialog(this,
                        "Pedido não encontrado.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<ItemPedido> itens = pedidoController.listarItensDoPedido(pedidoId);
            if (itens.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Esse pedido não possui itens cadastrados.",
                        "Aviso",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            PedidoDetalheDialog dialog = new PedidoDetalheDialog(this, pedido, itens);
            dialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar itens do pedido: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
