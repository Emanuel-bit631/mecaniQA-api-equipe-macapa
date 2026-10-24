
CREATE DATABASE IF NOT EXISTS mecaniqa_db;
USE mecaniqa_db;

CREATE TABLE IF NOT EXISTS pecas (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     nome VARCHAR(100) NOT NULL,
    codigo_barras VARCHAR(50) UNIQUE,
    fornecedor_marca VARCHAR(100),
    quantidade_estoque INT NOT NULL DEFAULT 0,
    preco_custo DOUBLE NOT NULL,
    preco_venda DOUBLE NOT NULL,
    data_cadastro DATETIME NOT NULL,
    data_ultima_atualizacao DATETIME NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    tamanho VARCHAR(20),
    cor VARCHAR(30)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS servicos (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        nome VARCHAR(100) NOT NULL,
    tempo_estimado_minutos INT NOT NULL,
    custo_tabelado DOUBLE NOT NULL,
    data_criacao DATETIME NOT NULL,
    data_ultima_atualizacao DATETIME NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ordens_servico (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              cliente VARCHAR(100) NOT NULL,
    veiculo VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ordem_servico_servicos (
                                                      ordem_servico_id BIGINT NOT NULL,
                                                      servico_id BIGINT NOT NULL,
                                                      PRIMARY KEY (ordem_servico_id, servico_id),
    CONSTRAINT fk_os_servicos_os FOREIGN KEY (ordem_servico_id)
    REFERENCES ordens_servico(id) ON DELETE CASCADE,
    CONSTRAINT fk_os_servicos_servico FOREIGN KEY (servico_id)
    REFERENCES servicos(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS pedidos_pecas (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             ordem_servico_id BIGINT NOT NULL,
                                             data_pedido DATETIME NOT NULL,
                                             status VARCHAR(30) NOT NULL,
    CONSTRAINT fk_pedidos_os FOREIGN KEY (ordem_servico_id)
    REFERENCES ordens_servico(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS itens_pedido (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            pedido_id BIGINT NOT NULL,
                                            peca_id BIGINT NOT NULL,
                                            quantidade INT NOT NULL,
                                            preco_unitario DOUBLE NOT NULL,
                                            CONSTRAINT fk_itens_pedido FOREIGN KEY (pedido_id)
    REFERENCES pedidos_pecas(id) ON DELETE CASCADE,
    CONSTRAINT fk_itens_peca FOREIGN KEY (peca_id)
    REFERENCES pecas(id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO pecas (nome, codigo_barras, fornecedor_marca, quantidade_estoque, preco_custo, preco_venda, data_cadastro, data_ultima_atualizacao, categoria, tamanho, cor)
VALUES ('Filtro de Óleo', '7891234567890', 'Bosch', 50, 15.00, 35.00, NOW(), NOW(), 'MOTOR', 'M', 'Preto');

INSERT INTO servicos (nome, tempo_estimado_minutos, custo_tabelado, data_criacao, data_ultima_atualizacao)
VALUES ('Troca de Óleo e Filtro', 45, 120.00, NOW(), NOW());