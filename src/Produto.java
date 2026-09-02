public class Produto {

    // ATRIBUTOS
    private int id;
    private String nome;
    private String status;                           // ex: "aguardando", "processado", "inspecionado"
    private double quantidadeMateriaPrimaNecessaria; // quanto de borracha esse pato precisa

    // CONSTRUTOR
    public Produto(int id, String nome, double quantidadeMateriaPrimaNecessaria) {
        this.id = id;
        this.nome = nome;
        this.quantidadeMateriaPrimaNecessaria = quantidadeMateriaPrimaNecessaria;
        this.status = "aguardando";  // todo pato começa "aguardando" ser fabricado
    }

    //MÉTODOS

    // Marca produto como processado
    public void processado() {
        this.status = "processado";
        System.out.println("[OK] " + this.nome + " acabou de sair do molde!");
    }

    // Define quanto de matéria-prima esse produto precisa
    public void definirDemandaMateriaPrima(double quantidade) {
        this.quantidadeMateriaPrimaNecessaria = quantidade;
    }

    // GETTERS

    public double getDemandaMateriaPrima() {
        return this.quantidadeMateriaPrimaNecessaria;
    }

    public int getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public String getStatus() {
        return this.status;
    }
}