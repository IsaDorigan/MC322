/**
 * Cenários de operação da fábrica. Cada cenário guarda os parâmetros
 * de orçamento, estoque inicial, confiabilidade e desgaste.
 */
public enum Cenario {

    //          nome           descrição                                              orçamento  estoque  falhaMaq falhaProd desgaste riscoInicial
    IDEAL("Ideal",
          "Lagoa tranquila: orçamento farto, máquinas confiáveis e pouco desgaste.",
          10000.0, 200.0, 0.5, 0.5, 0.5, 0.00),

    APOCALIPTICO("Apocalíptico",
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

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public double getOrcamentoInicial() { return orcamentoInicial; }
    public double getEstoqueInicialBorracha() { return estoqueInicialBorracha; }
    public double getMultiplicadorFalhaMaquina() { return multiplicadorFalhaMaquina; }
    public double getMultiplicadorFalhaProduto() { return multiplicadorFalhaProduto; }
    public double getMultiplicadorDesgaste() { return multiplicadorDesgaste; }
    public double getRiscoInicialProduto() { return riscoInicialProduto; }
}
