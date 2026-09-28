package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.dto.ItemPedidoDTO;
import br.com.mecaniQA.api.dto.PedidoPecaRequestDTO;
import br.com.mecaniQA.api.dto.PedidoPecaResponseDTO;
import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.model.StatusPedido;
import br.com.mecaniQA.api.repository.PecaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoPecaMapper {

    private static final PecaRepository pecaRepository = PecaRepository.getInstance();

    public static PedidoPecas toEntity(PedidoPecaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        List<ItemPedido> itens = new ArrayList<>();
        if (dto.getItens() != null) {
            for (ItemPedidoDTO itemDTO : dto.getItens()) {
                if (itemDTO.getPecaId() != null) {
                    Optional<Peca> pecaOpt = pecaRepository.buscarPorId(itemDTO.getPecaId());
                    if (pecaOpt.isPresent()) {
                        Peca peca = pecaOpt.get();
                        Double preco = (itemDTO.getPrecoUnitario() != null && itemDTO.getPrecoUnitario() > 0)
                                ? itemDTO.getPrecoUnitario()
                                : peca.getPrecoVenda();
                        itens.add(new ItemPedido(peca, itemDTO.getQuantidade(), preco));
                    }
                }
            }
        }

        return new PedidoPecas(null, LocalDateTime.now(), StatusPedido.ORCANDO, itens);
    }

    public static PedidoPecaResponseDTO toDTO(PedidoPecas entity) {
        if (entity == null) {
            return null;
        }

        List<ItemPedidoDTO> itensDTO = new ArrayList<>();
        if (entity.getItens() != null) {
            for (ItemPedido item : entity.getItens()) {
                Long pecaId = item.getPeca() != null ? item.getPeca().getId() : null;
                itensDTO.add(new ItemPedidoDTO(pecaId, item.getQuantidade(), item.getPrecoUnitario(), item.getSubtotal()));
            }
        }

        return new PedidoPecaResponseDTO(
                entity.getId(),
                entity.getDataPedido(),
                entity.getStatus(),
                itensDTO,
                entity.getValorTotal()
        );
    }
}
