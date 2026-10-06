package model.enums;
/**
 * Cenários de operação da fábrica. Cada cenário guarda os parâmetros
 * de orçamento, estoque inicial, confiabilidade e desgaste.
 * 
 * @author Isadora KLuge Dorigan e Guilherme Forte Silva
 */
public enum Cenario {

    //  nome     descrição    orçamento  estoque  falhaMaq falhaProd desgaste riscoInicial
    IDEAL("Ideal",
          "Lagoa tranquila: orçamento farto, maquinas confiaveis e pouco desgaste.",
          10000.0, 200.0, 0.5, 0.5, 0.5, 0.00),

    APOCALIPTICO("Apocaliptico",
          "Tempestade sobre a lagoa: orçamento no limite, muita falha e desgaste acelerado.",
          600.0, 40.0, 2.5, 2.0, 2.5, 0.10);

    private final String nome;
    private final String descricao;
    private final double orcamentoInicial;
    private final double estoqueInicialBorracha;
    private final double multiplicadorFalhaMaquina;  // multiplica a probabilidade base de falha das máquinas
    private final double multiplicadorFalhaProduto;  // multiplica a chance das máquinas aumentarem o risco do produto
    private final double multiplicadorDesgaste;      // multiplica o desgaste de saúde a cada uso
    private final double riscoInicialProduto;        // risco de falha que todo patinho já nasce carregando

    /**
     * Construtor do cenário com os parâmetros que definem o comportamento e a dificuldade da simulação
     *
     * @param nome                      O nome amigável do cenário
     * @param descricao                 A descrição narrativa das condições do cenário
     * @param orcamentoInicial          O valor inicial do orçamento em caixa
     * @param estoqueInicialBorracha    A quantidade de borracha disponível no início
     * @param multiplicadorFalhaMaquina O fator que multiplica a probabilidade base de falha das máquinas
     * @param multiplicadorFalhaProduto O fator que multiplica a chance das máquinas aumentarem o risco do produto
     * @param multiplicadorDesgaste     O fator que multiplica o desgaste de saúde a cada uso
     * @param riscoInicialProduto       O risco de falha inerente com o qual todo produto é criado
     */
    Cenario(String nome, String descricao, double orcamentoInicial, double estoqueInicialBorracha,
            double multiplicadorFalhaMaquina, double multiplicadorFalhaProduto,
            double multiplicadorDesgaste, double riscoInicialProduto) {
        this.nome = nome;
        this.descricao = descricao;
        this.orcamentoInicial = orcamentoInicial;
        this.estoqueInicialBorracha = estoqueInicialBorracha;
        this.multiplicadorFalhaMaquina = multiplicadorFalhaMaquina;
        this.multiplicadorFalhaProduto = multiplicadorFalhaProduto;
        this.multiplicadorDesgaste = multiplicadorDesgaste;
        this.riscoInicialProduto = riscoInicialProduto;
    }

    /**
     * Obtém o nome do cenário.
     *
     * @return O nome do cenário.
     */
    public String getNome() { return nome; }

    /**
     * Obtém a descrição do cenário.
     *
     * @return A descrição narrativa do cenário.
     */
    public String getDescricao() { return descricao; }
    
    /**
     * Obtém o valor do orçamento inicial disponível para a fábrica.
     *
     * @return O orçamento inicial.
     */
    public double getOrcamentoInicial() { return orcamentoInicial; }
   
    /**
     * Obtém a quantidade inicial do estoque de matéria-prima (borracha).
     *
     * @return O estoque inicial de borracha.
     */
    public double getEstoqueInicialBorracha() { return estoqueInicialBorracha; }
    
    /**
     * Obtém o multiplicador que afeta a probabilidade base de falha das máquinas no cenário.
     *
     * @return O multiplicador de falha da máquina.
     */
    public double getMultiplicadorFalhaMaquina() { return multiplicadorFalhaMaquina; }
    
    /**
     * Obtém o multiplicador que afeta a chance das máquinas aumentarem o risco de defeito no produto.
     *
     * @return O multiplicador de falha do produto.
     */
    public double getMultiplicadorFalhaProduto() { return multiplicadorFalhaProduto; }
   
    /**
     * Obtém o multiplicador de desgaste sofrido pela máquina a cada operação.
     *
     * @return O multiplicador de desgaste.
     */
    public double getMultiplicadorDesgaste() { return multiplicadorDesgaste; }
    
    /**
     * Obtém o risco inicial embutido com o qual todos os produtos (patinhos) já são criados.
     *
     * @return O risco inicial do produto.
     */
    public double getRiscoInicialProduto() { return riscoInicialProduto; }
}
