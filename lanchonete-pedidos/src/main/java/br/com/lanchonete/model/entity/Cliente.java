package br.com.lanchonete.model.entity;

import java.time.LocalDate;

public class Cliente {

    private Long id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private String cpf;
    private String senha;
    private boolean ativo;
    private int pontosFidelidade;

    public Cliente() {
        this.ativo = true;
        this.pontosFidelidade = 0;
    }

    public Cliente(Long id, String nome, String email, LocalDate dataNascimento,
                   String cpf, String senha, boolean ativo, int pontosFidelidade) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
        this.senha = senha;
        this.ativo = ativo;
        this.pontosFidelidade = pontosFidelidade;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public int getPontosFidelidade() { return pontosFidelidade; }
    public void setPontosFidelidade(int pontosFidelidade) { this.pontosFidelidade = pontosFidelidade; }
}
