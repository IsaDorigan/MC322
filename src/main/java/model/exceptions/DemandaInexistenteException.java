package model.exceptions;

/**
 * Exceção lançada quando se tenta operar sobre uma demanda que não existe ou que não pode ser escolhida para produção.
 *
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class DemandaInexistenteException extends PlantaException {

    /**
     * Cria uma exceção com a mensagem especificada.
     *
     * @param mensagem mensagem que descreve o motivo da exceção
     */
    public DemandaInexistenteException(String mensagem) {
        super(mensagem);
    }
}
