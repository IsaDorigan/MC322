import java.util.List;

/**
 * Padrão Strategy: cada implementação é um algoritmo diferente para
 * escolher qual demanda a fábrica deve produzir a seguir.
 * As estratégias são "puras": apenas decidem, nunca alteram orçamento ou estoque.
 */
public interface EstrategiaProducao {

    // Retorna a melhor demanda a produzir, ou null se nenhuma for elegível
    Demanda selecionarDemanda(List<Demanda> demandas, double orcamentoDisponivel);

    // Nome amigável exibido no menu
    String getNomeEstrategia();

    // Filtro comum (DRY): só demandas PENDENTES e com itens a fabricar são elegíveis
    default boolean isElegivel(Demanda demanda) {
        return demanda != null
                && demanda.getStatus() == StatusDemanda.PENDENTE
                && demanda.getQuantidadeProdutos() > 0;
    }
}
