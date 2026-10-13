package model.exceptions;

/**
 * Exceção lançada quando o orçamento da fábrica não cobre o custo da operação solicitada (produção, compra ou reparo).
 *
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class OrcamentoInsuficienteException extends PlantaException {

    /**
     * Cria uma exceção com a mensagem especificada.
     *
     * @param mensagem mensagem que descreve o motivo da exceção
     */
    public OrcamentoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
