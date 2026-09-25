/**
 * Pedido de patinhos de um tipo. O estado é controlado pelo enum StatusDemanda
 * e as transições são validadas para manter a consistência
 * (ex.: não é possível concluir uma demanda CANCELADA).
 */
public class Demanda {

    // ATRIBUTOS
    private String tipoProduto;
    private int quantidadeProdutos;
    private StatusDemanda status;

    // Estimativas preenchidas pelo gerenciador (ele conhece as máquinas e o produto)
    private double consumoUnitarioEstimado;  // kg de matéria-prima por unidade
    private double custoUnitarioEstimado;    // custo de operação por unidade

    // CONSTRUTOR
    public Demanda(String tipoProduto, int quantidadeProdutos) {
        this.tipoProduto = tipoProduto;
        this.quantidadeProdutos = quantidadeProdutos;
        this.status = StatusDemanda.PENDENTE;
    }

    // ATUALIZAÇÃO PELO USUÁRIO

    // Define uma nova quantidade. Quantidade > 0 deixa a demanda PENDENTE (reabre se estava
    // concluída ou cancelada); quantidade 0 cancela o pedido.
    public boolean atualizarQuantidade(int quantidade) {
        if (quantidade < 0) {
            System.out.println("[ERRO] A quantidade não pode ser negativa.");
            return false;
        }

        if (status == StatusDemanda.EM_PRODUCAO) {
            System.out.println("[ERRO] Não é possível alterar uma demanda que está em produção.");
            return false;
        }

        this.quantidadeProdutos = quantidade;
        this.status = (quantidade == 0) ? StatusDemanda.CANCELADA : StatusDemanda.PENDENTE;
        return true;
    }

    // TRANSIÇÕES DE ESTADO

    // PENDENTE -> EM_PRODUCAO
    public boolean iniciarProducao() {
        if (status != StatusDemanda.PENDENTE) {
            System.out.println("[ERRO] Só é possível iniciar uma demanda PENDENTE (estado atual: " + status + ").");
            return false;
        }
        status = StatusDemanda.EM_PRODUCAO;
        return true;
    }

    // Abate da quantidade os patinhos produzidos com sucesso
    public void registrarProduzidos(int produzidos) {
        quantidadeProdutos = Math.max(0, quantidadeProdutos - produzidos);
    }

    // EM_PRODUCAO -> CONCLUIDA (só se não sobrou nada a produzir)
    public boolean concluir() {
        if (status != StatusDemanda.EM_PRODUCAO) {
            System.out.println("[ERRO] Não é possível concluir uma demanda no estado " + status + ".");
            return false;
        }
        if (quantidadeProdutos > 0) {
            System.out.println("[ERRO] Ainda faltam " + quantidadeProdutos + " unidade(s) para concluir a demanda.");
            return false;
        }
        status = StatusDemanda.CONCLUIDA;
        return true;
    }

    // EM_PRODUCAO -> PENDENTE (produção parcial: o que faltou volta para a fila)
    public boolean devolverParaFila() {
        if (status != StatusDemanda.EM_PRODUCAO) {
            System.out.println("[ERRO] Só uma demanda EM_PRODUCAO pode voltar para a fila.");
            return false;
        }
        status = StatusDemanda.PENDENTE;
        return true;
    }

    // PENDENTE ou EM_PRODUCAO -> CANCELADA
    public boolean cancelar() {
        if (status == StatusDemanda.CONCLUIDA || status == StatusDemanda.CANCELADA) {
            System.out.println("[ERRO] Não é possível cancelar uma demanda no estado " + status + ".");
            return false;
        }
        status = StatusDemanda.CANCELADA;
        return true;
    }

    // CONSULTAS DE CONSUMO E VIABILIDADE

    // A quantidade de matéria prima necessária é a quantidade de produtos vezes o consumo unitário do produto
    public double calcularMateriaPrimaNecessaria(Produto produto) {
        return quantidadeProdutos * produto.getQuantidadeMateriaPrimaPorUnidade();
    }

    public void atualizarEstimativas(double consumoUnitario, double custoUnitario) {
        this.consumoUnitarioEstimado = consumoUnitario;
        this.custoUnitarioEstimado = custoUnitario;
    }

    public double calcularConsumoEstimado() {
        return quantidadeProdutos * consumoUnitarioEstimado;
    }

    public double calcularCustoEstimado() {
        return quantidadeProdutos * custoUnitarioEstimado;
    }

    // Viabilidade financeira: o custo de operação da demanda cabe no orçamento?
    public boolean cabeNoOrcamento(double orcamentoDisponivel) {
        return calcularCustoEstimado() <= orcamentoDisponivel;
    }

    // GETTERS
    public String getTipoProduto() {
        return tipoProduto;
    }

    public int getQuantidadeProdutos() {
        return quantidadeProdutos;
    }

    public StatusDemanda getStatus() {
        return status;
    }

    public boolean isAtendida() {
        return status == StatusDemanda.CONCLUIDA;
    }
}