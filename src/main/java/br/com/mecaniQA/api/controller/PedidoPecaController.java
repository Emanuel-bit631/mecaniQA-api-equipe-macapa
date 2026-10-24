package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.ItemPedidoDTO;
import br.com.mecaniQA.api.dto.PedidoPecaRequestDTO;
import br.com.mecaniQA.api.dto.PedidoPecaResponseDTO;
import br.com.mecaniQA.api.dto.PedidoPecaStatusDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.RecursoNaoEncontradoException;
import br.com.mecaniQA.api.model.StatusPedido;
import br.com.mecaniQA.api.service.PedidoPecasService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos-pecas")
public class PedidoPecaController {

    private final PedidoPecasService pedidoPecasService;

    public PedidoPecaController(
            PedidoPecasService pedidoPecasService) {
        this.pedidoPecasService = pedidoPecasService;
    }

    @PostMapping
    public ResponseEntity<?> criar(
            @RequestBody PedidoPecaRequestDTO dto) {
        try {
            PedidoPecaResponseDTO salvo =
                    pedidoPecasService.cadastrar(dto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(salvo);

        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cadastrar pedido de peças no MySQL.");
        }
    }

    @PostMapping("/{id}/itens")
    public ResponseEntity<?> adicionarItens(
            @PathVariable Long id,
            @RequestBody List<ItemPedidoDTO> novosItensDTO) {
        try {
            return ResponseEntity.ok(
                    pedidoPecasService.adicionarItens(id, novosItensDTO)
            );

        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao adicionar itens ao pedido de peças.");
        }
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<PedidoPecaResponseDTO> pedidos =
                    pedidoPecasService.listar();

            return ResponseEntity.ok(pedidos);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar pedidos de peças no MySQL.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(pedidoPecasService.buscarPorId(id));

        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();

        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar pedido de peças no MySQL.");
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable Long id,
            @RequestBody PedidoPecaStatusDTO statusDTO) {
        try {
            StatusPedido status = statusDTO == null
                    ? null
                    : statusDTO.getStatus();

            return ResponseEntity.ok(
                    pedidoPecasService.atualizarStatus(id, status)
            );

        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar status do pedido de peças.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            pedidoPecasService.deletar(id);
            return ResponseEntity.noContent().build();

        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();

        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir pedido de peças no MySQL.");
        }
    }
}