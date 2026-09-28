package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.ItemPedidoDTO;
import br.com.mecaniQA.api.dto.PedidoPecaRequestDTO;
import br.com.mecaniQA.api.dto.PedidoPecaResponseDTO;
import br.com.mecaniQA.api.dto.PedidoPecaStatusDTO;
import br.com.mecaniQA.api.mapper.PedidoPecaMapper;
import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.model.StatusPedido;
import br.com.mecaniQA.api.repository.OrdemServicoRepository;
import br.com.mecaniQA.api.repository.PecaRepository;
import br.com.mecaniQA.api.repository.PedidoPecasRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidos-pecas")
public class PedidoPecaController {

    private final PedidoPecasRepository repository = PedidoPecasRepository.getInstance();
    private final OrdemServicoRepository ordemServicoRepository = OrdemServicoRepository.getInstance();
    private final PecaRepository pecaRepository = PecaRepository.getInstance();

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody PedidoPecaRequestDTO dto) {
        if (dto == null || dto.getOrdemServicoId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("O ID da Ordem de Serviço é obrigatório para criar um pedido.");
        }

        if (ordemServicoRepository.buscarPorId(dto.getOrdemServicoId()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ordem de Serviço com ID " + dto.getOrdemServicoId() + " não foi encontrada.");
        }

        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("O pedido precisa conter pelo menos um item de peça.");
        }

        PedidoPecas pedido = PedidoPecaMapper.toEntity(dto);
        PedidoPecas salvo = repository.salvar(pedido);

        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoPecaMapper.toDTO(salvo));
    }

    @PostMapping("/{id}/itens")
    public ResponseEntity<?> adicionarItens(
            @PathVariable Long id,
            @RequestBody List<ItemPedidoDTO> novosItensDTO) {

        Optional<PedidoPecas> pedidoOpt = repository.buscarPorId(id);
        if (pedidoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Pedido de peças com ID " + id + " não encontrado.");
        }

        PedidoPecas pedido = pedidoOpt.get();

        if (pedido.getStatus() != null && !pedido.getStatus().equals(StatusPedido.ORCANDO)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Não é possível adicionar itens a um pedido com status " + pedido.getStatus());
        }

        if (novosItensDTO == null || novosItensDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Informe ao menos um item de peça para adicionar ao pedido.");
        }

        List<ItemPedido> novosItens = new ArrayList<>();
        for (ItemPedidoDTO itemDTO : novosItensDTO) {
            if (itemDTO.getPecaId() == null || itemDTO.getQuantidade() == null || itemDTO.getQuantidade() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Cada item deve conter um ID de peça válido e quantidade maior que zero.");
            }

            Optional<Peca> pecaOpt = pecaRepository.buscarPorId(itemDTO.getPecaId());
            if (pecaOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Peça com ID " + itemDTO.getPecaId() + " não foi encontrada.");
            }

            Peca peca = pecaOpt.get();
            Double preco = (itemDTO.getPrecoUnitario() != null && itemDTO.getPrecoUnitario() > 0)
                    ? itemDTO.getPrecoUnitario()
                    : peca.getPrecoVenda();

            novosItens.add(new ItemPedido(peca, itemDTO.getQuantidade(), preco));
        }

        pedido.adicionarItens(novosItens);
        repository.salvar(pedido);

        return ResponseEntity.ok(PedidoPecaMapper.toDTO(pedido));
    }

    @GetMapping
    public ResponseEntity<List<PedidoPecaResponseDTO>> listar() {
        List<PedidoPecaResponseDTO> response = repository.listarTodos().stream()
                .map(PedidoPecaMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoPecaResponseDTO> buscarPorId(@PathVariable Long id) {
        Optional<PedidoPecas> pedidoOpt = repository.buscarPorId(id);
        return pedidoOpt.map(p -> ResponseEntity.ok(PedidoPecaMapper.toDTO(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable Long id,
            @RequestBody PedidoPecaStatusDTO statusDTO) {

        if (statusDTO == null || statusDTO.getStatus() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("O novo status é obrigatório.");
        }

        Optional<PedidoPecas> pedidoOpt = repository.buscarPorId(id);
        if (pedidoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Pedido de peças com ID " + id + " não encontrado.");
        }

        PedidoPecas pedido = pedidoOpt.get();

        pedido.setStatus(statusDTO.getStatus());
        repository.salvar(pedido);

        return ResponseEntity.ok(PedidoPecaMapper.toDTO(pedido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        boolean removido = repository.deletar(id);
        if (removido) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}