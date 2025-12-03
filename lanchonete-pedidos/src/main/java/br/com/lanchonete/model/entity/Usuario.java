package br.com.lanchonete.model.entity;

import br.com.lanchonete.model.enums.PerfilUsuario;

public class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private PerfilUsuario perfil;
    private boolean ativo;

    public Usuario() {
        this.ativo = true;
    }

    public Usuario(Long id, String nome, String email, String senha,
                   PerfilUsuario perfil, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
        this.ativo = ativo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
