package br.com.lanchonete.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;

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
import javax.swing.table.DefaultTableModel;

import br.com.lanchonete.controller.CaixaController;
import br.com.lanchonete.model.entity.Cliente;
import br.com.lanchonete.model.entity.ItemPedido;
import br.com.lanchonete.model.entity.Pedido;
import br.com.lanchonete.model.enums.StatusPedido;
import br.com.lanchonete.model.repository.impl.ClienteRepositoryImpl;
import br.com.lanchonete.model.repository.impl.FidelidadeBeneficioRepositoryImpl;
import br.com.lanchonete.model.repository.impl.PedidoRepositoryImpl;
import br.com.lanchonete.service.CaixaService;
import br.com.lanchonete.service.CaixaService.FormaPagamento;
import br.com.lanchonete.service.CaixaService.ResultadoPagamento;
import br.com.lanchonete.service.FidelidadeService;

public class CaixaView extends JFrame {

    private JTable tabelaPedidos;
    private JTextField txtValorRecebido;
    private JComboBox<FormaPagamento> cbForma;
    private JLabel lblTotal;
    private JLabel lblTroco;
    private JCheckBox chkDesconto;
    private JCheckBox chkCombo;

    private final PedidoRepositoryImpl pedidoRepo = new PedidoRepositoryImpl();
    private final ClienteRepositoryImpl clienteRepo = new ClienteRepositoryImpl();
    private final CaixaController caixaController;
    private Pedido pedidoSelecionado;
    private Cliente clienteDoPedido;

    // NOVO: pontos do cliente antes do pagamento
    private int pontosAntes = 0;

    public CaixaView() {
        CaixaService caixaService = new CaixaService();
        FidelidadeBeneficioRepositoryImpl benefRepo = new FidelidadeBeneficioRepositoryImpl();
        FidelidadeService fidelidadeService = new FidelidadeService(clienteRepo, benefRepo);

        this.caixaController = new CaixaController(caixaService, pedidoRepo, benefRepo, fidelidadeService);
        initComponents();
        carregarPedidos();
    }

    private void initComponents() {
        setTitle("Caixa - Pagamento");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tabelaPedidos = new JTable();
        JScrollPane scroll = new JScrollPane(tabelaPedidos);

        JPanel painelPagamento = new JPanel(new GridLayout(9, 2, 5, 5)); // 9 linhas
        painelPagamento.setBorder(BorderFactory.createTitledBorder("Pagamento"));

        lblTotal = new JLabel("Total: R$ 0,00");
        lblTroco = new JLabel("Troco: R$ 0,00");
        cbForma = new JComboBox<>(FormaPagamento.values());
        txtValorRecebido = new JTextField();
        chkDesconto = new JCheckBox("Usar desconto 10%");
        chkCombo = new JCheckBox("Usar combo grátis");

        painelPagamento.add(new JLabel("Forma de pagamento:"));
        painelPagamento.add(cbForma);

        painelPagamento.add(new JLabel("Valor recebido (se dinheiro):"));
        painelPagamento.add(txtValorRecebido);

        painelPagamento.add(chkDesconto);
        painelPagamento.add(chkCombo);

        painelPagamento.add(lblTotal);
        painelPagamento.add(lblTroco);

        JButton btnCalcular = new JButton("Calcular Total com Benefício");
        btnCalcular.addActionListener(e -> calcularTotalBeneficio());
        painelPagamento.add(btnCalcular);

        JButton btnPagar = new JButton("Pagar");
        btnPagar.addActionListener(e -> realizarPagamento());
        painelPagamento.add(btnPagar);

        JButton btnAtualizar = new JButton("Atualizar pedidos");
        btnAtualizar.addActionListener(e -> carregarPedidos());
        painelPagamento.add(btnAtualizar);

        // Botão para ver itens do pedido
        JButton btnVerItens = new JButton("Ver itens do pedido");
        btnVerItens.addActionListener(e -> abrirDetalhesPedidoSelecionado());
        painelPagamento.add(btnVerItens);

        add(scroll, BorderLayout.CENTER);
        add(painelPagamento, BorderLayout.EAST);

        tabelaPedidos.getSelectionModel().addListSelectionListener(e -> selecionarPedido());
    }

    private void carregarPedidos() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Cliente ID", "Mesa", "Data", "Status", "Total"}, 0);
        List<Pedido> pedidos = pedidoRepo.findByStatus(StatusPedido.ENTREGUE);
        for (Pedido p : pedidos) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getCliente().getId(),
                    p.getNumeroMesa(),
                    p.getDataCriacao(),
                    p.getStatus(),
                    p.getValorTotal()
            });
        }
        tabelaPedidos.setModel(model);
        pedidoSelecionado = null;
        clienteDoPedido = null;
        pontosAntes = 0;
        lblTotal.setText("Total: R$ 0,00");
        lblTroco.setText("Troco: R$ 0,00");
    }

    private void selecionarPedido() {
        int row = tabelaPedidos.getSelectedRow();
        if (row == -1) {
            pedidoSelecionado = null;
            clienteDoPedido = null;
            pontosAntes = 0;
            return;
        }
        Long idPedido = (Long) tabelaPedidos.getValueAt(row, 0);
        Long idCliente = (Long) tabelaPedidos.getValueAt(row, 1);

        pedidoSelecionado = pedidoRepo.findById(idPedido).orElse(null);
        clienteDoPedido = clienteRepo.findById(idCliente).orElse(null);

        // guarda pontos atuais ANTES do pagamento, para comparar depois
        if (clienteDoPedido != null) {
            pontosAntes = clienteDoPedido.getPontosFidelidade();
        } else {
            pontosAntes = 0;
        }

        if (pedidoSelecionado != null) {
            lblTotal.setText("Total: R$ " + pedidoSelecionado.getValorTotal());
        }
    }

    private void calcularTotalBeneficio() {
        if (pedidoSelecionado == null || clienteDoPedido == null) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean usarDesc = chkDesconto.isSelected();
        boolean usarCombo = chkCombo.isSelected();
        BigDecimal novoTotal = caixaController.calcularTotalComBeneficio(pedidoSelecionado, clienteDoPedido,
                usarDesc, usarCombo);
        lblTotal.setText("Total: R$ " + novoTotal);
    }

    private void realizarPagamento() {
        if (pedidoSelecionado == null || clienteDoPedido == null) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FormaPagamento forma = (FormaPagamento) cbForma.getSelectedItem();
        BigDecimal valorRecebido = BigDecimal.ZERO;

        if (forma == FormaPagamento.DINHEIRO) {
            try {
                valorRecebido = new BigDecimal(txtValorRecebido.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Valor recebido inválido.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        ResultadoPagamento res = caixaController.pagar(pedidoSelecionado, clienteDoPedido, forma, valorRecebido);
        if (!res.isPago()) {
            JOptionPane.showMessageDialog(this, res.getMensagem(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        lblTroco.setText("Troco: R$ " + res.getTroco());
        JOptionPane.showMessageDialog(this, res.getMensagem(), "OK", JOptionPane.INFORMATION_MESSAGE);

        // 🔔 NOVO: verifica se atingiu 10 ou 20 pontos e mostra aviso
        try {
            Cliente clienteAtualizado = clienteRepo.findById(clienteDoPedido.getId()).orElse(null);
            if (clienteAtualizado != null) {
                int pontosDepois = clienteAtualizado.getPontosFidelidade();

                if (pontosAntes == 9 && pontosDepois == 10) {
                    JOptionPane.showMessageDialog(this,
                            "🎉 Cliente atingiu 10 pontos!\n" +
                            "Ele ganhou um benefício de 10% de desconto para a próxima compra.",
                            "Programa de Fidelidade",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (pontosAntes == 19 && pontosDepois == 0) {
                    JOptionPane.showMessageDialog(this,
                            "🎁 Cliente atingiu 20 pontos!\n" +
                            "Ele ganhou um COMBO GRÁTIS e os pontos foram resetados para 0.",
                            "Programa de Fidelidade",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            // Não quebra o fluxo se der erro no aviso
            e.printStackTrace();
        }

        carregarPedidos();
    }

    // Ver itens do pedido selecionado
    private void abrirDetalhesPedidoSelecionado() {
        if (pedidoSelecionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um pedido na tabela.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<ItemPedido> itens = pedidoRepo.listarItensDoPedido(pedidoSelecionado.getId());
            if (itens.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Esse pedido não possui itens cadastrados.",
                        "Aviso",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            PedidoDetalheDialog dialog = new PedidoDetalheDialog(this, pedidoSelecionado, itens);
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
