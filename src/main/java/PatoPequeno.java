/**
 * Representa o modelo de pato de tamanho pequeno na produção
 * 
 * PatoMedio
 */
public class PatoPequeno extends Produto {

    /**
     * Construtor que inicializa um pato pequeno com seus valores específicos de consumo de borracha e qualidade base
     *
     * @param id O identificador numérico a ser atribuído a este produto
     */
    public PatoPequeno(int id) {
        super(
            id,
            "Pato Pequeno",
            0.3,   // kg de borracha por unidade
            0.5    // qualidade
        );
    }

    /**
     * Calcula o tempo estimado necessário para a fabricação de uma unidade do pato pequeno
     *
     * @return O valor do tempo de produção correspondente a este modelo
     */
    @Override
    public double calcularTempoProducao() {
        return 2.0;
    }

    /**
     * Obtem a identificação textual da categoria à qual este modelo pertence
     *
     * @return Uma string contendo o tipo do produto
     */
    @Override
    public String getTipo() {
        return "Pato Pequeno";
    }
}