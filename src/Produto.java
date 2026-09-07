public abstract class Produto {

    // ATRIBUTOS
    private int id;
    private String nome;
    private String status;                         
    private double quantidadeMateriaPrimaPorUnidade; 
    private double qualidade;
    private double probabilidadeFalhaAcumulada;
    private static int totalProdutosFabricados = 0;

    // CONSTRUTOR
    public Produto(int id, String nome, double quantidadeMateriaPrimaPorUnidade, double qualidade) {
        this.id = id;
        this.nome = nome;
        this.quantidadeMateriaPrimaPorUnidade = quantidadeMateriaPrimaPorUnidade;
        this.status = "aguardando";  // todo pato começa "aguardando" ser fabricado
        this.qualidade = qualidade;
        this.probabilidadeFalhaAcumulada = 0;
    
    }

    // MÉTODOS ABSTRATOS

    public abstract void processar();
    public abstract void processado();
    public abstract void embalando();
    public abstract void embalado();
    public abstract void inspecionando();
    public abstract void inspecionado();

    public abstract double calcularTempoProducao();
    
    public abstract String getTipo();


    // MÉTODOS CONCRETOS

    public double getQuantidadeMateriaPrimaPorUnidade() {
        return this.quantidadeMateriaPrimaPorUnidade;
    }

    public double getQualidade() {
        return qualidade;
    }

    public double getProbabilidadeFalhaAcumulada() {
        return probabilidadeFalhaAcumulada;
    }

    public void aumentarProbabilidadeFalha(double aumento) {
        this.probabilidadeFalhaAcumulada += aumento;

        // Mantém a probabilidade sendo até 100%
        if (probabilidadeFalhaAcumulada > 1) {
            probabilidadeFalhaAcumulada = 1;
        }
    }

    public void registrarFabricacao() {
        totalProdutosFabricados++;
    }

    public static int getTotalProdutosFabricados() {
        return totalProdutosFabricados;
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

    public void setStatus(String status) {
        this.status = status;
    }
}