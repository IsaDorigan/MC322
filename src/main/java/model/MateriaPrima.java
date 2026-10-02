package model;
/**
 * Representa a matéria-prima utilizada na fábrica controlando seu estoque, custos e reserva mínima
 */
public class MateriaPrima {

    private static final double TOLERANCIA = 1e-9;  // evita erro de arredondamento com double

    // ATRIBUTOS
    private int id;
    private String nome;
    private double quantidade;
    private String unidade;
    private double custoPorUnidade;
    private double quantidadeMinima;  // reserva de segurança: o estoque nunca pode ficar abaixo dela

    /**
     * Construtor da matéria-prima que inicializa seus dados de identificação, estoque e limites
     *
     * @param id               O identificador único da matéria-prima
     * @param nome             O nome descritivo do insumo
     * @param quantidade       A quantidade inicial disponível no estoque
     * @param unidade          A unidade de medida utilizada para este insumo
     * @param custoPorUnidade  O valor financeiro de cada unidade do insumo
     * @param quantidadeMinima A reserva de segurança que nunca pode ser consumida
     */
    public MateriaPrima(int id, String nome, double quantidade, String unidade,
                        double custoPorUnidade, double quantidadeMinima) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.custoPorUnidade = custoPorUnidade;
        this.quantidadeMinima = quantidadeMinima;
    }

    // MÉTODOS

    /**
     * Reduz a quantidade demandada do estoque atual, bloqueando o consumo se ultrapassar a quantidade mínima estipulada
     *
     * @param quantidadeDemandada A quantidade de matéria-prima que quer utilizar
     * @return                    true se o consumo foi realizado com sucesso, false se foi bloqueado ou a quantidade for inválida
     */
    public boolean consumir(double quantidadeDemandada) {

        if (quantidadeDemandada <= 0) {
            System.out.println("[ERRO] A quantidade consumida deve ser maior que 0.");
            return false;
        }

        if (this.quantidade - quantidadeDemandada < this.quantidadeMinima - TOLERANCIA) {
            System.out.println("[ERRO] Consumo bloqueado: o estoque de " + this.nome
                    + " não pode ficar abaixo da reserva mínima de " + this.quantidadeMinima + " " + this.unidade
                    + ". Estoque atual: " + this.quantidade + " " + this.unidade + ".");
            return false;
        }

        this.quantidade -= quantidadeDemandada;
        return true;
    }

    /**
     * Acrescenta uma nova quantidade ao estoque atual da matéria-prima
     *
     * @param quantidadeAdicionada O volume de materiais recém-adquirido para somar ao estoque
     */
    public void adicionarEstoque(double quantidadeAdicionada) {
        if (quantidadeAdicionada <= 0) {
            System.out.println("[ERRO] A quantidade adicionada deve ser maior que 0.");
            return;
        }

        this.quantidade += quantidadeAdicionada;
        System.out.println("[OK] Novo lote de " + this.nome + " recebido! Estoque atualizado para: "
                + String.format("%.2f", this.quantidade) + " " + this.unidade);
    }

    /**
     * Verifica se há borracha suficiente, respeitando a reserva mínima
     *
     * @param demanda A quantidade de matéria-prima necessária para a operação
     * @return        true se houver disponibilidade suficiente livre da reserva, false caso contrário
     */
    public boolean verificarDisponibilidade(double demanda) {
        return this.quantidade - demanda >= this.quantidadeMinima - TOLERANCIA;
    }

    /**
     * Calcula o volume real de matéria-prima que está liberado para uso, descontando a reserva de segurança do estoque total
     *
     * @return A quantidade disponível para consumo
     */
    public double getQuantidadeDisponivel() {
        return Math.max(0, this.quantidade - this.quantidadeMinima);
    }

    // GETTERS

    /**
     * Obtem o número de identificação desta matéria-prima
     *
     * @return O identificador numérico
     */
    public int getId() {
        return this.id;
    }

    /**
     * Obtem o nome da matéria-prima
     *
     * @return O nome da matéria prima
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Obtem o estoque total armazenado no momento
     *
     * @return A quantidade total em estoque
     */
    public double getQuantidade() {
        return this.quantidade;
    }

    /**
     * Obtem a unidade de medida para quantificar esta matéria-prima
     *
     * @return A unidade de medida
     */
    public String getUnidade() {
        return unidade;
    }

    /**
     * Obtem o custo financeiro para a compra de cada unidade desta matéria-prima
     *
     * @return O preço de compra por unidade
     */
    public double getCustoPorUnidade() {
        return custoPorUnidade;
    }

    /**
     * Obtem o limite da reserva de segurança de quantidade em estoque
     *
     * @return A quantidade mínima que deve ser mantida intocada no estoque
     */
    public double getQuantidadeMinima() {
        return quantidadeMinima;
    }
}