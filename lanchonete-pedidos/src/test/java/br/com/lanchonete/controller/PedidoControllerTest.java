package br.com.lanchonete.controller;

import br.com.lanchonete.model.entity.*;
import br.com.lanchonete.model.enums.StatusPedido;
import br.com.lanchonete.model.repository.PedidoRepository;
import br.com.lanchonete.service.PedidoService;
import br.com.lanchonete.service.FidelidadeService;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoControllerTest {

    private PedidoController controller;
    private PedidoService pedidoService;
    private PedidoRepository pedidoRepo;
    private FidelidadeService fidelidadeService;

    @BeforeEach
    void setup() {
        pedidoRepo = Mockito.mock(PedidoRepository.class);
        pedidoService = Mockito.mock(PedidoService.class);
        fidelidadeService = Mockito.mock(FidelidadeService.class);

        controller = new PedidoController(pedidoService, pedidoRepo, fidelidadeService);
    }

    @Test
    @DisplayName("Deve retornar lista de pedidos pendentes")
    void deveListarPedidosPendentes() {
        Pedido p = new Pedido();
        p.setStatus(StatusPedido.AGUARDANDO_PRODUCAO);

        when(pedidoRepo.findPendentesCozinha())
                .thenReturn(List.of(p));

        List<Pedido> result = controller.listarPendentesCozinha();

        assertEquals(1, result.size());
    }
}
