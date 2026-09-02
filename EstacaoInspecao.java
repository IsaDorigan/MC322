public class EstacaoInspecao {

    // Atributos
    private boolean ativa;
    private int produtosInspecionados;

    // Construtor
    public EstacaoInspecao() {
        this.ativa = false;
        this.produtosInspecionados = 0;
    }

    // Ativa a estação
    public void ativar() {
        this.ativa = true;
        System.out.println("[OK] Estação de inspeção ativada.");
    }

    // Desativa a estação
    public void desativar() {
        this.ativa = false;
        System.out.println("[OK] Estação de inspeção desativada.");
    }

    // Inspeciona um produto, e ve se é possível inspecionar devido a estação ou a quantidade
    public void inspecionar(Produto produto) {

        if (!ativa) {
            System.out.println("[ERRO] A estação de inspeção está desativada.");
            return;
        }

        if (produto == null) {
            System.out.println("[ERRO] Não há produto para inspecionar.");
            return;
        }

        produtosInspecionados++;

        System.out.println("[OK] " + produto.getNome() + " aprovado na inspeção!");
    }

    // Retorna a quantidade de produtos inspecionados
    public int getTotalInspecionados() {
        return produtosInspecionados;
    }
}