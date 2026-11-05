package model.exceptions;

/**
 * O arquivo de persistência solicitado não existe.
 * Na primeira execução isso é uma situação normal e a aplicação inicia com uma planta padrão.
 *
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class ArquivoInexistenteException extends PersistenciaException {

    /**
     * Cria a exceção com a mensagem especificada.
     *
     * @param mensagem mensagem que descreve qual arquivo não foi encontrado
     */
    public ArquivoInexistenteException(String mensagem) {
        super(mensagem);
    }
}
