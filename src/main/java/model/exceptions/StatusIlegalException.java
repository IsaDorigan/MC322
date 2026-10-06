package model.exceptions;

/**
 * Exceção lançada quando o status apresenta algum erro
 * 
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class StatusIlegalException extends RuntimeException {
    
    /**
     * Cria uma exceção com a mensagem especificada. 
     * 
     * @param mensagem mensagem que descreve o motivo da exceção
     */
    public StatusIlegalException(String mensagem) {
        super(mensagem);
    }
}
