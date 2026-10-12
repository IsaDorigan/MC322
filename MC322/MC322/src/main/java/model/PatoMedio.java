package model;
/**
 * Representa o modelo de pato de tamanho medio na produção
 * 
 * @author Isadora KLuge Dorigan e Guilherme Forte Silva
 */
public class PatoMedio extends Produto {

    /**
     * Construtor que inicializa um pato medio com seus valores específicos de consumo de borracha e qualidade base
     *
     * @param id O identificador numérico a ser atribuído a este produto
     */
    public PatoMedio(int id) {
        super(
            id,
            "Pato Medio",
            0.45,  // kg de borracha por unidade
            0.6    // qualidade
        );
    }

    /**
     * Calcula o tempo estimado necessário para a fabricação de uma unidade do pato medio
     *
     * @return O valor do tempo de produção correspondente a este modelo
     */
    @Override
    public double calcularTempoProducao() {
        return 3.0;
    }

    /**
     * Obtém a identificação textual da categoria a qual este modelo pertence
     *
     * @return Uma string contendo o tipo do produto
     */
    @Override
    public String getTipo() {
        return "Pato Medio";
    }
}