import java.util.List;

/**
 * Implementa a estratégia Revoada Gigante (maior demanda): prioriza o pedido com mais patinhos
 * a fabricar, sem se preocupar se o orçamento aguenta.
 * 
 * EstrategiaRevoadaGigante
 */
public class EstrategiaRevoadaGigante implements EstrategiaProducao {

    /**
     * Percorre a lista de demandas e seleciona aquela que possui o maior volume de produtos a serem fabricados mantendo a ordem de chegada em caso de empate
     *
     * @param demandas            A lista contendo todas as demandas cadastradas no sistema
     * @param orcamentoDisponivel O valor financeiro atualmente em caixa (não atua como restrição nesta estratégia)
     * @return                    A demanda com a maior quantidade pendente ou null caso não haja demandas elegíveis
     */
    @Override
    public Demanda selecionarDemanda(List<Demanda> demandas, double orcamentoDisponivel) {
        Demanda maior = null;

        for (Demanda demanda : demandas) {
            if (!isElegivel(demanda)) {
                continue;
            }
            // Em caso de empate, mantém a demanda que chegou primeiro
            if (maior == null || demanda.getQuantidadeProdutos() > maior.getQuantidadeProdutos()) {
                maior = demanda;
            }
        }
        return maior;
    }

    /**
     * Obtem o nome de identificação desta estratégia de produção
     *
     * @return O nome descritivo da estratégia
     */
    @Override
    public String getNomeEstrategia() {
        return "Revoada Gigante (Maior Demanda)";
    }
}
