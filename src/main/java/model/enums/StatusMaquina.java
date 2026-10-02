package model.enums;
/**
 * Estados possíveis de uma máquina da linha de produção.
 */
public enum StatusMaquina {

    DESLIGADA("Desligada"),
    LIGADA("Ligada"),
    QUEBRADA("Quebrada");

    private final String descricao;

    /**
     * Construtor do status da maquina que define sua descrição para exibição
     *
     * @param descricao O texto formatado detalhando o significado de cada status
     */
    StatusMaquina(String descricao) {
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
