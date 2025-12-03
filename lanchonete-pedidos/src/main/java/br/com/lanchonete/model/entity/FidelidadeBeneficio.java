package br.com.lanchonete.model.entity;

import java.time.LocalDate;

import br.com.lanchonete.model.enums.TipoBeneficio;

public class FidelidadeBeneficio {

    private Long id;
    private Cliente cliente;
    private TipoBeneficio tipo;
    private LocalDate validade;
    private boolean utilizado;

    public FidelidadeBeneficio() {
    }

    public FidelidadeBeneficio(Long id, Cliente cliente, TipoBeneficio tipo,
                               LocalDate validade, boolean utilizado) {
        this.id = id;
        this.cliente = cliente;
        this.tipo = tipo;
        this.validade = validade;
        this.utilizado = utilizado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public TipoBeneficio getTipo() { return tipo; }
    public void setTipo(TipoBeneficio tipo) { this.tipo = tipo; }

    public LocalDate getValidade() { return validade; }
    public void setValidade(LocalDate validade) { this.validade = validade; }

    public boolean isUtilizado() { return utilizado; }
    public void setUtilizado(boolean utilizado) { this.utilizado = utilizado; }
}
