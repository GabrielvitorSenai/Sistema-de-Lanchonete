package br.com.lanchonete.controller;

import java.math.BigDecimal;

import br.com.lanchonete.model.entity.Cliente;
import br.com.lanchonete.model.entity.FidelidadeBeneficio;
import br.com.lanchonete.model.entity.Pedido;
import br.com.lanchonete.model.enums.StatusPedido;
import br.com.lanchonete.model.enums.TipoBeneficio;
import br.com.lanchonete.model.repository.FidelidadeBeneficioRepository;
import br.com.lanchonete.model.repository.PedidoRepository;
import br.com.lanchonete.service.CaixaService;
import br.com.lanchonete.service.CaixaService.FormaPagamento;
import br.com.lanchonete.service.CaixaService.ResultadoPagamento;
import br.com.lanchonete.service.FidelidadeService;

public class CaixaController {

    private final CaixaService caixaService;
    private final PedidoRepository pedidoRepository;
    private final FidelidadeBeneficioRepository beneficioRepository;
    private final FidelidadeService fidelidadeService;

    public CaixaController(CaixaService caixaService,
                           PedidoRepository pedidoRepository,
                           FidelidadeBeneficioRepository beneficioRepository,
                           FidelidadeService fidelidadeService) {
        this.caixaService = caixaService;
        this.pedidoRepository = pedidoRepository;
        this.beneficioRepository = beneficioRepository;
        this.fidelidadeService = fidelidadeService;
    }

    /**
     * Aplica benefícios de fidelidade (desconto ou combo grátis),
     * atualiza o valor_total do pedido e persiste no banco.
     *
     * Regra:
     * - Se usarComboGratis = true e existir benefício COMBO_GRATIS -> total = 0
     * - Senão, se usarDesconto10 = true e existir benefício DESCONTO_10 -> total * 0.9
     * - Nunca aplica os dois ao mesmo tempo (combo tem prioridade).
     */
    public BigDecimal calcularTotalComBeneficio(Pedido pedido, Cliente cliente,
                                                boolean usarDesconto10, boolean usarComboGratis) {

        BigDecimal total = pedido.getValorTotal();

        // 1) COMBO GRÁTIS tem prioridade e zera o valor
        if (usarComboGratis) {
            FidelidadeBeneficio b = beneficioRepository
                    .findPrimeiroAtivoByClienteAndTipo(cliente, TipoBeneficio.COMBO_GRATIS);
            if (b != null) {
                // Combo grátis zera o total do pedido
                total = BigDecimal.ZERO;

                b.setUtilizado(true);
                beneficioRepository.update(b);
            }
        }
        // 2) Se não usou combo, pode aplicar o DESCONTO DE 10%
        else if (usarDesconto10) {
            FidelidadeBeneficio b = beneficioRepository
                    .findPrimeiroAtivoByClienteAndTipo(cliente, TipoBeneficio.DESCONTO_10);
            if (b != null) {
                total = total.multiply(new BigDecimal("0.90")); // 10% de desconto

                b.setUtilizado(true);
                beneficioRepository.update(b);
            }
        }

        // Atualiza valor no pedido e persiste
        pedido.setValorTotal(total);
        pedidoRepository.update(pedido);
        return total;
    }

    /**
     * Realiza o pagamento, marca o pedido como CONCLUIDO
     * e adiciona 1 ponto de fidelidade para o cliente.
     */
    public ResultadoPagamento pagar(Pedido pedido,
                                    Cliente cliente,
                                    FormaPagamento forma,
                                    BigDecimal valorRecebido) {

        ResultadoPagamento res = caixaService.pagar(pedido, forma, valorRecebido);

        if (res.isPago()) {
            // Atualiza status do pedido
            pedido.setStatus(StatusPedido.CONCLUIDO);
            pedidoRepository.update(pedido);

            // Adiciona 1 ponto por visita (pagamento concluído)
            if (cliente != null) {
                try {
                    fidelidadeService.adicionarPontoPorVisita(cliente);
                } catch (Exception e) {
                    // Não quebra o fluxo de pagamento se der erro ao somar ponto
                    System.err.println("Erro ao adicionar ponto de fidelidade: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        return res;
    }
}
