package model.exceptions;

/**
 * Erro ao salvar ou carregar o estado da planta (falha de leitura ou escrita do arquivo).
 * É a base das exceções mais específicas de persistência, de modo que a camada de interação com o
 * usuário possa tratá-las junto com os demais erros de domínio ({@link PlantaException}).
 *
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class PersistenciaException extends PlantaException {

    /**
     * Cria uma exceção de persistência com a mensagem especificada.
     *
     * @param mensagem mensagem que descreve o motivo da falha
     */
    public PersistenciaException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria uma exceção de persistência com a mensagem e a causa original da falha.
     *
     * @param mensagem mensagem que descreve o motivo da falha
     * @param causa    exceção original (por exemplo, uma {@link java.io.IOException})
     */
    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
