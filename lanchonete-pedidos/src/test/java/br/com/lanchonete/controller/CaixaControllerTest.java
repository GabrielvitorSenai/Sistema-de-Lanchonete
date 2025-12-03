package br.com.lanchonete.controller;

import br.com.lanchonete.model.entity.*;
import br.com.lanchonete.model.enums.TipoBeneficio;
import br.com.lanchonete.model.repository.*;
import br.com.lanchonete.model.enums.StatusPedido;
import br.com.lanchonete.service.*;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CaixaControllerTest {

    private CaixaController controller;
    private CaixaService caixaService;
    private PedidoRepository pedidoRepo;
    private FidelidadeBeneficioRepository benefRepo;
    private FidelidadeService fidelidadeService;

    @BeforeEach
    void setup() {
        caixaService = Mockito.mock(CaixaService.class);
        pedidoRepo = Mockito.mock(PedidoRepository.class);
        benefRepo = Mockito.mock(FidelidadeBeneficioRepository.class);
        fidelidadeService = Mockito.mock(FidelidadeService.class);

        controller = new CaixaController(caixaService, pedidoRepo, benefRepo, fidelidadeService);
    }

    @Test
    @DisplayName("Deve aplicar beneficio de 10% corretamente")
    void deveAplicarDesconto10() {
        Pedido pedido = new Pedido();
        pedido.setValorTotal(new BigDecimal("100"));

        Cliente cliente = new Cliente();

        FidelidadeBeneficio b = new FidelidadeBeneficio();
        b.setTipo(TipoBeneficio.DESCONTO_10);

        when(benefRepo.findPrimeiroAtivoByClienteAndTipo(any(), eq(TipoBeneficio.DESCONTO_10)))
                .thenReturn(b);

        BigDecimal total = controller.calcularTotalComBeneficio(pedido, cliente, true, false);

        assertEquals(new BigDecimal("90.00"), total);
    }
}
