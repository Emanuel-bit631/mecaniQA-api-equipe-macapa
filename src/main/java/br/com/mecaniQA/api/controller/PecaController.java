
package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.PecaRequestDTO;
import br.com.mecaniQA.api.dto.PecaResponseDTO;
 oat3-jdbc
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.service.PecaService;

import br.com.mecaniQA.api.mapper.PecaMapper;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.repository.PecaRepository;
 main
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pecas")
public class PecaController {

    private final PecaService pecaService;

    public PecaController(PecaService pecaService) {
        this.pecaService = pecaService;
    }

    @GetMapping
 oat3-jdbc
    public ResponseEntity<List<PecaResponseDTO>> listarTodas(
            @RequestParam(required = false) String codigoBarras) {
        try {
            return ResponseEntity.ok(
                    pecaService.listarTodas(codigoBarras)
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PecaResponseDTO> buscarPorId(
            @PathVariable Long id) {
        try {
            Optional<PecaResponseDTO> peca =
                    pecaService.buscarPorId(id);

            return peca.map(ResponseEntity::ok)
                    .orElseGet(() ->
                            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                    );
        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestBody PecaRequestDTO dto) {
        try {
            PecaResponseDTO peca = pecaService.cadastrar(dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(peca);
        } catch (DadosInvalidosException e) {

    public ResponseEntity<List<PecaResponseDTO>> listarTodas(@RequestParam(required = false) String codigoBarras){
        if(codigoBarras != null && !codigoBarras.trim().isEmpty()){
            Optional<Peca> pecaOpt = pecaRepository.buscarPorCodigoBarras(codigoBarras);
            List<PecaResponseDTO> dtos = pecaOpt.stream()
                    .map(PecaMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        }
        List<PecaResponseDTO> dtos = pecaRepository.getPecas().stream()
                .map(PecaMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PecaResponseDTO> buscarPorId(@PathVariable Long id){
        Optional<Peca> pecaOpt = pecaRepository.buscarPorId(id);
        return pecaOpt.map(peca -> ResponseEntity.ok(PecaMapper.toDTO(peca)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody PecaRequestDTO dto){
        Peca peca = PecaMapper.toEntity(dto);
        boolean salvo = pecaRepository.salvar(peca);
        if(!salvo){
 main
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().build();
        }
 oat3-jdbc
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody PecaRequestDTO dto) {
        if (dto == null) {
            return ResponseEntity.badRequest()
                    .body("Corpo da requisição não pode ser vazio.");
        }

        try {
            Optional<PecaResponseDTO> atualizada =
                    pecaService.atualizar(id, dto);

            if (atualizada.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Peça não encontrada para o ID informado: " + id);
            }

            return ResponseEntity.ok(atualizada.get());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(PecaMapper.toDTO(peca));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody PecaRequestDTO dto) {
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Corpo da requisição não pode ser vazio.");
        }

        Peca peca = PecaMapper.toEntity(dto);
        boolean atualizado = pecaRepository.atualizar(id, peca);
        if (!atualizado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Peça não encontrada para o id informado: " + id);
        }

        Optional<Peca> pecaAtualizada = pecaRepository.buscarPorId(id);
        return ResponseEntity.ok(PecaMapper.toDTO(pecaAtualizada.get()));
 main
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            if (pecaService.deletar(id)) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
