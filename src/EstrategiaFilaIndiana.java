import java.util.List;

/**
 * Fila Indiana (ordem de chegada): os patinhos andam em fila, então
 * a primeira demanda cadastrada que ainda está PENDENTE sai na frente (FIFO).
 */
public class EstrategiaFilaIndiana implements EstrategiaProducao {

    @Override
    public Demanda selecionarDemanda(List<Demanda> demandas, double orcamentoDisponivel) {
        for (Demanda demanda : demandas) {
            if (isElegivel(demanda)) {
                return demanda;
            }
        }
        return null;
    }

    @Override
    public String getNomeEstrategia() {
        return "Fila Indiana (Ordem de Chegada)";
    }
}
