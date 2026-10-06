package model.enums;
/**
 * Estados possíveis de uma demanda de patinhos.
 * Usar um enum evita erros de digitação e comparações com String.
 */
public enum StatusDemanda {

    PENDENTE("Pendente - aguardando na fila da lagoa"),
    EM_PRODUCAO("Em producao - patinhos no molde"),
    CONCLUIDA("Concluida - revoada entregue"),
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
