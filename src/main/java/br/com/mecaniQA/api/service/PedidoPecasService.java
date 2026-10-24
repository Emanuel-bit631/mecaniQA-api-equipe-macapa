package br.com.mecaniQA.api.service;

import br.com.mecaniQA.api.dto.ItemPedidoDTO;
import br.com.mecaniQA.api.dto.PedidoPecaRequestDTO;
import br.com.mecaniQA.api.dto.PedidoPecaResponseDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.mapper.PedidoPecaMapper;
import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.model.StatusPedido;
import br.com.mecaniQA.api.repository.OrdemServicoRepository;
import br.com.mecaniQA.api.repository.PecaRepository;
import br.com.mecaniQA.api.repository.PedidoPecasRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoPecasService {

    private final PedidoPecasRepository pedidoRepository =
            PedidoPecasRepository.getInstance();

    private final OrdemServicoRepository ordemServicoRepository =
            OrdemServicoRepository.getInstance();

    private final PecaRepository pecaRepository =
            PecaRepository.getInstance();

    public List<PedidoPecaResponseDTO> listar() {
        return pedidoRepository.findAll()
                .stream()
                .map(PedidoPecaMapper::toDTO)
                .toList();
    }

    public Optional<PedidoPecaResponseDTO> buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(PedidoPecaMapper::toDTO);
    }

    public PedidoPecaResponseDTO cadastrar(PedidoPecaRequestDTO dto) {
        if (dto == null || dto.getOrdemServicoId() == null) {
            throw new DadosInvalidosException(
                    "O ID da ordem de serviço é obrigatório."
            );
        }

        if (ordemServicoRepository.findById(dto.getOrdemServicoId()).isEmpty()) {
            throw new DadosInvalidosException(
                    "Ordem de serviço não encontrada: "
                            + dto.getOrdemServicoId()
            );
        }

        List<ItemPedido> itens = criarItens(dto.getItens());

        PedidoPecas pedido = new PedidoPecas();
        pedido.setOrdemServicoId(dto.getOrdemServicoId());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.ORCANDO);
        pedido.setItens(itens);

        return PedidoPecaMapper.toDTO(pedidoRepository.save(pedido));
    }

    public Optional<PedidoPecaResponseDTO> adicionarItens(
            Long id, List<ItemPedidoDTO> novosItensDTO) {

        Optional<PedidoPecas> pedidoOpt = pedidoRepository.findById(id);

        if (pedidoOpt.isEmpty()) {
            return Optional.empty();
        }

        PedidoPecas pedido = pedidoOpt.get();

        if (pedido.getStatus() != StatusPedido.ORCANDO) {
            throw new DadosInvalidosException(
                    "Não é possível adicionar itens a um pedido com status "
                            + pedido.getStatus()
            );
        }

        List<ItemPedido> novosItens = criarItens(novosItensDTO);
        pedido.adicionarItens(novosItens);

        PedidoPecas atualizado = pedidoRepository.update(pedido);

        return Optional.of(PedidoPecaMapper.toDTO(atualizado));
    }

    public Optional<PedidoPecaResponseDTO> atualizarStatus(
            Long id, StatusPedido status) {

        if (status == null) {
            throw new DadosInvalidosException(
                    "O novo status do pedido é obrigatório."
            );
        }

        Optional<PedidoPecas> pedidoOpt = pedidoRepository.findById(id);

        if (pedidoOpt.isEmpty()) {
            return Optional.empty();
        }

        PedidoPecas pedido = pedidoOpt.get();
        pedido.setStatus(status);

        PedidoPecas atualizado = pedidoRepository.update(pedido);

        return Optional.of(PedidoPecaMapper.toDTO(atualizado));
    }

    public boolean deletar(Long id) {
        if (!pedidoRepository.existsById(id)) {
            return false;
        }

        pedidoRepository.deleteById(id);
        return true;
    }

    private List<ItemPedido> criarItens(List<ItemPedidoDTO> itensDTO) {
        if (itensDTO == null || itensDTO.isEmpty()) {
            throw new DadosInvalidosException(
                    "Informe pelo menos um item de peça para o pedido."
            );
        }

        List<ItemPedido> itens = new ArrayList<>();

        for (ItemPedidoDTO itemDTO : itensDTO) {
            if (itemDTO == null
                    || itemDTO.getPecaId() == null
                    || itemDTO.getQuantidade() == null
                    || itemDTO.getQuantidade() <= 0) {

                throw new DadosInvalidosException(
                        "Cada item deve informar uma peça e uma quantidade "
                                + "maior que zero."
                );
            }

            Peca peca = pecaRepository.findById(itemDTO.getPecaId())
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

            itens.add(new ItemPedido(
                    peca,
                    itemDTO.getQuantidade(),
                    preco
            ));
        }

        return itens;
    }
}