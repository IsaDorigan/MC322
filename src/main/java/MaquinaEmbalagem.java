/**
 * Representa a máquina responsável por embalar os produtos na linha de produção
 */
public class MaquinaEmbalagem extends Maquina {

    // Chance da embalagem aumentar o risco de falha do produto, e quanto aumenta
    private static final double CHANCE_AUMENTAR_RISCO = 0.15;
    private static final double AUMENTO_RISCO = 0.15;

    /**
     * Construtor da máquina de embalagem que define seus parâmetros padrão e ajusta as probabilidades com base no cenário
     *
     * @param cenario O cenário atual da simulação que afeta os parâmetros da máquina
     */
    public MaquinaEmbalagem(Cenario cenario) {
        super("Embaladora de Patinhos",
                50.0,
                0.03,
                10.0,
                cenario);
    }

    /**
     * Executa o processo de embalagem do produto podendo aumentar seu risco de falha
     * Risco de falha depende do cenário
     *
     * @param produto O produto que será embalado pela máquina
     * @return        O produto após passar pelo processo de embalagem
     */
    @Override
    protected Produto executarProcessamento(Produto produto) {
        System.out.println("[OK] Embalando " + produto.getNome() + "...");

        produto.embalando();
        produto.embalado();

        possivelmenteAumentarRisco(produto, CHANCE_AUMENTAR_RISCO, AUMENTO_RISCO,
                "A embalagem aumentou a probabilidade de falha.");

        return produto;
    }

    /**
    * Obtem a mensagem exibida quando ocorre uma falha específica nessa máquina
    *
    * @return A mensagem descritiva da falha de embalagem
    */
    @Override
    protected String getMensagemFalha() {
        return "[ERRO] Quá! A embaladora enrolou o plástico-bolha e o patinho foi amassado!";
    }

    /**
     * Obtem o tipo de operação que essa máquina realiza na linha de produção
     *
     * @return Uma string representando o tipo da máquina
     */
    @Override
    public String getTipo() {
        return "Embalagem";
    }
}
