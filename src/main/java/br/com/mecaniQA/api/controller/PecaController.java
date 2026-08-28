package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.repository.PecaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pecas")
public class PecaController {
    private final PecaRepository pecaRepository = PecaRepository.getInstance();

    @GetMapping
    public ResponseEntity<List<Peca>> listarTodas(@RequestParam(required = false) String codigoBarras){
        if(codigoBarras != null && !codigoBarras.trim().isEmpty()){
            Optional<Peca> pecaOpt = pecaRepository.buscarPorCodigoBarras(codigoBarras);
            return pecaOpt.map(peca -> ResponseEntity.ok(List.of(peca)))
                    .orElseGet(() -> ResponseEntity.ok(List.of()));
        }
        return ResponseEntity.ok(pecaRepository.getPecas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Peca> buscarPorId(@PathVariable Long id){
        Optional<Peca> pecaOpt = pecaRepository.buscarPorId(id);
        return pecaOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Peca peca){
        boolean salvo = pecaRepository.salvar(peca);
        if(!salvo){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Dados inválidos. Verifique código de barras, fornecedor, estoque, preços e categoria.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(peca);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Peca peca) {
        if (peca == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Corpo da requisição não pode ser vazio.");
        }

        boolean atualizado = pecaRepository.atualizar(id, peca);
        if (!atualizado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Peça não encontrada para o id informado: " + id);
        }

        Optional<Peca> pecaAtualizada = pecaRepository.buscarPorId(id);
        return ResponseEntity.ok(pecaAtualizada.get());
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