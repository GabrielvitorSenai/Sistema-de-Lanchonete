package br.com.lanchonete.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import br.com.lanchonete.controller.PedidoController;
import br.com.lanchonete.model.entity.ItemPedido;
import br.com.lanchonete.model.entity.Pedido;
import br.com.lanchonete.model.enums.StatusPedido;
import br.com.lanchonete.model.repository.impl.ClienteRepositoryImpl;
import br.com.lanchonete.model.repository.impl.FidelidadeBeneficioRepositoryImpl;
import br.com.lanchonete.model.repository.impl.PedidoRepositoryImpl;
import br.com.lanchonete.model.repository.impl.ProdutoRepositoryImpl;
import br.com.lanchonete.service.FidelidadeService;
import br.com.lanchonete.service.PedidoService;

public class CozinhaView extends JFrame {

    private JTable tabela;
    private DefaultTableModel model;
    private final PedidoController controller;

    public CozinhaView() {
        PedidoRepositoryImpl pedidoRepo = new PedidoRepositoryImpl();
        ProdutoRepositoryImpl produtoRepo = new ProdutoRepositoryImpl();
        ClienteRepositoryImpl clienteRepo = new ClienteRepositoryImpl();
        FidelidadeBeneficioRepositoryImpl benefRepo = new FidelidadeBeneficioRepositoryImpl();

        PedidoService pedidoService = new PedidoService(pedidoRepo, produtoRepo);
        FidelidadeService fidelidadeService = new FidelidadeService(clienteRepo, benefRepo);

        this.controller = new PedidoController(pedidoService, pedidoRepo, fidelidadeService);

        initComponents();
        carregarPedidos();
    }

    private void initComponents() {
        setTitle("Cozinha - Pedidos");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 🔹 Agora com coluna "Mesa"
        model = new DefaultTableModel(
                new Object[]{"ID", "Mesa", "Cliente ID", "Data", "Status", "Total"}, 0);
        tabela = new JTable(model);

        JScrollPane scroll = new JScrollPane(tabela);

        JButton btnVerItens = new JButton("Ver itens do pedido");
        btnVerItens.addActionListener(e -> abrirDetalhesPedidoSelecionado());

        JButton btnEmProducao = new JButton("Marcar EM PRODUÇÃO");
        btnEmProducao.addActionListener(e -> atualizarStatus(StatusPedido.EM_PRODUCAO));

        JButton btnPronto = new JButton("Marcar PRONTO");
        btnPronto.addActionListener(e -> atualizarStatus(StatusPedido.PRONTO));

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> carregarPedidos());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnVerItens);
        botoes.add(btnEmProducao);
        botoes.add(btnPronto);
        botoes.add(btnAtualizar);

        add(scroll, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    private void carregarPedidos() {
        model.setRowCount(0);
        List<Pedido> pedidos = controller.listarPendentesCozinha();
        for (Pedido p : pedidos) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getNumeroMesa(),          // 🔹 nova coluna Mesa
                    p.getCliente().getId(),
                    p.getDataCriacao(),
                    p.getStatus(),
                    p.getValorTotal()
            });
        }
    }

    private void atualizarStatus(StatusPedido novoStatus) {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tabela.getValueAt(row, 0);
        PedidoRepositoryImpl repo = new PedidoRepositoryImpl();
        Pedido p = repo.findById(id).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        controller.atualizarStatus(p, novoStatus);
        carregarPedidos();
    }

    // NOVO: ver itens do pedido selecionado
    private void abrirDetalhesPedidoSelecionado() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um pedido na lista.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long pedidoId = (Long) tabela.getValueAt(row, 0);

        try {
            Pedido pedido = controller.buscarPorId(pedidoId);
            if (pedido == null) {
                JOptionPane.showMessageDialog(this,
                        "Pedido não encontrado.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<ItemPedido> itens = controller.listarItensDoPedido(pedidoId);
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
