package model.enums;
/**
 * Estados possíveis de um patinho ao longo da linha de produção.
 * 
 * @author Isadora KLuge Dorigan e Guilherme Forte Silva
 */
public enum StatusProduto {

    AGUARDANDO("Aguardando"),
    PROCESSANDO("Moldando"),
    PROCESSADO("Moldado"),
    EMBALANDO("Embalando"),
    EMBALADO("Embalado"),
    INSPECIONANDO("Inspecionando"),
    INSPECIONADO("Inspecionado"),
    REJEITADO("Rejeitado na inspecaoo"),
    PERDIDO("Perdido por falha de maquina");

    private final String descricao;

    /**
     * Construtor do status do produto que define sua descrição para exibição
     *
     * @param descricao O texto formatado detalhando o significado de cada status
     */
    StatusProduto(String descricao) {
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