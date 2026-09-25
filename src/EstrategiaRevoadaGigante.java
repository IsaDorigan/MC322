import java.util.List;

/**
 * Revoada Gigante (maior demanda): prioriza o pedido com mais patinhos
 * a fabricar, sem se preocupar se o orçamento aguenta.
 */
public class EstrategiaRevoadaGigante implements EstrategiaProducao {

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

    @Override
    public String getNomeEstrategia() {
        return "Revoada Gigante (Maior Demanda)";
    }
}
