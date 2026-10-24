package br.com.mecaniQA.api.service;

import br.com.mecaniQA.api.dto.ItemPedidoDTO;
import br.com.mecaniQA.api.dto.OrdemServicoRequestDTO;
import br.com.mecaniQA.api.dto.OrdemServicoResponseDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.mapper.OrdemServicoMapper;
import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.model.StatusOrdemServico;
import br.com.mecaniQA.api.repository.OrdemServicoRepository;
import br.com.mecaniQA.api.repository.PecaRepository;
import br.com.mecaniQA.api.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository =
            OrdemServicoRepository.getInstance();

    private final ServicoRepository servicoRepository =
            ServicoRepository.getInstance();

    private final PecaRepository pecaRepository =
            PecaRepository.getInstance();

    public List<OrdemServicoResponseDTO> listar() {
        return ordemServicoRepository.findAll()
                .stream()
                .map(OrdemServicoMapper::toDTO)
                .toList();
    }

    public Optional<OrdemServicoResponseDTO> buscarPorId(Long id) {
        return ordemServicoRepository.findById(id)
                .map(OrdemServicoMapper::toDTO);
    }

    public OrdemServicoResponseDTO cadastrar(
            OrdemServicoRequestDTO dto) {

        OrdemServico ordem = paraEntidade(dto);

        OrdemServico salva = ordemServicoRepository.save(ordem);

        return OrdemServicoMapper.toDTO(salva);
    }

    public Optional<OrdemServicoResponseDTO> atualizar(
            Long id, OrdemServicoRequestDTO dto) {

        Optional<OrdemServico> existente =
                ordemServicoRepository.findById(id);

        if (existente.isEmpty()) {
            return Optional.empty();
        }

        OrdemServico atualizada = paraEntidade(dto);
        atualizada.setId(id);

        OrdemServico salva =
                ordemServicoRepository.update(atualizada);

        return Optional.of(OrdemServicoMapper.toDTO(salva));
    }

    public Optional<OrdemServicoResponseDTO> atualizarStatus(
            Long id, StatusOrdemServico status) {

        if (status == null) {
            throw new DadosInvalidosException(
                    "O status da ordem de serviço é obrigatório."
            );
        }

        Optional<OrdemServico> ordemOpt =
                ordemServicoRepository.findById(id);

        if (ordemOpt.isEmpty()) {
            return Optional.empty();
        }

        OrdemServico ordem = ordemOpt.get();
        ordem.setStatus(status);

        OrdemServico atualizada =
                ordemServicoRepository.update(ordem);

        return Optional.of(OrdemServicoMapper.toDTO(atualizada));
    }

    public boolean deletar(Long id) {
        if (!ordemServicoRepository.existsById(id)) {
            return false;
        }

        ordemServicoRepository.deleteById(id);

        return true;
    }

    private OrdemServico paraEntidade(
            OrdemServicoRequestDTO dto) {

        if (dto == null) {
            throw new DadosInvalidosException(
                    "Os dados da ordem de serviço não foram informados."
            );
        }

        if (dto.getCliente() == null
                || dto.getCliente().trim().isEmpty()
                || dto.getVeiculo() == null
                || dto.getVeiculo().trim().isEmpty()) {

            throw new DadosInvalidosException(
                    "Cliente e veículo são obrigatórios."
            );
        }

        OrdemServico ordem = new OrdemServico();

        ordem.setCliente(dto.getCliente());
        ordem.setVeiculo(dto.getVeiculo());
        ordem.setStatus(
                dto.getStatus() == null
                        ? StatusOrdemServico.ABERTO
                        : dto.getStatus()
        );

        List<Servico> servicos = new ArrayList<>();

        if (dto.getServicosIds() != null) {
            for (Long servicoId : dto.getServicosIds()) {

                Servico servico = servicoRepository
                        .findById(servicoId)
                        .orElseThrow(() ->
                                new DadosInvalidosException(
                                        "Serviço não encontrado: "
                                                + servicoId
                                )
                        );

                servicos.add(servico);
            }
        }

        ordem.setServicos(servicos);

        List<ItemPedido> itensPeca = new ArrayList<>();

        if (dto.getItensPeca() != null) {
            for (ItemPedidoDTO itemDTO : dto.getItensPeca()) {

                if (itemDTO == null
                        || itemDTO.getPecaId() == null
                        || itemDTO.getQuantidade() == null
                        || itemDTO.getQuantidade() <= 0) {

                    throw new DadosInvalidosException(
                            "Cada item deve informar uma peça e "
                                    + "uma quantidade maior que zero."
                    );
                }

                Peca peca = pecaRepository
                        .findById(itemDTO.getPecaId())
                        .orElseThrow(() ->
                                new DadosInvalidosException(
                                        "Peça não encontrada: "
                                                + itemDTO.getPecaId()
                                )
                        );

                Double preco = itemDTO.getPrecoUnitario();

                if (preco == null || preco <= 0) {
                    preco = peca.getPrecoVenda();
                }

                if (preco == null || preco <= 0) {
                    throw new DadosInvalidosException(
                            "O preço unitário da peça é inválido."
                    );
                }

                itensPeca.add(
                        new ItemPedido(
                                peca,
                                itemDTO.getQuantidade(),
                                preco
                        )
                );
            }
        }

        ordem.setItensPeca(itensPeca);

        return ordem;
    }
}