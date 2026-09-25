/**
 * Estados possíveis de uma demanda de patinhos.
 * Usar um enum evita erros de digitação e comparações com String.
 */
public enum StatusDemanda {

    PENDENTE("Pendente - aguardando na fila da lagoa"),
    EM_PRODUCAO("Em produção - patinhos no molde"),
    CONCLUIDA("Concluída - revoada entregue"),
    CANCELADA("Cancelada - sem orçamento, sem insumos ou pedido zerado");

    private final String descricao;

    StatusDemanda(String descricao) {
        this.descricao = descricao;
    }

    // Descrição formatada para o menu e para os relatórios
    public String getDescricao() {
        return descricao;
    }
}
