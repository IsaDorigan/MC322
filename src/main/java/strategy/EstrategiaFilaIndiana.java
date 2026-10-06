package strategy;
import java.util.List;

import model.Demanda;

/**
 * Fila Indiana (ordem de chegada): os patinhos andam em fila, então
 * a primeira demanda cadastrada que ainda está PENDENTE sai na frente (FIFO).
 * 
 * @author Isadora KLuge Dorigan e Guilherme Forte Silva
 */
public class EstrategiaFilaIndiana implements EstrategiaProducao {

    /**
     * Percorre a lista e seleciona a primeira demanda que atenda aos critérios de elegibilidade, seguindo a ordem de chegada
     *
     * @param demandas            A lista contendo todas as demandas cadastradas no sistema
     * @param orcamentoDisponivel O valor financeiro disponível no orçamento da fábrica
     * @return                    A primeira demanda elegível encontrada ou null caso nenhuma possa ser atendida
     */
    @Override
    public Demanda selecionarDemanda(List<Demanda> demandas, double orcamentoDisponivel) {
        for (Demanda demanda : demandas) {
            if (isElegivel(demanda)) {
                return demanda;
            }
        }
        return null;
    }

    /**
     * Obtem o nome de identificação da estratégia de produção
     *
     * @return O nome descritivo da estratégia
     */
    @Override
    public String getNomeEstrategia() {
        return "Fila Indiana (Ordem de Chegada)";
    }
}
