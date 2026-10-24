
package br.com.mecaniQA.api.service;

import br.com.mecaniQA.api.dto.PecaRequestDTO;
import br.com.mecaniQA.api.dto.PecaResponseDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.repository.PecaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PecaService {

    private final PecaRepository pecaRepository =
            PecaRepository.getInstance();

    public List<PecaResponseDTO> listarTodas(String codigoBarras) {
        List<Peca> pecas;

        if (codigoBarras != null && !codigoBarras.trim().isEmpty()) {
            Optional<Peca> encontrada =
                    pecaRepository.buscarPorCodigoBarras(codigoBarras);

            pecas = encontrada.map(List::of).orElseGet(List::of);
        } else {
            pecas = pecaRepository.getPecas();
        }

        return pecas.stream()
                .map(this::paraResposta)
                .toList();
    }

    public Optional<PecaResponseDTO> buscarPorId(Long id) {
        return pecaRepository.buscarPorId(id)
                .map(this::paraResposta);
    }

    public PecaResponseDTO cadastrar(PecaRequestDTO dto) {
        if (dto == null) {
            throw new DadosInvalidosException(
                    "Os dados da peça não foram informados."
            );
        }

        Peca peca = paraEntidade(dto);

        if (!peca.isValida() || peca.getCategoria() == null) {
            throw new DadosInvalidosException(
                    "Dados inválidos para cadastrar a peça."
            );
        }

        if (!pecaRepository.salvar(peca)) {
            throw new DadosInvalidosException(
                    "Não foi possível cadastrar a peça com os dados informados."
            );
        }

        return paraResposta(peca);
    }

    public Optional<PecaResponseDTO> atualizar(
            Long id, PecaRequestDTO dto) {

        if (dto == null) {
            throw new DadosInvalidosException(
                    "Os dados da peça não foram informados."
            );
        }

        Peca peca = paraEntidade(dto);

        if (!pecaRepository.atualizar(id, peca)) {
            return Optional.empty();
        }

        return pecaRepository.buscarPorId(id)
                .map(this::paraResposta);
    }

    public boolean deletar(Long id) {
        return pecaRepository.deletar(id);
    }

    private Peca paraEntidade(PecaRequestDTO dto) {
        Peca peca = new Peca();

        peca.setNome(dto.getNome());
        peca.setCodigoBarras(dto.getCodigoBarras());
        peca.setFornecedorMarca(dto.getFornecedorMarca());
        peca.setQuantidadeEstoque(dto.getQuantidadeEstoque());
        peca.setPrecoCusto(dto.getPrecoCusto());
        peca.setPrecoVenda(dto.getPrecoVenda());
        peca.setCategoria(dto.getCategoria());
        peca.setTamanho(dto.getTamanho());
        peca.setCor(dto.getCor());

        return peca;
    }

    private PecaResponseDTO paraResposta(Peca peca) {
        PecaResponseDTO dto = new PecaResponseDTO();

        dto.setId(peca.getId());
        dto.setNome(peca.getNome());
        dto.setCodigoBarras(peca.getCodigoBarras());
        dto.setFornecedorMarca(peca.getFornecedorMarca());
        dto.setQuantidadeEstoque(peca.getQuantidadeEstoque());
        dto.setPrecoCusto(peca.getPrecoCusto());
        dto.setPrecoVenda(peca.getPrecoVenda());
        dto.setDataCadastro(peca.getDataCadastro());
        dto.setDataUltimaAtualizacao(
                peca.getDataUltimaAtualizacao()
        );
        dto.setCategoria(peca.getCategoria());
        dto.setTamanho(peca.getTamanho());
        dto.setCor(peca.getCor());
        dto.setValida(peca.isValida());

        return dto;
    }
}
