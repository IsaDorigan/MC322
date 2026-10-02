package strategy;
import java.util.List;

import model.Demanda;
import model.enums.StatusDemanda;

/**
 * Padrão Strategy: cada implementação é um algoritmo diferente para
 * escolher qual demanda a fábrica deve produzir a seguir.
 * As estratégias são "puras": apenas decidem, nunca alteram orçamento ou estoque.
 */
public interface EstrategiaProducao {

    /**
     * Retorna a melhor demanda a produzir, ou null se nenhuma for elegível
     * 
     * @param demandas            A lista contendo todas as demandas cadastradas e seus estados
     * @param orcamentoDisponivel O valor financeiro atualmente em caixa para verificar a viabilidade da demanda
     * @return                    A demanda escolhida para produção ou null caso nenhuma seja elegível
     */
    Demanda selecionarDemanda(List<Demanda> demandas, double orcamentoDisponivel);

   /**
     * Obtem o nome formatado e amigável da estratégia para exibição nos menus do sistema
     *
     * @return O nome descritivo da estratégia atual
     */
    String getNomeEstrategia();

    /**
     * Verifica as condições de elegibilidade de uma demanda atuando como um filtro comum para as diferentes estratégias
     *
     * @param demanda A demanda que será submetida à validação
     * @return        true se a demanda estiver pendente e com quantidade maior que zero, false caso contrário
     */
    default boolean isElegivel(Demanda demanda) {
        return demanda != null
                && demanda.getStatus() == StatusDemanda.PENDENTE
                && demanda.getQuantidadeProdutos() > 0;
    }
}
