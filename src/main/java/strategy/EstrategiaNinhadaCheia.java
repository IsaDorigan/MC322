package strategy;
import java.util.List;

import model.Demanda;

/**
 * Ninhada Cheia (máximo de produtos): entre as demandas que cabem no
 * orçamento disponível, escolhe a que entrega mais patinhos no total.
 * 
 * @author Isadora KLuge Dorigan e Guilherme Forte Silva
 */
public class EstrategiaNinhadaCheia implements EstrategiaProducao {

    /**
     * Percorre a lista de demandas e seleciona aquela com a maior quantidade de produtos que cabe no orçamento atual
     *
     * @param demandas            A lista contendo todas as demandas cadastradas no sistema
     * @param orcamentoDisponivel O valor financeiro atualmente disponível no orçamento da fábrica
     * @return                    A demanda selecionada com a maior quantidade ou null caso nenhuma seja viável
     */
    @Override
    public Demanda selecionarDemanda(List<Demanda> demandas, double orcamentoDisponivel) {
        Demanda melhor = null;

        for (Demanda demanda : demandas) {
            // Só considera demandas viáveis financeiramente
            if (!isElegivel(demanda) || !demanda.cabeNoOrcamento(orcamentoDisponivel)) {
                continue;
            }
            if (melhor == null || demanda.getQuantidadeProdutos() > melhor.getQuantidadeProdutos()) {
                melhor = demanda;
            }
        }
        return melhor;
    }

    /**
     * Obtem o nome de identificação desta estratégia de produção
     *
     * @return O nome descritivo da estratégia
     */
    @Override
    public String getNomeEstrategia() {
        return "Ninhada Cheia (Maximizar Producao)";
    }
}