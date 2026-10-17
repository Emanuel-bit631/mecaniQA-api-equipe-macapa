
CREATE DATABASE IF NOT EXISTS mecaniqa;

USE mecaniqa;

-- Tabela de peças
CREATE TABLE IF NOT EXISTS pecas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    codigo_barras VARCHAR(100) NOT NULL,
    fornecedor_marca VARCHAR(150) NOT NULL,
    quantidade_estoque INT NOT NULL,
    preco_custo DECIMAL(12,2) NOT NULL,
    preco_venda DECIMAL(12,2) NOT NULL,
    data_cadastro DATETIME NOT NULL,
    data_ultima_atualizacao DATETIME NOT NULL,
    categoria VARCHAR(40) NOT NULL,
    tamanho VARCHAR(100),
    cor VARCHAR(100)
);

-- Tabela de serviços
CREATE TABLE IF NOT EXISTS servicos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    tempo_estimado_minutos INT NOT NULL,
    custo_tabelado DECIMAL(12,2) NOT NULL,
    data_criacao DATETIME NOT NULL,
    data_ultima_atualizacao DATETIME NOT NULL
);

-- Tabela de ordens de serviço
CREATE TABLE IF NOT EXISTS ordens_servico (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente VARCHAR(150) NOT NULL,
    veiculo VARCHAR(150) NOT NULL,
    status VARCHAR(40) NOT NULL
);

-- Tabela de pedidos de peças
CREATE TABLE IF NOT EXISTS pedidos_pecas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL,
    data_pedido DATETIME NOT NULL,
    status VARCHAR(40) NOT NULL,
    FOREIGN KEY (ordem_servico_id)
        REFERENCES ordens_servico(id)
);

-- Serviços de uma ordem de serviço
CREATE TABLE IF NOT EXISTS ordem_servico_servicos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL,
    servico_id BIGINT NOT NULL,
    FOREIGN KEY (ordem_servico_id)
        REFERENCES ordens_servico(id),
    FOREIGN KEY (servico_id)
        REFERENCES servicos(id)
);

-- Peças de uma ordem de serviço
CREATE TABLE IF NOT EXISTS ordem_servico_itens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ordem_servico_id BIGINT NOT NULL,
    peca_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (ordem_servico_id)
        REFERENCES ordens_servico(id),
    FOREIGN KEY (peca_id)
        REFERENCES pecas(id)
);

-- Itens de um pedido de peças
CREATE TABLE IF NOT EXISTS pedido_pecas_itens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    peca_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (pedido_id)
        REFERENCES pedidos_pecas(id),
    FOREIGN KEY (peca_id)
        REFERENCES pecas(id)
);
