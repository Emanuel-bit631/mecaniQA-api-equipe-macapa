package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.ServicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {
    private final ServicoRepository servicoRepository = ServicoRepository.getInstance();

    @GetMapping
    public ResponseEntity<List<Servico>> listarTodos(){
        return ResponseEntity.ok(servicoRepository.getServicos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servico> buscarPorId(@PathVariable Long id){
        Optional<Servico> servicoOpt = servicoRepository.buscarPorId(id);
        return servicoOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Servico servico){
        boolean salvo = servicoRepository.salvar(servico);
        if(!salvo){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Dados inválidos. Certifique-se de preencher o nome, tempo estimado maior que zero e custo tabelado positivo.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(servico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Servico servico){
        if (servico == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Corpo da requisição não pode ser vazio.");
        }
        boolean atualizado = servicoRepository.atualizar(id, servico);
        if(!atualizado){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Serviço não encontrado para o ID informado: " + id);
        }
        Optional<Servico> servicoAtualizado = servicoRepository.buscarPorId(id);
        return ResponseEntity.ok(servicoAtualizado.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        boolean deletado = servicoRepository.deletar(id);
        if(deletado){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}