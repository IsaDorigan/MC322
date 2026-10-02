package model;
/**
 * Representa o modelo de pato de tamanho grande na produção
 */
public class PatoGrande extends Produto {

    /**
     * Construtor que inicializa um pato grande com seus valores específicos de consumo de borracha e qualidade base
     *
     * @param id O identificador numérico a ser atribuído a este produto
     */
    public PatoGrande(int id) {
        super(
            id,
            "Pato Grande",
            0.6,   // kg de borracha por unidade
            0.7    // qualidade
        );
    }

    /**
     * Calcula o tempo estimado necessário para a fabricação de uma unidade do pato grande
     *
     * @return O valor do tempo de produção correspondente a este modelo
     */
    @Override
    public double calcularTempoProducao() {
        return 4.0;
    }

    /**
     * Obtem a identificação textual da categoria a qual este modelo pertence
     *
     * @return Uma string contendo o tipo do produto
     */
    @Override
    public String getTipo() {
        return "Pato Grande";
    }
}