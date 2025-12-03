package br.com.lanchonete.view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import br.com.lanchonete.model.entity.ItemPedido;
import br.com.lanchonete.model.entity.Pedido;

public class PedidoDetalheDialog extends JDialog {

    private final Pedido pedido;
    private final List<ItemPedido> itens;

    public PedidoDetalheDialog(Frame owner, Pedido pedido, List<ItemPedido> itens) {
        super(owner, "Detalhes do Pedido #" + pedido.getId(), true);
        this.pedido = pedido;
        this.itens = itens;
        initComponents();
    }

    private void initComponents() {
        setSize(600, 400);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new GridLayout(2, 2, 5, 5));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        header.add(new JLabel("Pedido ID: " + pedido.getId()));
        header.add(new JLabel("Status: " + pedido.getStatus()));
        header.add(new JLabel("Data: " + pedido.getDataCriacao()));
        header.add(new JLabel("Total: R$ " + pedido.getValorTotal()));

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Produto", "Qtd", "Preço Unit.", "Subtotal"}, 0
        );
        JTable tabela = new JTable(model);

        BigDecimal totalItens = BigDecimal.ZERO;
        for (ItemPedido item : itens) {
            model.addRow(new Object[]{
                    item.getProduto().getNome(),
                    item.getQuantidade(),
                    item.getPrecoUnitario(),
                    item.getSubtotal()
            });
            totalItens = totalItens.add(item.getSubtotal());
        }

        JScrollPane scroll = new JScrollPane(tabela);

        JLabel lblTotalItens = new JLabel("Total calculado pelos itens: R$ " + totalItens);
        lblTotalItens.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(lblTotalItens, BorderLayout.SOUTH);
    }
}
