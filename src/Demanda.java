public class Demanda {

    // ATRIBUTOS
    private String tipoProduto;
    private int quantidadeProdutos;
    private boolean atendida;

    // CONSTRUTOR
    public Demanda(String tipoProduto, int quantidadeProdutos) {
        this.tipoProduto = tipoProduto;
        this.quantidadeProdutos = quantidadeProdutos;
        this.atendida = false;
    }


    // MÉTODOS

    public void atualizarQuantidade(int quantidade) {
        if (quantidade < 0) {
            System.out.println("[ERRO] A quantidade não pode ser negativa.");
            return;
        }

        this.quantidadeProdutos = quantidade;

        if (quantidade == 0) {
            this.atendida = true;
        } 
        else {
            this.atendida = false;
        }
    }

    public double calcularMateriaPrimaNecessaria(Produto produto) {
        // A quantidade de matéria prima necessária, é a quantidade em unidades de produto vezes a matéria de cada produto
        return quantidadeProdutos * produto.getQuantidadeMateriaPrimaPorUnidade();
    }

    public void atender() { // Essa função marca a demanda como atendida
        atendida = true;
        quantidadeProdutos = 0;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public int getQuantidadeProdutos() {
        return quantidadeProdutos;
    }

    public boolean isAtendida() {
        return atendida;
    }
}