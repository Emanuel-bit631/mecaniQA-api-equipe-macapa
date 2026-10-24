package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.ServicoDTO;
import br.com.mecaniQA.api.mapper.ServicoMapper;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.ServicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {
    private final ServicoRepository servicoRepository = ServicoRepository.getInstance();

    @GetMapping
    public ResponseEntity<List<ServicoDTO>> listarTodos(){
        List<ServicoDTO> dtos = servicoRepository.getServicos().stream()
                .map(ServicoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoDTO> buscarPorId(@PathVariable Long id){
        Optional<Servico> servicoOpt = servicoRepository.buscarPorId(id);
        return servicoOpt.map(servico -> ResponseEntity.ok(ServicoMapper.toDTO(servico)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody ServicoDTO dto){
        Servico servico = ServicoMapper.toEntity(dto);
        boolean salvo = servicoRepository.salvar(servico);
        if(!salvo){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Dados inválidos. Certifique-se de preencher o nome, tempo estimado maior que zero e custo tabelado positivo.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ServicoMapper.toDTO(servico));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody ServicoDTO dto){
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Corpo da requisição não pode ser vazio.");
        }
        Servico servico = ServicoMapper.toEntity(dto);
        boolean atualizado = servicoRepository.atualizar(id, servico);
        if(!atualizado){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Serviço não encontrado para o ID informado: " + id);
        }
        Optional<Servico> servicoAtualizado = servicoRepository.buscarPorId(id);
        return ResponseEntity.ok(ServicoMapper.toDTO(servicoAtualizado.get()));
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