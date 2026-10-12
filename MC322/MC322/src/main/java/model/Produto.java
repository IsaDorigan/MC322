package model;
import model.enums.StatusProduto;

/**
 * Classe base de todos os patinhos. Implementa Auditavel para que a
 * qualidade e o risco acumulado apareçam nos relatórios de auditoria.
 * As transições de status ficam aqui (DRY); as subclasses só definem
 * os dados específicos de cada tamanho.
 * 
 * @author Isadora KLuge Dorigan e Guilherme Forte Silva
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

    /**
     * Construtor da classe base que inicializa os dados de identificação, consumo e qualidade de um produto
     *
     * @param id                               O identificador único numérico do produto
     * @param nome                             O nome descritivo do patinho
     * @param quantidadeMateriaPrimaPorUnidade A quantidade de borracha exigida para fabricar uma unidade
     * @param qualidade                        O nível de qualidade base de fabricação
     */
    public Produto(int id, String nome, double quantidadeMateriaPrimaPorUnidade, double qualidade) {
        this.id = id;
        this.nome = nome;
        this.quantidadeMateriaPrimaPorUnidade = quantidadeMateriaPrimaPorUnidade;
        this.status = StatusProduto.AGUARDANDO;  // todo pato começa aguardando ser fabricado
        this.qualidade = qualidade;
        this.probabilidadeFalhaAcumulada = 0;
        this.lote = 0;
    }

    /**
     * Calcula o tempo estimado necessário para a fabricação de uma unidade deste produto
     *
     * @return O tempo de produção em unidades de tempo da simulação
     */
    public abstract double calcularTempoProducao();

    /**
     * Obtem a identificação textual da categoria a qual este modelo de produto pertence
     *
     * @return Uma string contendo o tipo do produto
     */
    public abstract String getTipo();

    /**
     * Altera o status do produto para PROCESSANDO indicando que ele iniciou a etapa de moldagem
     */
    public void processar() {
        setStatus(StatusProduto.PROCESSANDO);
    }

    /**
     * Altera o status do produto para PROCESSADO indicando que ele concluiu a etapa de moldagem
     */
    public void processado() {
        setStatus(StatusProduto.PROCESSADO);
        System.out.println("[OK] " + nome + " acabou de sair do molde!");
    }

    /**
     * Altera o status do produto para EMBALANDO indicando que ele iniciou a etapa de embalagem
     */
    public void embalando() {
        setStatus(StatusProduto.EMBALANDO);
        System.out.println("[OK] " + nome + " esta sendo embalado...");
    }

    /**
     * Altera o status do produto para EMBALADO indicando que ele concluiu a etapa de embalagem
     */
    public void embalado() {
        setStatus(StatusProduto.EMBALADO);
        System.out.println("[OK] " + nome + " acabou de ser embalado!");
    }

    /**
     * Altera o status do produto para INSPECIONANDO indicando que ele iniciou a etapa de inspeção
     */
    public void inspecionando() {
        setStatus(StatusProduto.INSPECIONANDO);
        System.out.println("[OK] " + nome + " esta sendo inspecionado...");
    }

    /**
     * Altera o status do produto para INSPECIONADO indicando que ele concluiu e foi aprovado na etapa de inspeção
     */
    public void inspecionado() {
        setStatus(StatusProduto.INSPECIONADO);
        System.out.println("[OK] " + nome + " acabou de ser inspecionado!");
    }

    /**
     * Gera um relatório formatado com os dados atuais de identificação, status, qualidade e risco do produto
     *
     * @return Uma string contendo o diagnóstico de auditoria desse produto
     */
    @Override
    public String gerarRelatorioDiagnostico() {
        return String.format("%s #%d | Lote %d | Status: %s | Qualidade: %.0f%% | Risco acumulado: %.0f%%",
                nome, id, lote, status.getDescricao(), qualidade * 100, probabilidadeFalhaAcumulada * 100);
    }

    /**
     * Verifica se o produto precisa de manutenção por ter ultrapassado os limites críticos de risco ou por estar com baixa qualidade
     *
     * @return true se o produto está em risco e precisa de atenção, false caso contrário
     */
    @Override
    public boolean precisaManutencao() {
        return probabilidadeFalhaAcumulada >= LIMITE_RISCO || qualidade < LIMITE_QUALIDADE;
    }

    /**
     * Aumenta a probabilidade de falha acumulada do produto limitando o valor máximo a 100%
     *
     * @param aumento O valor decimal a ser acrescido no risco atual do produto
     */
    public void aumentarProbabilidadeFalha(double aumento) {
        this.probabilidadeFalhaAcumulada += aumento;

        // Mantém a probabilidade sendo até 100%
        if (probabilidadeFalhaAcumulada > 1) {
            probabilidadeFalhaAcumulada = 1;
        }
    }

    /**
     * Incrementa o contador global de todos os produtos já fabricados com sucesso pela fábrica
     */
    public void registrarFabricacao() {
        totalProdutosFabricados++;
    }

    /**
     * Obtem o número total de produtos já fabricados desde o início da operação
     *
     * @return A quantidade total global de produtos finalizados
     */
    public static int getTotalProdutosFabricados() {
        return totalProdutosFabricados;
    }

    /**
     * Obtem a quantidade de matéria prima exigida para a produção de uma única unidade
     *
     * @return O volume de material necessário
     */
    public double getQuantidadeMateriaPrimaPorUnidade() {
        return this.quantidadeMateriaPrimaPorUnidade;
    }

    /**
     * Obtem o índice atual de qualidade deste produto
     *
     * @return A qualidade base em formato decimal
     */
    public double getQualidade() {
        return qualidade;
    }

    /**
     * Obtem a probabilidade atual de falha que este produto acumulou 
     *
     * @return O risco acumulado de falha em formato decimal
     */
    public double getProbabilidadeFalhaAcumulada() {
        return probabilidadeFalhaAcumulada;
    }

    /**
     * Obtem o identificador numérico do produto
     *
     * @return O ID numérico do produto
     */
    public int getId() {
        return this.id;
    }

    /**
     * Obtem o nome do produto
     *
     * @return O nome do patinho
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Obtem o status de produção em que o produto se encontra
     *
     * @return O status atual do produto
     */
    public StatusProduto getStatus() {
        return this.status;
    }

    /**
     * Altera o status de produção atual do produto
     *
     * @param status O novo status a ser atribuído ao patinho
     */
    public void setStatus(StatusProduto status) {
        this.status = status;
    }

    /**
     * Obtem o número de identificação do lote ao qual este produto pertence
     *
     * @return O número do lote
     */
    public int getLote() {
        return lote;
    }

    /**
     * Define a qual lote de fabricação este produto pertence
     *
     * @param lote O número do lote a ser atribuído
     */
    public void setLote(int lote) {
        this.lote = lote;
    }
}