package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.OrdemServicoRequestDTO;
import br.com.mecaniQA.api.dto.OrdemServicoResponseDTO;
import br.com.mecaniQA.api.mapper.OrdemServicoMapper;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.StatusOrdemServico;
import br.com.mecaniQA.api.repository.OrdemServicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ordens-servico")

public class OrdemServicoController {
    private final OrdemServicoRepository repository = OrdemServicoRepository.getInstance();

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody OrdemServicoRequestDTO dto){
       if(dto == null || dto.getCliente() == null || dto.getCliente().trim().isEmpty() || dto.getVeiculo() == null || dto.getVeiculo().trim().isEmpty()){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                   .body("Cliente e veiculo são campos obrigatorios para criar uma OS");
       }

       OrdemServico os = OrdemServicoMapper.toEntity(dto);

       if(os.getStatus() == null){
           os.setStatus(StatusOrdemServico.ABERTO);
       }

       OrdemServico salva = repository.salvar(os);

       return ResponseEntity.status(HttpStatus.CREATED).body(OrdemServicoMapper.toDTO(salva));
    }

    @GetMapping
    public ResponseEntity<List<OrdemServicoResponseDTO>> listar() {
        List<OrdemServicoResponseDTO> response = repository.listarTodas().stream()
                .map(OrdemServicoMapper ::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(@PathVariable Long id){
        Optional<OrdemServico> osOpt = repository.buscarPorId(id);
        return osOpt.map(os -> ResponseEntity.ok(OrdemServicoMapper.toDTO(os)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody OrdemServicoRequestDTO dto){
        Optional<OrdemServico> osOpt = repository.buscarPorId(id);
        if(osOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ordem de serviço com id " +id+ " não foi encontrado.");
        }

        OrdemServico osAtualizada = OrdemServicoMapper.toEntity(dto);
        osAtualizada.setId(id);
        repository.salvar(osAtualizada);

        return ResponseEntity.ok(OrdemServicoMapper.toDTO(osAtualizada));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestParam(required = false) StatusOrdemServico status){
        if(status == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("O parâmetro 'status' é obrigatório.");
        }

        Optional<OrdemServico> osOpt = repository.buscarPorId(id);
        if(osOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ordem de serviço com id " +id+ " não foi encontrado.");
        }

        OrdemServico os = osOpt.get();
        os.setStatus(status);
        repository.salvar(os);

        return ResponseEntity.ok(OrdemServicoMapper.toDTO(os));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        boolean removido = repository.deletar(id);
        if(removido){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}