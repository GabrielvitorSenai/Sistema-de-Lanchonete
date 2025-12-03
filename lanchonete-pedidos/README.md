Sistema de Gestão de Pedidos – Lanchonete

Aplicação Desktop desenvolvida em Java Swing (Projeto Acadêmico – SENAI)

Este sistema implementa um fluxo completo de pedidos para uma lanchonete, contemplando cadastro de produtos, controle operacional (cliente → cozinha → garçom → caixa → gerente), fidelidade e relatórios.

1. Tecnologias Utilizadas

Java 8+

Java Swing (interface gráfica)

Maven

MySQL 8+

JUnit 5 + Mockito

Padrões de projeto:

MVC

DAO/Repository

Service Layer

Singleton

Controller

2. Funcionalidades Implementadas
2.1. Autenticação e Perfis de Acesso

O sistema possui controle de acesso por perfil, permitindo que cada usuário visualize apenas suas funcionalidades:

Gerente

Caixa

Cozinha

Garçom

Cliente

2.2. Cadastros
Produtos

Campos:

Nome

Descrição

Categoria (Lanche, Bebida, Combo)

Preço

Estoque

Status (Ativo/Inativo)

Clientes

Cadastro simples com nome, e-mail e CPF.

Usuários do sistema

Criados e gerenciados pelo Gerente.

2.3. Pedidos (Fluxo Completo)

O cliente monta seu carrinho e cria o pedido.

A cozinha altera o status dos pedidos:

NOVO → EM PRODUÇÃO → PRONTO

O garçom altera o status:

A CAMINHO → ENTREGUE

O caixa realiza o pagamento com:

Dinheiro

Débito

Crédito

Pix

O sistema calcula automaticamente o troco quando aplicável.

2.4. Programa de Fidelidade

O cliente recebe 1 ponto por pedido concluído.

Ao atingir 10 pontos, ganha um cupom de 10% de desconto.

Ao atingir 20 pontos, ganha um combo grátis e os pontos são zerados.

Benefícios possuem validade e são controlados pelo sistema (utilizado / não utilizado).

2.5. Caixa

Aplicação automática de benefícios (10% ou combo grátis)

Cálculo de total, troco e métodos de pagamento

Atualização automática do status do pedido para CONCLUÍDO

2.6. Relatórios (Gerente)

O sistema gera relatórios com:

Itens mais vendidos no período

Quantidade vendida por item

Valor total vendido por item

Total acumulado no período

Total diário

Também é possível:

Exportar o relatório gerado para um arquivo .txt