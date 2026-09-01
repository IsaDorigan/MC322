public class Produto {

    // ATRIBUTOS
    private int id;
    private String nome;
    private String status;                         // ex: "aguardando", "processado", "inspecionado"
    private double minimomateria; // quanto de borracha esse pato precisa

    // CONSTRUTOR
    public Produto(int id, String nome, double minimomateria) {
        this.id = id;
        this.nome = nome;
        this.minimomateria = minimomateria;
        this.status = "aguardando";  // todo pato começa "aguardando" ser fabricado

    // Fase de processo
        this.status = "processado";
        System.out.println( this.nome + " acabou de sair do molde!");
    }

    // Fase de inspeção
    public void inspecionar() {
        this.status = "inspecionado";
    }

    // Define quanto de matéria-prima esse produto precisa
    public void definirDemandaMateriaPrima(double quantidade) {
        this.minimomateria = quantidade;
    }

    // GETTERS
    public double getDemandaMateriaPrima() {
        return this.minimomateria;
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