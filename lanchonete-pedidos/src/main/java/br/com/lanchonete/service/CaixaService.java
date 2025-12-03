package br.com.lanchonete.service;

import java.math.BigDecimal;

import br.com.lanchonete.model.entity.Pedido;

public class CaixaService {

    public enum FormaPagamento {
        DINHEIRO, DEBITO, CREDITO, PIX
    }

    public static class ResultadoPagamento {
        private boolean pago;
        private BigDecimal troco;
        private String mensagem;

        public boolean isPago() { return pago; }
        public void setPago(boolean pago) { this.pago = pago; }

        public BigDecimal getTroco() { return troco; }
        public void setTroco(BigDecimal troco) { this.troco = troco; }

        public String getMensagem() { return mensagem; }
        public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    }

    public ResultadoPagamento pagar(Pedido pedido, FormaPagamento forma,
                                    BigDecimal valorRecebido) {

        ResultadoPagamento resultado = new ResultadoPagamento();
        BigDecimal total = pedido.getValorTotal();

        if (forma == FormaPagamento.DINHEIRO) {
            if (valorRecebido.compareTo(total) < 0) {
                resultado.setPago(false);
                resultado.setMensagem("Valor recebido menor que o total.");
                resultado.setTroco(BigDecimal.ZERO);
                return resultado;
            }
            BigDecimal troco = valorRecebido.subtract(total);
            resultado.setTroco(troco);
            if (troco.compareTo(BigDecimal.ZERO) == 0) {
                resultado.setMensagem("Pagamento em dinheiro sem troco.");
            } else {
                resultado.setMensagem("Pagamento em dinheiro com troco de: " + troco);
            }
        } else {
            resultado.setTroco(BigDecimal.ZERO);
            resultado.setMensagem("Pagamento realizado via " + forma);
        }

        resultado.setPago(true);
        return resultado;
    }
}
