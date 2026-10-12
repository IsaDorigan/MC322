package model.exceptions;

/**
 * Exceção base de todos os erros de regra de negócio da planta industrial.
 * Permite que a camada de interação com o usuário capture, com um único {@code catch},
 * qualquer erro de domínio previsto, sem precisar capturar exceções genéricas.
 *
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class PlantaException extends RuntimeException {

    /**
     * Cria uma exceção de domínio com a mensagem especificada.
     *
     * @param mensagem mensagem que descreve o motivo da exceção
     */
    public PlantaException(String mensagem) {
        super(mensagem);
    }
}
