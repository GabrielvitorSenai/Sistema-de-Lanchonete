package br.com.lanchonete.service;

import java.time.LocalDate;

import br.com.lanchonete.model.entity.Cliente;
import br.com.lanchonete.model.entity.FidelidadeBeneficio;
import br.com.lanchonete.model.enums.TipoBeneficio;
import br.com.lanchonete.model.repository.ClienteRepository;
import br.com.lanchonete.model.repository.FidelidadeBeneficioRepository;

public class FidelidadeService {

    private final ClienteRepository clienteRepository;
    private final FidelidadeBeneficioRepository beneficioRepository;

    public FidelidadeService(ClienteRepository clienteRepository,
                             FidelidadeBeneficioRepository beneficioRepository) {
        this.clienteRepository = clienteRepository;
        this.beneficioRepository = beneficioRepository;
    }

    /**
     * Soma 1 ponto por visita após o pagamento.
     * Regras:
     * - 10 pontos → gera DESCONTO_10 (val. 30 dias)
     * - 20 pontos → gera COMBO_GRATIS (val. 30 dias) e reseta pontos para 0
     */
    public void adicionarPontoPorVisita(Cliente cliente) {

        // Sempre recarrega do banco para garantir dados atualizados
        Cliente cli = clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente não encontrado: " + cliente.getId()));

        int pontosAtuais = cli.getPontosFidelidade();
        pontosAtuais++;
        cli.setPontosFidelidade(pontosAtuais);

        // --- Regras de Benefícios ---
        if (pontosAtuais == 10) {
            gerarBeneficio(cli, TipoBeneficio.DESCONTO_10, 30);
        }

        if (pontosAtuais == 20) {
            gerarBeneficio(cli, TipoBeneficio.COMBO_GRATIS, 30);
            cli.setPontosFidelidade(0); // reset dos pontos
        }

        // Atualiza cliente no banco
        clienteRepository.update(cli);
    }

    /**
     * Cria um benefício e salva no banco.
     */
    private void gerarBeneficio(Cliente cliente, TipoBeneficio tipo, int diasValidade) {
        FidelidadeBeneficio beneficio = new FidelidadeBeneficio();
        beneficio.setCliente(cliente);
        beneficio.setTipo(tipo);
        beneficio.setValidade(LocalDate.now().plusDays(diasValidade));
        beneficio.setUtilizado(false);

        beneficioRepository.save(beneficio);
    }
}
