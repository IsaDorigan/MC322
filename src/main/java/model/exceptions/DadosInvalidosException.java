package model.exceptions;

/**
 * O conteúdo do arquivo de persistência não pôde ser aproveitado: arquivo vazio, XML malformado,
 * elementos obrigatórios ausentes, valores de tipo errado ou dados que violam as regras do domínio.
 *
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class DadosInvalidosException extends PersistenciaException {

    /**
     * Cria a exceção com a mensagem especificada.
     *
     * @param mensagem mensagem que descreve o que há de inválido nos dados
     */
    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria a exceção com a mensagem e a causa original.
     *
     * @param mensagem mensagem que descreve o que há de inválido nos dados
     * @param causa    exceção original (erro de sintaxe XML ou violação de regra do domínio)
     */
    public DadosInvalidosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
