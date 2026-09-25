/**
 * Estados possíveis de uma máquina da linha de produção.
 */
public enum StatusMaquina {

    DESLIGADA("Desligada"),
    LIGADA("Ligada"),
    QUEBRADA("Quebrada");

    private final String descricao;

    StatusMaquina(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
