package br.com.lanchonete.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import br.com.lanchonete.model.entity.Cliente;
import br.com.lanchonete.model.repository.ClienteRepository;

public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente cadastrarNovoCliente(String nome, String email, String dataNascStr,
                                        String cpf, String senha) {

        Optional<Cliente> existente = clienteRepository.findByEmail(email);
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Já existe cliente com esse e-mail.");
        }

        Cliente c = new Cliente();
        c.setNome(nome);
        c.setEmail(email);
        c.setCpf(cpf);
        c.setSenha(senha);
        c.setAtivo(true);
        c.setPontosFidelidade(0);

        // 👇 Ajuste para funcionar em Java 8 (sem isBlank)
        if (dataNascStr != null && !dataNascStr.trim().isEmpty()) {
            LocalDate dataNasc = LocalDate.parse(dataNascStr, formatter);
            c.setDataNascimento(dataNasc);
        }

        return clienteRepository.save(c);
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }
}
