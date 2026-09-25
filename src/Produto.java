/**
 * Classe base de todos os patinhos. Implementa Auditavel para que a
 * qualidade e o risco acumulado apareçam nos relatórios de auditoria.
 * As transições de status ficam aqui (DRY); as subclasses só definem
 * os dados específicos de cada tamanho.
 */
public abstract class Produto implements Auditavel {

    // Acima desse risco acumulado (ou abaixo dessa qualidade) o produto entra em "atenção"
    private static final double LIMITE_RISCO = 0.20;
    private static final double LIMITE_QUALIDADE = 0.40;

    private static int totalProdutosFabricados = 0;

    // ATRIBUTOS
    private int id;
    private String nome;
    private StatusProduto status;
    private double quantidadeMateriaPrimaPorUnidade;
    private double qualidade;
    private double probabilidadeFalhaAcumulada;
    private int lote;

    // CONSTRUTOR
    public Produto(int id, String nome, double quantidadeMateriaPrimaPorUnidade, double qualidade) {
        this.id = id;
        this.nome = nome;
        this.quantidadeMateriaPrimaPorUnidade = quantidadeMateriaPrimaPorUnidade;
        this.status = StatusProduto.AGUARDANDO;  // todo pato começa aguardando ser fabricado
        this.qualidade = qualidade;
        this.probabilidadeFalhaAcumulada = 0;
        this.lote = 0;
    }

    // MÉTODOS ABSTRATOS (específicos de cada tamanho)
    public abstract double calcularTempoProducao();

    public abstract String getTipo();

    // TRANSIÇÕES DE STATUS (comuns a todos os patinhos)
    public void processar() {
        setStatus(StatusProduto.PROCESSANDO);
    }

    public void processado() {
        setStatus(StatusProduto.PROCESSADO);
        System.out.println("[OK] " + nome + " acabou de sair do molde!");
    }

    public void embalando() {
        setStatus(StatusProduto.EMBALANDO);
        System.out.println("[OK] " + nome + " está sendo embalado...");
    }

    public void embalado() {
        setStatus(StatusProduto.EMBALADO);
        System.out.println("[OK] " + nome + " acabou de ser embalado!");
    }

    public void inspecionando() {
        setStatus(StatusProduto.INSPECIONANDO);
        System.out.println("[OK] " + nome + " está sendo inspecionado...");
    }

    public void inspecionado() {
        setStatus(StatusProduto.INSPECIONADO);
        System.out.println("[OK] " + nome + " acabou de ser inspecionado!");
    }

    // AUDITAVEL
    @Override
    public String gerarRelatorioDiagnostico() {
        return String.format("%s #%d | Lote %d | Status: %s | Qualidade: %.0f%% | Risco acumulado: %.0f%%",
                nome, id, lote, status.getDescricao(), qualidade * 100, probabilidadeFalhaAcumulada * 100);
    }

    @Override
    public boolean precisaManutencao() {
        return probabilidadeFalhaAcumulada >= LIMITE_RISCO || qualidade < LIMITE_QUALIDADE;
    }

    // MÉTODOS CONCRETOS
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

    // GETTERS E SETTERS
    public double getQuantidadeMateriaPrimaPorUnidade() {
        return this.quantidadeMateriaPrimaPorUnidade;
    }

    public double getQualidade() {
        return qualidade;
    }

    public double getProbabilidadeFalhaAcumulada() {
        return probabilidadeFalhaAcumulada;
    }

    public int getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public StatusProduto getStatus() {
        return this.status;
    }

    public void setStatus(StatusProduto status) {
        this.status = status;
    }

    public int getLote() {
        return lote;
    }

    public void setLote(int lote) {
        this.lote = lote;
    }
}