/**
 * Estados possíveis de um patinho ao longo da linha de produção.
 */
public enum StatusProduto {

    AGUARDANDO("Aguardando"),
    PROCESSANDO("Moldando"),
    PROCESSADO("Moldado"),
    EMBALANDO("Embalando"),
    EMBALADO("Embalado"),
    INSPECIONANDO("Inspecionando"),
    INSPECIONADO("Inspecionado"),
    REJEITADO("Rejeitado na inspeção"),
    PERDIDO("Perdido por falha de máquina");

    private final String descricao;

    StatusProduto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}