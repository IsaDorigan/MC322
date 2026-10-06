package model.enums;
/**
 * Estados possíveis de uma demanda de patinhos.
 * Usar um enum evita erros de digitação e comparações com String.
 * 
 * @author Isadora KLuge Dorigan e Guilherme Forte Silva
 */
public enum StatusDemanda {

    /** Demanda aguardando para ser selecionada */
    PENDENTE("Pendente - aguardando na fila da lagoa"),
    /** Demdanda que está atualmente em produção */
    EM_PRODUCAO("Em producao - patinhos no molde"),
    /** Demanda cuja produção foi concluída */
    CONCLUIDA("Concluida - revoada entregue"),
    /** Demanda cancelada por algum motivo (orçamento, insumos ou pedido zerado) */
    CANCELADA("Cancelada - sem orçamento, sem insumos ou pedido zerado");

    private final String descricao;

    /**
     * Construtor do status da demanda que define sua descrição para exibição
     *
     * @param descricao O texto formatado detalhando o significado de cada status
     */
    StatusDemanda(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Obtem a descrição detalhada e formatada do status atual para exibição em menus e relatórios
     *
     * @return Uma string contendo a explicação do status
     */
    public String getDescricao() {
        return descricao;
    }
}
