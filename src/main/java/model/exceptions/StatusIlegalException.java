package model.exceptions;

public class StatusIlegalException extends RuntimeException {
    
    public StatusIlegalException(String mensagem) {
        super(mensagem);
    }
}
