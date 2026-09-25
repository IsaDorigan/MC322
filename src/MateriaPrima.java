public class MateriaPrima {

    private static final double TOLERANCIA = 1e-9;  // evita erro de arredondamento com double

    // ATRIBUTOS
    private int id;
    private String nome;
    private double quantidade;
    private String unidade;
    private double custoPorUnidade;
    private double quantidadeMinima;  // reserva de segurança: o estoque nunca pode ficar abaixo dela

    // CONSTRUTOR
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

    // Reduz o estoque quando a borracha é usada. BLOQUEIA o consumo se o estoque
    // ficasse abaixo da quantidade mínima. Retorna true se consumiu.
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

    // Adiciona matéria prima ao estoque
    public void adicionarEstoque(double quantidadeAdicionada) {
        if (quantidadeAdicionada <= 0) {
            System.out.println("[ERRO] A quantidade adicionada deve ser maior que 0.");
            return;
        }

        this.quantidade += quantidadeAdicionada;
        System.out.println("[OK] Novo lote de " + this.nome + " recebido! Estoque atualizado para: "
                + String.format("%.2f", this.quantidade) + " " + this.unidade);
    }

    // Verifica se há borracha suficiente, respeitando a reserva mínima
    public boolean verificarDisponibilidade(double demanda) {
        return this.quantidade - demanda >= this.quantidadeMinima - TOLERANCIA;
    }

    // Quanto pode ser usado de fato (estoque menos a reserva mínima)
    public double getQuantidadeDisponivel() {
        return Math.max(0, this.quantidade - this.quantidadeMinima);
    }

    // GETTERS

    public int getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public double getQuantidade() {
        return this.quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public double getCustoPorUnidade() {
        return custoPorUnidade;
    }

    public double getQuantidadeMinima() {
        return quantidadeMinima;
    }
}