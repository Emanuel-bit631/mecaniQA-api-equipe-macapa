package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.PecaRequestDTO;
import br.com.mecaniQA.api.dto.PecaResponseDTO;
import br.com.mecaniQA.api.mapper.PecaMapper;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.repository.PecaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pecas")
public class PecaController {
    private final PecaRepository pecaRepository = PecaRepository.getInstance();

    @GetMapping
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Dados inválidos. Verifique código de barras, fornecedor, estoque, preços e categoria.");
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
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        boolean deletado = pecaRepository.deletar(id);
        if (deletado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}