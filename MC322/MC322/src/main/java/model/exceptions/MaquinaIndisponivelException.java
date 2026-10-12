package model.exceptions;

/**
 * Exceção lançada quando a linha de produção não pode operar porque há máquinas quebradas ou a linha está mal configurada.
 *
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class MaquinaIndisponivelException extends PlantaException {

    /**
     * Cria uma exceção com a mensagem especificada.
     *
     * @param mensagem mensagem que descreve o motivo da exceção
     */
    public MaquinaIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
