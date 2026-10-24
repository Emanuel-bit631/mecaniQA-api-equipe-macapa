package br.com.mecaniQA.api.service;

import br.com.mecaniQA.api.dto.ItemPedidoDTO;
import br.com.mecaniQA.api.dto.OrdemServicoRequestDTO;
import br.com.mecaniQA.api.dto.OrdemServicoResponseDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.RecursoNaoEncontradoException;
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

    public OrdemServicoResponseDTO buscarPorId(Long id) {
        return ordemServicoRepository.findById(id)
                .map(OrdemServicoMapper::toDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ordem de serviço não encontrada: " + id
                ));
    }

    public OrdemServicoResponseDTO cadastrar(
            OrdemServicoRequestDTO dto) {

        OrdemServico ordem = paraEntidade(dto);

        OrdemServico salva = ordemServicoRepository.save(ordem);

        return OrdemServicoMapper.toDTO(salva);
    }

    public OrdemServicoResponseDTO atualizar(
            Long id, OrdemServicoRequestDTO dto) {

        ordemServicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ordem de serviço não encontrada: " + id
                ));

        OrdemServico atualizada = paraEntidade(dto);
        atualizada.setId(id);

        OrdemServico salva =
                ordemServicoRepository.update(atualizada);

        return OrdemServicoMapper.toDTO(salva);
    }

    public OrdemServicoResponseDTO atualizarStatus(
            Long id, StatusOrdemServico status) {

        if (status == null) {
            throw new DadosInvalidosException(
                    "O status da ordem de serviço é obrigatório."
            );
        }

        OrdemServico ordem = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ordem de serviço não encontrada: " + id
                ));

        ordem.setStatus(status);

        OrdemServico atualizada =
                ordemServicoRepository.update(ordem);

        return OrdemServicoMapper.toDTO(atualizada);
    }

    public void deletar(Long id) {
        if (!ordemServicoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException(
                    "Ordem de serviço não encontrada: " + id
            );
        }

        ordemServicoRepository.deleteById(id);
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

                if (servicoId == null) {
                    throw new DadosInvalidosException(
                            "O ID do serviço é obrigatório."
                    );
                }

                Servico servico = servicoRepository
                        .findById(servicoId)
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
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
                                new RecursoNaoEncontradoException(
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