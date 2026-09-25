public class MaquinaInspecao extends Maquina {

    // A chance de rejeição soma a qualidade (peso abaixo) com o risco acumulado do produto
    private static final double PESO_QUALIDADE_NA_REJEICAO = 0.30;

    public MaquinaInspecao(Cenario cenario) {
        super(
                "Máquina de Inspeção",
                50.0,
                0.10,
                20.0,
                cenario
        );
    }

    @Override
    protected Produto executarProcessamento(Produto produto) {
        System.out.println("[OK] Iniciando inspeção...");

        produto.inspecionando();

        // A qualidade é diretamente proporcional à chance de rejeição na inspeção
        // Somamos essa chance de rejeição devido à qualidade com a probabilidade de falha acumulada
        double chanceRejeicao = (produto.getQualidade() * PESO_QUALIDADE_NA_REJEICAO)
                + produto.getProbabilidadeFalhaAcumulada();

        // Limita a chance a 100%
        if (chanceRejeicao > 1.0) {
            chanceRejeicao = 1.0;
        }

        if (Sorteio.ocorre(chanceRejeicao)) {
            produto.setStatus(StatusProduto.REJEITADO);
            System.out.println("[ERRO] " + produto.getNome() + " foi rejeitado na inspeção.");
            return null;
        }

        produto.inspecionado();  // atualiza o status para INSPECIONADO

        System.out.println("[OK] " + produto.getNome() + " aprovado na inspeção!");

        return produto;
    }

    @Override
    protected String getMensagemFalha() {
        return "Quá! O sensor de inspeção pifou e o patinho ficou sem laudo!";
    }

    @Override
    public String getTipo() {
        return "Inspeção";
    }
}