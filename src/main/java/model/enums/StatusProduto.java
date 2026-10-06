package model.enums;
/**
 * Estados possíveis de um patinho ao longo da linha de produção.
 * 
 * @author Isadora KLuge Dorigan e Guilherme Forte Silva
 */
public enum StatusProduto {

    /** Produto aguardando inicio do processamento */
    AGUARDANDO("Aguardando"),
    /** Produto sendo moldado */
    PROCESSANDO("Moldando"),
    /** Produto que a moldagem foi concluída */
    PROCESSADO("Moldado"),
    /** Produto passando pelo processo de embalagem */
    EMBALANDO("Embalando"),
    /** Produto que terminou de ser embalado */
    EMBALADO("Embalado"),
    /** Produto sendo inspecionado */
    INSPECIONANDO("Inspecionando"),
    /** Produto que passou pelo processo de inspeção */
    INSPECIONADO("Inspecionado"),
    /** Produto que não aprovado no processo de inspeção */
    REJEITADO("Rejeitado na inspecaoo"),
    /** Produto perdido devido a falha na máquina */
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