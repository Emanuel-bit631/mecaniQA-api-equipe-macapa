package br.com.mecaniQA.api.repository;
import br.com.mecaniQA.api.model.OrdemServico;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdemServicoRepository {
    private static OrdemServicoRepository instance;
    private final List<OrdemServico> ordensServico;
    private Long proximoId;

    private OrdemServicoRepository() {
        this.ordensServico = new ArrayList<>();
        this.proximoId = 1L;
    }

    public static synchronized OrdemServicoRepository getInstance(){
        if(instance == null){
            instance = new OrdemServicoRepository();
        }
        return instance;
    }

    public OrdemServico salvar(OrdemServico os){
        if(os.getId() == null){
            os.setId(proximoId++);
            ordensServico.add(os);
        } else {
            for(int i = 0; i < ordensServico.size(); i++){
                if(ordensServico.get(i).getId().equals(os.getId())){
                    ordensServico.set(i, os);
                    break;
                }
            }
        }
        return os;
    }

    public List<OrdemServico> listarTodas() {
        return new ArrayList<>(ordensServico);
    }

    public Optional<OrdemServico> buscarPorId(Long id) {
        return ordensServico.stream()
                .filter(os -> os.getId().equals(id))
                .findFirst();
    }

    public boolean deletar(Long id) {
        return ordensServico.removeIf(os -> os.getId().equals(id));
    }
}
