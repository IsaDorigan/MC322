public class MaquinaEmbalagem extends Maquina {

    // Chance da embalagem aumentar o risco de falha do produto, e quanto aumenta
    private static final double CHANCE_AUMENTAR_RISCO = 0.15;
    private static final double AUMENTO_RISCO = 0.15;

    public MaquinaEmbalagem(Cenario cenario) {
        super("Embaladora de Patinhos",
                50.0,
                0.03,
                10.0,
                cenario);
    }

    @Override
    protected Produto executarProcessamento(Produto produto) {
        System.out.println("[OK] Embalando " + produto.getNome() + "...");

        produto.embalando();
        produto.embalado();

        possivelmenteAumentarRisco(produto, CHANCE_AUMENTAR_RISCO, AUMENTO_RISCO,
                "A embalagem aumentou a probabilidade de falha.");

        return produto;
    }

    @Override
    protected String getMensagemFalha() {
        return "Quá! A embaladora enrolou o plástico-bolha e o patinho foi amassado!";
    }

    @Override
    public String getTipo() {
        return "Embalagem";
    }
}
