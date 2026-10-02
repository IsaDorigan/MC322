/**
 * Pedido de patinhos de um tipo. O estado é controlado pelo enum StatusDemanda
 * e as transições são validadas para manter a consistência
 * (ex.: não é possível concluir uma demanda CANCELADA).
 *  
 * Demanda
 */
public class Demanda {

    // ATRIBUTOS
    private String tipoProduto;
    private int quantidadeProdutos;
    private StatusDemanda status;

    // Estimativas preenchidas pelo gerenciador (ele conhece as máquinas e o produto)
    private double consumoUnitarioEstimado;  // kg de matéria-prima por unidade
    private double custoUnitarioEstimado;    // custo de operação por unidade

    /**
    * Construtor padrão para inicializar um novo pedido de produção
    * A demanda recém-criada inicia sempre no estado PENDENTE
    *
    * @param tipoProduto        O tipo de patinho solicitado na demanda
    * @param quantidadeProdutos A quantidade total de patinhos solicitados
    */
    public Demanda(String tipoProduto, int quantidadeProdutos) {
        this.tipoProduto = tipoProduto;
        this.quantidadeProdutos = quantidadeProdutos;
        this.status = StatusDemanda.PENDENTE;
    }

    // ATUALIZAÇÃO PELO USUÁRIO

    /**
     * Define uma nova quantidade para a demanda. Se a quantidade for maior que zero, 
     * o status passa a ser pendente. Se for zero, a demanda é automaticamente cancelada
     *
     * @param quantidade A nova quantidade de produtos desejada
     * @return           true se a quantidade foi atualizada, false caso seja negativa ou a demanda já esteja em produção
     */
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

    /**
     * Altera o estado da demanda de pendente para em produção
     *
     * @return true se a transição de estado foi bem-sucedida, false se a demanda não estava no estado correto
     */
    public boolean iniciarProducao() {
        if (status != StatusDemanda.PENDENTE) {
            System.out.println("[ERRO] Só é possível iniciar uma demanda PENDENTE (estado atual: " + status + ").");
            return false;
        }
        status = StatusDemanda.EM_PRODUCAO;
        return true;
    }

    /**
     * Abate da quantidade da demanda os produtos que já foram fabricados com sucesso
     * Garante que a quantidade pendente não seja inferior a zero
     *
     * @param produzidos A quantidade de patinhos fabricados e finalizados com sucesso
     */
    public void registrarProduzidos(int produzidos) {
        quantidadeProdutos = Math.max(0, quantidadeProdutos - produzidos);
    }

    /**
     * Altera o estado da demanda para concluida. Esta transição só é permitida se a 
     * demanda estiver em produção e não houver mais nenhum produto pendente.
     *
     * @return true se a demanda foi concluída com sucesso, false caso contrário.
     */
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

    /**
     * Retorna uma demanda que estava em produção para o estado pendente.
     * Utilizado quando ocorre uma produção parcial e o volume restante precisa voltar à fila de espera
     *
     * @return true se a demanda retornou para a fila com sucesso, false caso não estivesse em produção
     */
    public boolean devolverParaFila() {
        if (status != StatusDemanda.EM_PRODUCAO) {
            System.out.println("[ERRO] Só uma demanda EM_PRODUCAO pode voltar para a fila.");
            return false;
        }
        status = StatusDemanda.PENDENTE;
        return true;
    }

    /**
     * Altera o estado da demanda para cancelada, interrompendo sua execução
     *
     * @return true se o cancelamento foi realizado, false se a demanda já estava concluída ou previamente cancelada
     */
    public boolean cancelar() {
        if (status == StatusDemanda.CONCLUIDA || status == StatusDemanda.CANCELADA) {
            System.out.println("[ERRO] Não é possível cancelar uma demanda no estado " + status + ".");
            return false;
        }
        status = StatusDemanda.CANCELADA;
        return true;
    }

    // CONSULTAS DE CONSUMO E VIABILIDADE

    /**
     * Calcula a quantidade exata de matéria-prima exigida para suprir a quantidade
     * de produtos restantes nesta demanda.
     *
     * @param produto O tipo de produto que servirá de base para calcular o consumo
     * @return        O total de matéria-prima necessária
     */
    public double calcularMateriaPrimaNecessaria(Produto produto) {
        return quantidadeProdutos * produto.getQuantidadeMateriaPrimaPorUnidade();
    }

    /**
     * Atualiza os valores estimados de consumo e custo baseados nas máquinas e no perfil do produto
     *
     * @param consumoUnitario O consumo estimado de matéria-prima por cada unidade
     * @param custoUnitario   O custo de operação estimado por cada unidade
     */
    public void atualizarEstimativas(double consumoUnitario, double custoUnitario) {
        this.consumoUnitarioEstimado = consumoUnitario;
        this.custoUnitarioEstimado = custoUnitario;
    }

    /**
     * Calcula o consumo total estimado de matéria-prima para a demanda atual
     *
     * @return O consumo total estimado multiplicado pela quantidade de produtos
     */
    public double calcularConsumoEstimado() {
        return quantidadeProdutos * consumoUnitarioEstimado;
    }

    /**
     * Calcula o custo total estimado de operação para finalizar a demanda atual
     *
     * @return O custo total estimado multiplicado pela quantidade de produtos
     */
    public double calcularCustoEstimado() {
        return quantidadeProdutos * custoUnitarioEstimado;
    }


    /**
     * Verifica se o custo estimado de operação da demanda atual não ultrapassa 
     * o orçamento disponível no caixa da fábrica.
     *
     * @param orcamentoDisponivel O valor financeiro atualmente disponível no orçamento
     * @return                    true se a demanda couber financeiramente no orçamento, false caso contrário
     */
    public boolean cabeNoOrcamento(double orcamentoDisponivel) {
        return calcularCustoEstimado() <= orcamentoDisponivel;
    }

    // GETTERS

    /**
     * Obtém o tipo de produto (patinho) solicitado pela demanda
     *
     * @return Uma string contendo o tipo do produto
     */
    public String getTipoProduto() {
        return tipoProduto;
    }

    /**
     * Obtém a quantidade de produtos que ainda faltam ser fabricados nesta demanda
     *
     * @return O número de produtos pendentes ou em produção
     */
    public int getQuantidadeProdutos() {
        return quantidadeProdutos;
    }

    /**
     * Obtém o status atual do pedido de produção
     *
     * @return O estado atual da demanda
     */
    public StatusDemanda getStatus() {
        return status;
    }

    /**
     * Verifica se a demanda já foi totalmente atendida e finalizada
     *
     * @return true se o status for CONCLUIDA, false caso contrário
     */
    public boolean isAtendida() {
        return status == StatusDemanda.CONCLUIDA;
    }
}