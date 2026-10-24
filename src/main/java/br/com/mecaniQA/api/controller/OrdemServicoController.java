package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.OrdemServicoRequestDTO;
import br.com.mecaniQA.api.dto.OrdemServicoResponseDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.RecursoNaoEncontradoException;
import br.com.mecaniQA.api.model.StatusOrdemServico;
import br.com.mecaniQA.api.service.OrdemServicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(
            OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @PostMapping
    public ResponseEntity<?> criar(
            @RequestBody OrdemServicoRequestDTO dto) {
        try {
            OrdemServicoResponseDTO salva =
                    ordemServicoService.cadastrar(dto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(salva);

        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cadastrar ordem de serviço no MySQL.");
        }
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<OrdemServicoResponseDTO> ordens =
                    ordemServicoService.listar();

            return ResponseEntity.ok(ordens);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar ordens de serviço no MySQL.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(ordemServicoService.buscarPorId(id));

        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();

        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar ordem de serviço no MySQL.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody OrdemServicoRequestDTO dto) {
        try {
            return ResponseEntity.ok(
                    ordemServicoService.atualizar(id, dto)
            );

        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar ordem de serviço no MySQL.");
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable Long id,
            @RequestParam(required = false)
            StatusOrdemServico status) {
        try {
            return ResponseEntity.ok(
                    ordemServicoService.atualizarStatus(id, status)
            );

        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar status da ordem de serviço.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            ordemServicoService.deletar(id);
            return ResponseEntity.noContent().build();

        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();

        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir ordem de serviço no MySQL.");
        }
    }
}