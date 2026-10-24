
package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.ServicoDTO;
 oat3-jdbc
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.service.ServicoService;

import br.com.mecaniQA.api.mapper.ServicoMapper;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.ServicoRepository;
 main
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
 oat3-jdbc
    public ResponseEntity<List<ServicoDTO>> listarTodos() {
        try {
            return ResponseEntity.ok(servicoService.listarTodos());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoDTO> buscarPorId(
            @PathVariable Long id) {
        try {
            Optional<ServicoDTO> servico =
                    servicoService.buscarPorId(id);

            return servico.map(ResponseEntity::ok)
                    .orElseGet(() ->
                            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                    );
        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestBody ServicoDTO dto) {
        try {
            ServicoDTO salvo = servicoService.cadastrar(dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        } catch (DadosInvalidosException e) {

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
 main
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).build();
        }
 oat3-jdbc
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody ServicoDTO dto) {
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Corpo da requisição não pode ser vazio.");
        }

        try {
            Optional<ServicoDTO> atualizado =
                    servicoService.atualizar(id, dto);

            if (atualizado.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Serviço não encontrado para o ID informado: " + id);
            }

            return ResponseEntity.ok(atualizado.get());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).build();
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
 main
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            if (servicoService.deletar(id)) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).build();
        }
    }
}
