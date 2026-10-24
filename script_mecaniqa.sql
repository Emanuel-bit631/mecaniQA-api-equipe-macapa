
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

-- ==========================================================
-- DML: dados de demonstração do MecaniQA
-- Os INSERTs verificam se os registros já existem.
-- ==========================================================

-- Peça de demonstração
INSERT INTO pecas (
    nome, codigo_barras, fornecedor_marca,
    quantidade_estoque, preco_custo, preco_venda,
    data_cadastro, data_ultima_atualizacao,
    categoria, tamanho, cor
)
SELECT
    'Lanterna OAT3', '7899999900013', 'Bosch',
    10, 120.00, 185.00,
    NOW(), NOW(), 'ELETRICA', 'Universal', 'Preto'
WHERE NOT EXISTS (
    SELECT 1 FROM pecas
    WHERE codigo_barras = '7899999900013'
);

-- Serviço de demonstração
INSERT INTO servicos (
    nome, tempo_estimado_minutos, custo_tabelado,
    data_criacao, data_ultima_atualizacao
)
SELECT
    'Revisao preventiva OAT3', 60, 150.00,
    NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM servicos
    WHERE nome = 'Revisao preventiva OAT3'
);

-- Ordem de serviço de demonstração
INSERT INTO ordens_servico (cliente, veiculo, status)
SELECT
    'Cliente Teste OAT3', 'Veiculo Teste OAT3', 'ABERTO'
WHERE NOT EXISTS (
    SELECT 1 FROM ordens_servico
    WHERE cliente = 'Cliente Teste OAT3'
      AND veiculo = 'Veiculo Teste OAT3'
);

-- Relaciona a ordem de serviço ao serviço
INSERT INTO ordem_servico_servicos (
    ordem_servico_id, servico_id
)
SELECT os.id, s.id
FROM ordens_servico os
INNER JOIN servicos s
    ON s.nome = 'Revisao preventiva OAT3'
WHERE os.cliente = 'Cliente Teste OAT3'
  AND os.veiculo = 'Veiculo Teste OAT3'
  AND NOT EXISTS (
      SELECT 1
      FROM ordem_servico_servicos oss
      WHERE oss.ordem_servico_id = os.id
        AND oss.servico_id = s.id
  );

-- Relaciona uma peça à ordem de serviço
INSERT INTO ordem_servico_itens (
    ordem_servico_id, peca_id, quantidade, preco_unitario
)
SELECT os.id, p.id, 1, p.preco_venda
FROM ordens_servico os
INNER JOIN pecas p
    ON p.codigo_barras = '7899999900013'
WHERE os.cliente = 'Cliente Teste OAT3'
  AND os.veiculo = 'Veiculo Teste OAT3'
  AND NOT EXISTS (
      SELECT 1
      FROM ordem_servico_itens osi
      WHERE osi.ordem_servico_id = os.id
        AND osi.peca_id = p.id
  );

-- Pedido de peças para a ordem de demonstração
INSERT INTO pedidos_pecas (
    ordem_servico_id, data_pedido, status
)
SELECT os.id, NOW(), 'ORCANDO'
FROM ordens_servico os
WHERE os.cliente = 'Cliente Teste OAT3'
  AND os.veiculo = 'Veiculo Teste OAT3'
  AND NOT EXISTS (
      SELECT 1
      FROM pedidos_pecas pp
      WHERE pp.ordem_servico_id = os.id
  );

-- Item do pedido de peças
INSERT INTO pedido_pecas_itens (
    pedido_id, peca_id, quantidade, preco_unitario
)
SELECT pp.id, p.id, 1, p.preco_custo
FROM pedidos_pecas pp
INNER JOIN ordens_servico os
    ON os.id = pp.ordem_servico_id
INNER JOIN pecas p
    ON p.codigo_barras = '7899999900013'
WHERE os.cliente = 'Cliente Teste OAT3'
  AND os.veiculo = 'Veiculo Teste OAT3'
  AND NOT EXISTS (
      SELECT 1
      FROM pedido_pecas_itens ppi
      WHERE ppi.pedido_id = pp.id
        AND ppi.peca_id = p.id
  );

-- ==========================================================
-- SELECT: consultas para conferir os registros de exemplo
-- ==========================================================

SELECT * FROM pecas
WHERE codigo_barras = '7899999900013';

SELECT * FROM servicos
WHERE nome = 'Revisao preventiva OAT3';

SELECT * FROM ordens_servico
WHERE cliente = 'Cliente Teste OAT3'
  AND veiculo = 'Veiculo Teste OAT3';

SELECT * FROM pedidos_pecas
WHERE ordem_servico_id IN (
    SELECT id FROM ordens_servico
    WHERE cliente = 'Cliente Teste OAT3'
      AND veiculo = 'Veiculo Teste OAT3'
);
