package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.builder.OrdemServicoBuilder;
import br.com.mecaniQA.api.dto.ItemPedidoDTO;
import br.com.mecaniQA.api.dto.OrdemServicoRequestDTO;
import br.com.mecaniQA.api.dto.OrdemServicoResponseDTO;
import br.com.mecaniQA.api.dto.ServicoDTO;
import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.PecaRepository;
import br.com.mecaniQA.api.repository.ServicoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdemServicoMapper {

    private static final PecaRepository pecaRepository = PecaRepository.getInstance();
    private static final ServicoRepository servicoRepository = ServicoRepository.getInstance();

    public static OrdemServico toEntity(OrdemServicoRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        List<Servico> servicos = new ArrayList<>();
        if (dto.getServicosIds() != null) {
            for (Long servicoId : dto.getServicosIds()) {
                Optional<Servico> servicoOpt = servicoRepository.buscarPorId(servicoId);
                servicoOpt.ifPresent(servicos::add);
            }
        }

        List<ItemPedido> itensPeca = new ArrayList<>();
        if (dto.getItensPeca() != null) {
            for (ItemPedidoDTO itemDTO : dto.getItensPeca()) {
                if (itemDTO.getPecaId() != null) {
                    Optional<Peca> pecaOpt = pecaRepository.buscarPorId(itemDTO.getPecaId());
                    if (pecaOpt.isPresent()) {
                        Peca peca = pecaOpt.get();
                        Double preco = (itemDTO.getPrecoUnitario() != null && itemDTO.getPrecoUnitario() > 0)
                                ? itemDTO.getPrecoUnitario()
                                : peca.getPrecoVenda();
                        itensPeca.add(new ItemPedido(peca, itemDTO.getQuantidade(), preco));
                    }
                }
            }
        }

        return new OrdemServicoBuilder()
                .comCliente(dto.getCliente())
                .comVeiculo(dto.getVeiculo())
                .comStatus(dto.getStatus())
                .comServicos(servicos)
                .comItensPeca(itensPeca)
                .build();
    }

    public static OrdemServicoResponseDTO toDTO(OrdemServico entity) {
        if (entity == null) {
            return null;
        }

        List<ServicoDTO> servicosDTO = new ArrayList<>();
        if (entity.getServicos() != null) {
            for (Servico s : entity.getServicos()) {
                servicosDTO.add(new ServicoDTO(s.getId(), s.getNome(), s.getTempoEstimadoMinutos(), s.getCustoTabelado()));
            }
        }

        List<ItemPedidoDTO> itensDTO = new ArrayList<>();
        if (entity.getItensPeca() != null) {
            for (ItemPedido item : entity.getItensPeca()) {
                Long pecaId = item.getPeca() != null ? item.getPeca().getId() : null;
                itensDTO.add(new ItemPedidoDTO(pecaId, item.getQuantidade(), item.getPrecoUnitario(), item.getSubtotal()));
            }
        }

        return new OrdemServicoResponseDTO(
                entity.getId(),
                entity.getCliente(),
                entity.getVeiculo(),
                entity.getStatus(),
                servicosDTO,
                itensDTO,
                entity.getValorTotal()
        );
    }
}