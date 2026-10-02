package model.maquina;
import model.Produto;
import model.enums.Cenario;

/**
 * Representa a máquina responsável por moldar a borracha e fazer virar patinhos
 */
public class MaquinaMoldagem extends Maquina {

    // Chance da moldagem aumentar o risco de falha do produto, e quanto aumenta
    private static final double CHANCE_AUMENTAR_RISCO = 0.10;
    private static final double AUMENTO_RISCO = 0.10;

    /**
     * Construtor da máquina de moldagem que define os parâmetros padrão e ajusta as probabilidades 
     * com base no cenário
     *
     * @param cenario O cenário atual da simulação que afeta os parâmetros da máquina
     */
    public MaquinaMoldagem(Cenario cenario) {
        super("Moldadora de Patinhos",
                50,
                0.04,
                15,
                cenario);
    }

    /**
     * Executa o processo de moldagem do produto podendo aumentar seu risco de defeito dependendo 
     * da sorte e do cenário
     *
     * @param produto O produto que receberá a forma no molde
     * @return        O produto processado após passar pela etapa de moldagem
     */
    @Override
    protected Produto executarProcessamento(Produto produto) {
        System.out.println("[OK] Moldando " + produto.getNome() + "...");

        produto.processar();

        possivelmenteAumentarRisco(produto, CHANCE_AUMENTAR_RISCO, AUMENTO_RISCO,
                "A moldagem aumentou a probabilidade de falha.");

        produto.processado();

        return produto;
    }

    /**
     * Obtem a mensagem exibida quando ocorre uma falha de operação nesta máquina
     *
     * @return A mensagem alertando sobre a falha no molde
     */
    @Override
    protected String getMensagemFalha() {
        return "[ERRO] Quá! O molde entupiu de borracha e o patinho se perdeu!";
    }

    /**
     * Obtem o tipo de operação que esta máquina realiza dentro da linha de produção
     *
     * @return Uma string representando o tipo da máquina
     */
    @Override
    public String getTipo() {
        return "Moldagem";
    }
}