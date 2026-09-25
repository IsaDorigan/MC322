public class MaquinaMoldagem extends Maquina {

    // Chance da moldagem aumentar o risco de falha do produto, e quanto aumenta
    private static final double CHANCE_AUMENTAR_RISCO = 0.10;
    private static final double AUMENTO_RISCO = 0.10;

    public MaquinaMoldagem(Cenario cenario) {
        super("Moldadora de Patinhos",
                50,
                0.04,
                15,
                cenario);
    }

    @Override
    protected Produto executarProcessamento(Produto produto) {
        System.out.println("[OK] Moldando " + produto.getNome() + "...");

        produto.processar();

        possivelmenteAumentarRisco(produto, CHANCE_AUMENTAR_RISCO, AUMENTO_RISCO,
                "A moldagem aumentou a probabilidade de falha.");

        produto.processado();

        return produto;
    }

    @Override
    protected String getMensagemFalha() {
        return "Quá! O molde entupiu de borracha e o patinho se perdeu!";
    }

    @Override
    public String getTipo() {
        return "Moldagem";
    }
}