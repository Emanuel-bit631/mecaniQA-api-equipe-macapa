package br.com.mecaniQA.api.repository;
import br.com.mecaniQA.api.model.PedidoPecas;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoPecasRepository {
    private static PedidoPecasRepository instance;
    private final List<PedidoPecas> pedidos;
    private Long proximoId;

    private PedidoPecasRepository(){
        this.pedidos = new ArrayList<>();
        this.proximoId = 1L;
    }

    public static synchronized PedidoPecasRepository getInstance(){
        if(instance == null){
            instance = new PedidoPecasRepository();
        }
        return instance;
    }

    public PedidoPecas salvar(PedidoPecas pedido){
        if(pedido.getId() == null){
            pedido.setId(proximoId++);
            pedidos.add(pedido);
        } else {
            for(int i = 0; i < pedidos.size(); i++){
                if(pedidos.get(i).getId().equals(pedido.getId())){
                    pedidos.set(i, pedido);
                    break;
                }
            }
        }
        return pedido;
    }

    public List<PedidoPecas> listarTodos(){
        return new ArrayList<>(pedidos);
    }

    public Optional<PedidoPecas> buscarPorId(Long id){
        return pedidos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public boolean deletar(Long id){
        return pedidos.removeIf(p -> p.getId().equals(id));
    }
}
