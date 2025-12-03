CREATE DATABASE IF NOT EXISTS lanchonete_db_teste
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE lanchonete_db_teste;

CREATE TABLE cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    data_nascimento DATE,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    pontos_fidelidade INT NOT NULL DEFAULT 0
);

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    perfil VARCHAR(20) NOT NULL,
    ativo TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE produto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    categoria VARCHAR(20) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    estoque INT NOT NULL,
    ativo TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    data_criacao DATETIME NOT NULL,
    status VARCHAR(30) NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente(id)
);

CREATE TABLE item_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (pedido_id)
        REFERENCES pedido(id),
    CONSTRAINT fk_item_pedido_produto FOREIGN KEY (produto_id)
        REFERENCES produto(id)
);

CREATE TABLE fidelidade_beneficio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    validade DATE NOT NULL,
    utilizado TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_fid_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente(id)
);
    INSERT INTO usuario (nome, email, senha, perfil, ativo)
    VALUES ('Gerente Master', 'gerente@lanchonete.com', '123456', 'GERENTE', 1);
    
    INSERT INTO usuario (nome, email, senha, perfil, ativo)
    VALUES ('Caixa', 'caixa@lanchonete.com', '123', 'CAIXA', 1);

    INSERT INTO usuario (nome, email, senha, perfil, ativo)
    VALUES ('Cozinha', 'cozinha@lanchonete.com', '123', 'COZINHA', 1);
    
    INSERT INTO usuario (nome, email, senha, perfil, ativo)
    VALUES ('Garcom', 'garcom@lanchonete.com', '123', 'GARCOM', 1);


    INSERT INTO produto (nome, descricao, categoria, preco, estoque, ativo)
    VALUES 
    ('X-Salada Especial', 'Pão brioche, hambúrguer 150g, queijo, alface, tomate e maionese da casa', 'LANCHE', 24.90, 50, 1),
    ('Coca-Cola Lata 350ml', 'Refrigerante lata gelado', 'BEBIDA', 6.00, 120, 1),
    ('Batata Frita com Cheddar', 'Porção de 400g com molho cheddar e bacon crocante', 'LANCHE', 28.50, 30, 1);


    ALTER TABLE pedido
    ADD COLUMN numero_mesa INT NULL AFTER cliente_id;

    INSERT INTO produto (nome, descricao, categoria, preco, estoque, ativo) VALUES
('Coxinha de Frango', 'Coxinha crocante recheada com frango', 'LANCHE', 5.50, 40, true),
('Coxinha com Catupiry', 'Coxinha com frango e catupiry', 'LANCHE', 6.50, 35, true),
('Bolinha de Queijo', 'Porção com 6 unidades', 'LANCHE', 7.00, 30, true),
('Pastel de Carne', 'Pastel grande de carne moída', 'LANCHE', 8.00, 25, true),
('Kibe Recheado', 'Kibe recheado com catupiry', 'LANCHE', 6.00, 30, true),
('Empada de Frango', 'Empada artesanal de frango', 'LANCHE', 6.50, 20, true),

('Espetinho de Carne', 'Carne bovina temperada', 'LANCHE', 10.00, 20, true),
('Espetinho de Frango', 'Frango ao tempero especial', 'LANCHE', 8.50, 22, true),
('Espetinho de Linguiça', 'Linguiça toscana assada', 'LANCHE', 9.00, 18, true),

('Guaraná Antarctica', 'Lata 350ml', 'BEBIDA', 6.00, 35, true),
('Pepsi', 'Lata 350ml', 'BEBIDA', 6.00, 30, true),
('Chá Gelado Pêssego', 'Garrafa 500ml', 'BEBIDA', 7.50, 20, true),
('Energético Fusion', 'Lata 269ml', 'BEBIDA', 12.00, 15, true),
('Suco Uva Integral', 'Garrafa 300ml', 'BEBIDA', 9.00, 18, true),

('Combo Salgados Mix', 'Coxinha + 3 bolinhas + kibe + refrigerante lata', 'COMBO', 21.90, 12, true);



