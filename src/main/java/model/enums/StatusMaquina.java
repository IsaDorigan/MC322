package model.enums;
/**
 * Estados possíveis de uma máquina da linha de produção.
 * 
 * @author Isadora KLuge Dorigan e Guilherme Forte Silva
 */
public enum StatusMaquina {

    /** Indica que a máquina está desligada */
    DESLIGADA("Desligada"),
    /** Indica que a máquina está ligada */
    LIGADA("Ligada"),
    /** Indica que a máquina está quebrada */
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
