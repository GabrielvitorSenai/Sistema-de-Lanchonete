package br.com.lanchonete.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import br.com.lanchonete.model.entity.ItemPedido;
import br.com.lanchonete.model.entity.Pedido;
import br.com.lanchonete.model.entity.Produto;
import br.com.lanchonete.model.enums.StatusPedido;
import br.com.lanchonete.model.repository.impl.PedidoRepositoryImpl;
import br.com.lanchonete.model.repository.impl.ProdutoRepositoryImpl;

public class RelatorioGerenteView extends JFrame {

    private final PedidoRepositoryImpl pedidoRepo = new PedidoRepositoryImpl();
    private final ProdutoRepositoryImpl produtoRepo = new ProdutoRepositoryImpl();

    private JTextField txtDataInicio;
    private JTextField txtDataFim;
    private JLabel lblTotalPeriodo;
    private JLabel lblTotalDia;
    private JTable tabelaItens;
    private DefaultTableModel modelItens;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Dados do último relatório (para exportar)
    private LocalDate ultimoInicio;
    private LocalDate ultimoFim;
    private BigDecimal ultimoTotalPeriodo = BigDecimal.ZERO;
    private BigDecimal ultimoTotalDia = BigDecimal.ZERO;
    private List<AcumuladorItem> ultimoRanking = new ArrayList<>();

    public RelatorioGerenteView() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Relatórios de Vendas - Gerente");
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ====== PAINEL DE FILTROS (2 LINHAS) ======
        JPanel painelFiltros = new JPanel(new BorderLayout());
        painelFiltros.setBorder(BorderFactory.createTitledBorder("Período"));

        // Linha 1: datas + Hoje + Mês atual
        JPanel linha1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        linha1.add(new JLabel("Data início (dd/MM/aaaa):"));
        txtDataInicio = new JTextField(10);
        linha1.add(txtDataInicio);

        linha1.add(new JLabel("Data fim (dd/MM/aaaa):"));
        txtDataFim = new JTextField(10);
        linha1.add(txtDataFim);

        JButton btnHoje = new JButton("Hoje");
        btnHoje.addActionListener(e -> aplicarHoje());
        linha1.add(btnHoje);

        JButton btnMesAtual = new JButton("Mês atual");
        btnMesAtual.addActionListener(e -> aplicarMesAtual());
        linha1.add(btnMesAtual);

        // Linha 2: Gerar + Exportar TXT
        JPanel linha2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton btnGerar = new JButton("Gerar relatório");
        btnGerar.addActionListener(e -> gerarRelatorio());
        linha2.add(btnGerar);

        JButton btnExportar = new JButton("Exportar TXT");
        btnExportar.addActionListener(e -> exportarParaTxt());
        linha2.add(btnExportar);

        painelFiltros.add(linha1, BorderLayout.NORTH);
        painelFiltros.add(linha2, BorderLayout.SOUTH);

        add(painelFiltros, BorderLayout.NORTH);

        // ====== TABELA ITENS MAIS VENDIDOS ======
        modelItens = new DefaultTableModel(
                new Object[]{"Produto", "Quantidade total", "Valor total vendido"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaItens = new JTable(modelItens);
        JScrollPane scrollItens = new JScrollPane(tabelaItens);
        scrollItens.setBorder(BorderFactory.createTitledBorder("Itens mais vendidos no período"));

        add(scrollItens, BorderLayout.CENTER);

        // ====== RODAPÉ ======
        JPanel painelResumo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        lblTotalPeriodo = new JLabel("Total no período: R$ 0,00");
        lblTotalDia = new JLabel("Total diário: R$ 0,00 (quando início = fim)");
        painelResumo.add(lblTotalPeriodo);
        painelResumo.add(lblTotalDia);
        add(painelResumo, BorderLayout.SOUTH);
    }

    // ==== BOTÕES RÁPIDOS ====

    private void aplicarHoje() {
        LocalDate hoje = LocalDate.now();
        String dataStr = hoje.format(formatter);
        txtDataInicio.setText(dataStr);
        txtDataFim.setText(dataStr);
        gerarRelatorio();
    }

    private void aplicarMesAtual() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.withDayOfMonth(1);
        LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());

        txtDataInicio.setText(inicio.format(formatter));
        txtDataFim.setText(fim.format(formatter));
        gerarRelatorio();
    }

    // ==== GERAÇÃO DO RELATÓRIO ====

    private void gerarRelatorio() {
        LocalDate inicio;
        LocalDate fim;

        try {
            inicio = LocalDate.parse(txtDataInicio.getText().trim(), formatter);
            fim = LocalDate.parse(txtDataFim.getText().trim(), formatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Datas inválidas. Use o formato dd/MM/aaaa.",
                    "Erro de data",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (fim.isBefore(inicio)) {
            JOptionPane.showMessageDialog(this,
                    "Data fim não pode ser antes da data início.",
                    "Erro de período",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        ultimoInicio = inicio;
        ultimoFim = fim;

        List<Pedido> todos = pedidoRepo.findAll();
        List<Pedido> pedidosPeriodo = new ArrayList<>();

        for (Pedido p : todos) {
            if (p.getStatus() != StatusPedido.CONCLUIDO) continue;
            if (p.getDataCriacao() == null) continue;

            LocalDate dataPedido = p.getDataCriacao().toLocalDate();
            if ((dataPedido.isEqual(inicio) || dataPedido.isAfter(inicio)) &&
                (dataPedido.isEqual(fim) || dataPedido.isBefore(fim))) {
                pedidosPeriodo.add(p);
            }
        }

        BigDecimal totalPeriodo = BigDecimal.ZERO;
        for (Pedido p : pedidosPeriodo) {
            if (p.getValorTotal() != null) {
                totalPeriodo = totalPeriodo.add(p.getValorTotal());
            }
        }
        ultimoTotalPeriodo = totalPeriodo;

        BigDecimal totalDia = BigDecimal.ZERO;
        if (inicio.equals(fim)) {
            for (Pedido p : pedidosPeriodo) {
                LocalDate dataPedido = p.getDataCriacao().toLocalDate();
                if (dataPedido.equals(inicio) && p.getValorTotal() != null) {
                    totalDia = totalDia.add(p.getValorTotal());
                }
            }
            ultimoTotalDia = totalDia;
            lblTotalDia.setText("Total diário (" + inicio.format(formatter) + "): R$ " + totalDia);
        } else {
            ultimoTotalDia = BigDecimal.ZERO;
            lblTotalDia.setText("Total diário: R$ 0,00 (quando início = fim)");
        }

        lblTotalPeriodo.setText("Total no período (" +
                inicio.format(formatter) + " a " + fim.format(formatter) + "): R$ " + totalPeriodo);

        montarRankingItens(pedidosPeriodo);
    }

    // ==== AUXILIAR PARA RANKING ====

    private static class AcumuladorItem {
        String nome;
        int quantidade;
        BigDecimal valorTotal;

        AcumuladorItem(String nome) {
            this.nome = nome;
            this.quantidade = 0;
            this.valorTotal = BigDecimal.ZERO;
        }
    }

    private void montarRankingItens(List<Pedido> pedidosPeriodo) {
        Map<Long, AcumuladorItem> mapa = new HashMap<>();

        for (Pedido p : pedidosPeriodo) {
            if (p.getItens() == null) continue;

            for (ItemPedido item : p.getItens()) {
                Produto prod = item.getProduto();
                if (prod == null || prod.getId() == null) continue;

                Long idProd = prod.getId();
                String nomeProd = prod.getNome();

                if (nomeProd == null || nomeProd.trim().isEmpty()) {
                    Produto prodBanco = produtoRepo.findById(idProd).orElse(null);
                    if (prodBanco != null && prodBanco.getNome() != null) {
                        nomeProd = prodBanco.getNome();
                    } else {
                        nomeProd = "Produto ID " + idProd;
                    }
                }

                AcumuladorItem acc = mapa.get(idProd);
                if (acc == null) {
                    acc = new AcumuladorItem(nomeProd);
                    mapa.put(idProd, acc);
                }

                int qtd = item.getQuantidade();
                BigDecimal subtotal;
                if (item.getSubtotal() != null) {
                    subtotal = item.getSubtotal();
                } else if (item.getPrecoUnitario() != null) {
                    subtotal = item.getPrecoUnitario().multiply(BigDecimal.valueOf(qtd));
                } else if (prod.getPreco() != null) {
                    subtotal = prod.getPreco().multiply(BigDecimal.valueOf(qtd));
                } else {
                    subtotal = BigDecimal.ZERO;
                }

                acc.quantidade += qtd;
                acc.valorTotal = acc.valorTotal.add(subtotal);
            }
        }

        List<AcumuladorItem> lista = new ArrayList<>(mapa.values());
        lista.sort(Comparator.comparingInt((AcumuladorItem a) -> a.quantidade).reversed());
        ultimoRanking = lista;

        modelItens.setRowCount(0);
        for (AcumuladorItem acc : lista) {
            modelItens.addRow(new Object[]{
                    acc.nome,
                    acc.quantidade,
                    acc.valorTotal
            });
        }
    }

    // ==== EXPORTAÇÃO TXT ====

    private void exportarParaTxt() {
        if (ultimoRanking == null || ultimoRanking.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nenhum relatório gerado ainda. Gere o relatório antes de exportar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar relatório em TXT");

        String nomeBase;
        if (ultimoInicio != null && ultimoFim != null) {
            DateTimeFormatter fArq = DateTimeFormatter.ofPattern("yyyyMMdd");
            nomeBase = "relatorio_" + ultimoInicio.format(fArq) + "_" + ultimoFim.format(fArq) + ".txt";
        } else {
            nomeBase = "relatorio_vendas.txt";
        }
        chooser.setSelectedFile(new File(nomeBase));

        int opc = chooser.showSaveDialog(this);
        if (opc != JFileChooser.APPROVE_OPTION) return;

        File arquivo = chooser.getSelectedFile();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
            bw.write("RELATÓRIO DE VENDAS - GERENTE");
            bw.newLine();
            bw.write("----------------------------------------");
            bw.newLine();

            if (ultimoInicio != null && ultimoFim != null) {
                bw.write("Período: " + ultimoInicio.format(formatter) +
                        " até " + ultimoFim.format(formatter));
                bw.newLine();
            }

            bw.write(String.format(Locale.US,
                    "Total no período: R$ %.2f", ultimoTotalPeriodo));
            bw.newLine();

            if (ultimoTotalDia != null && ultimoTotalDia.compareTo(BigDecimal.ZERO) > 0) {
                bw.write(String.format(Locale.US,
                        "Total diário: R$ %.2f", ultimoTotalDia));
                bw.newLine();
            }

            bw.newLine();
            bw.write("Itens mais vendidos:");
            bw.newLine();
            bw.write(String.format("%-40s %-15s %-15s",
                    "Produto", "Quantidade", "Valor total"));
            bw.newLine();
            bw.write("---------------------------------------------------------------------");
            bw.newLine();

            for (AcumuladorItem acc : ultimoRanking) {
                bw.write(String.format(Locale.US,
                        "%-40s %-15d R$ %-15.2f",
                        acc.nome,
                        acc.quantidade,
                        acc.valorTotal));
                bw.newLine();
            }

            JOptionPane.showMessageDialog(this,
                    "Relatório exportado com sucesso para:\n" + arquivo.getAbsolutePath(),
                    "Exportação concluída",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao exportar relatório: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
