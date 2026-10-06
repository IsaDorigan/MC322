package model.exceptions;

/**
 * Exceção lançada quando um argumento inválido é fornecido
 * 
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class ArgumentoIlegalException extends RuntimeException {
    
    /**
     * Cria uma exceção com a mensagem especificada. 
     * 
     * @param mensagem mensagem que descreve o motivo da exceção
     */
    public ArgumentoIlegalException(String mensagem) {
        super(mensagem);
    }
}
