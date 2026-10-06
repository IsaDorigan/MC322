package model.exceptions;

/**
 * Exceção lançada quando o recurso é insuficiente
 * 
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class RecursoInsuficienteException extends RuntimeException {
    
    /**
     * Cria uma exceção com a mensagem especificada. 
     * 
     * @param mensagem mensagem que descreve o motivo da exceção
     */
    public RecursoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
