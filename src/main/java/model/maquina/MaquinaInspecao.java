package model.maquina;
import model.Produto;
import model.enums.Cenario;
import model.enums.StatusProduto;
import util.Sorteio;

/**
 * Representa a máquina de inspeção responsável por avaliar a qualidade e aprovar 
 * ou rejeitar os produtos na linha
 */
public class MaquinaInspecao extends Maquina {

    // A chance de rejeição soma a qualidade (peso abaixo) com o risco acumulado do produto
    private static final double PESO_QUALIDADE_NA_REJEICAO = 0.30;

    /**
     * Construtor da máquina de inspeção que define seus parâmetros padrão e ajusta as probabilidades
     * 
     * @param cenario O cenário atual da simulação que afeta os parâmetros da máquina
     */
    public MaquinaInspecao(Cenario cenario) {
        super(
                "Máquina de Inspeção",
                50.0,
                0.10,
                20.0,
                cenario
        );
    }

    /**
     * Executa a inspeção do produto calculando a chance de rejeição com base na qualidade e no risco acumulado 
     *
     * @param produto O produto que passará pela avaliação rigorosa de qualidade
     * @return        O produto aprovado com o status atualizado, ou null caso seja reprovado e descartado
     */
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

    /**
     * Obtem a mensagem exibida quando ocorre uma falha operacional nesta máquina
     *
     * @return A mensagem descritiva da falha de inspeção
     */
    @Override
    protected String getMensagemFalha() {
        return "[ERRO] Quá! O sensor de inspeção pifou e o patinho ficou sem laudo!";
    }

    /**
     * Obtem o tipo de operação que essa máquina realiza dentro da linha de produção
     *
     * @return Uma string representando o tipo da máquina
     */
    @Override
    public String getTipo() {
        return "Inspeção";
    }
}