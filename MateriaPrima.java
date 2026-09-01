public class MateriaPrima {

    // informações de cada lote 
    private int id;
    private String nome;
    private double quantidade;
    private String unidade;
    private double quantidadeMinima;

    // CONSTRUTOR
    public MateriaPrima(int id, String nome, double quantidade, String unidade, double quantidadeMinima) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.quantidadeMinima = quantidadeMinima;
    }

    // Reduz o estoque quando a borracha é usada para moldar um pato
    public void consumir(double quantidadeDemandada) {
        if (quantidadeDemandada <= this.quantidade) {
            this.quantidade -= quantidadeDemandada;
        } else {
            System.out.println("Ops! Não há borracha suficiente para moldar mais patinhos. Estoque atual de "
                    + this.nome + ": " + this.quantidade + this.unidade);
        }
    }

    // Adiciona borracha ao estoque (chegou um novo carregamento do fornecedor)
    public void adicionarEstoque(double quantidadeAdicionada) {
        this.quantidade += quantidadeAdicionada;
        System.out.println("Novo lote de " + this.nome + " recebido! Estoque atualizado.");
    }

    // Verifica se há borracha suficiente para moldar a quantidade de patinhos pedida
    public boolean verificarDisponibilidade(double demanda) {
        return this.quantidade >= demanda;
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
}