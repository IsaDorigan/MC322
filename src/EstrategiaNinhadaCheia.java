import java.util.List;

/**
 * Ninhada Cheia (máximo de produtos): entre as demandas que cabem no
 * orçamento disponível, escolhe a que entrega mais patinhos no total.
 */
public class EstrategiaNinhadaCheia implements EstrategiaProducao {

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

    @Override
    public String getNomeEstrategia() {
        return "Ninhada Cheia (Maximizar Produção)";
    }
}