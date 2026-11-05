package persistence;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import model.exceptions.DadosInvalidosException;

/**
 * Visão de leitura de um elemento XML que converte e valida os valores lidos.
 * Toda falha (elemento ausente ou duplicado, número ou valor de enumeração inválido) vira uma
 * {@link DadosInvalidosException} cuja mensagem informa o caminho do elemento no arquivo,
 * por exemplo {@code planta/linhaDeProducao/maquinas/maquina[2]/saude}.
 *
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
final class NoXml {

    private final Element elemento;
    private final String caminho;

    /**
     * Cria a visão de um elemento.
     *
     * @param elemento elemento DOM observado
     * @param caminho  caminho do elemento dentro do arquivo, usado nas mensagens de erro
     */
    NoXml(Element elemento, String caminho) {
        this.elemento = elemento;
        this.caminho = caminho;
    }

    /**
     * Retorna o caminho do elemento dentro do arquivo.
     *
     * @return o caminho, no formato {@code planta/materiasPrimas/materiaPrima[1]}
     */
    String caminho() {
        return caminho;
    }

    /**
     * Retorna o único filho direto com o nome indicado.
     *
     * @param nome nome do elemento filho
     * @return a visão do filho
     * @throws DadosInvalidosException se o filho não existir ou aparecer mais de uma vez
     */
    NoXml filho(String nome) {
        List<NoXml> encontrados = filhos(nome);
        if (encontrados.isEmpty()) {
            throw new DadosInvalidosException("Elemento obrigatorio ausente: " + caminho + "/" + nome + ".");
        }
        if (encontrados.size() > 1) {
            throw new DadosInvalidosException("Elemento duplicado: " + caminho + "/" + nome + ".");
        }
        return encontrados.get(0);
    }

    /**
     * Retorna o filho direto com o nome indicado, se existir.
     *
     * @param nome nome do elemento filho
     * @return a visão do filho ou {@code null} se ele não existir
     * @throws DadosInvalidosException se o filho aparecer mais de uma vez
     */
    NoXml filhoOpcional(String nome) {
        List<NoXml> encontrados = filhos(nome);
        if (encontrados.size() > 1) {
            throw new DadosInvalidosException("Elemento duplicado: " + caminho + "/" + nome + ".");
        }
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    /**
     * Retorna todos os filhos diretos com o nome indicado, na ordem do arquivo.
     *
     * @param nome nome dos elementos filhos
     * @return lista (possivelmente vazia) com a visão de cada filho
     */
    List<NoXml> filhos(String nome) {
        List<Element> elementos = new ArrayList<>();
        NodeList nos = elemento.getChildNodes();
        for (int i = 0; i < nos.getLength(); i++) {
            Node no = nos.item(i);
            if (no.getNodeType() == Node.ELEMENT_NODE && nome.equals(no.getNodeName())) {
                elementos.add((Element) no);
            }
        }
        List<NoXml> resultado = new ArrayList<>();
        for (int i = 0; i < elementos.size(); i++) {
            resultado.add(new NoXml(elementos.get(i), caminho + "/" + nome + "[" + (i + 1) + "]"));
        }
        // Quando só há um elemento, o caminho fica sem índice, o que deixa as mensagens mais legíveis
        if (resultado.size() == 1) {
            return List.of(new NoXml(elementos.get(0), caminho + "/" + nome));
        }
        return resultado;
    }

    /**
     * Lê o texto de um filho obrigatório.
     *
     * @param nome nome do elemento filho
     * @return o texto do filho, sem espaços nas pontas
     * @throws DadosInvalidosException se o filho não existir ou estiver vazio
     */
    String texto(String nome) {
        NoXml filho = filho(nome);
        String valor = filho.elemento.getTextContent().trim();
        if (valor.isEmpty()) {
            throw new DadosInvalidosException("Elemento vazio: " + filho.caminho + ".");
        }
        return valor;
    }

    /**
     * Lê um filho obrigatório como número decimal finito.
     *
     * @param nome nome do elemento filho
     * @return o valor lido
     * @throws DadosInvalidosException se o filho não existir ou não for um número decimal finito
     */
    double decimal(String nome) {
        String valor = texto(nome);
        try {
            double numero = Double.parseDouble(valor);
            if (Double.isNaN(numero) || Double.isInfinite(numero)) {
                throw new NumberFormatException();
            }
            return numero;
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Valor '" + valor + "' nao e um numero decimal valido em "
                    + caminho + "/" + nome + ".");
        }
    }

    /**
     * Lê um filho obrigatório como número inteiro.
     *
     * @param nome nome do elemento filho
     * @return o valor lido
     * @throws DadosInvalidosException se o filho não existir ou não for um número inteiro
     */
    int inteiro(String nome) {
        String valor = texto(nome);
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Valor '" + valor + "' nao e um numero inteiro valido em "
                    + caminho + "/" + nome + ".");
        }
    }

    /**
     * Lê um filho obrigatório como booleano ({@code true} ou {@code false}).
     *
     * @param nome nome do elemento filho
     * @return o valor lido
     * @throws DadosInvalidosException se o filho não existir ou não for {@code true} nem {@code false}
     */
    boolean booleano(String nome) {
        String valor = texto(nome);
        if (valor.equals("true")) {
            return true;
        }
        if (valor.equals("false")) {
            return false;
        }
        throw new DadosInvalidosException("Valor '" + valor + "' deve ser true ou false em " + caminho + "/" + nome + ".");
    }

    /**
     * Lê um filho obrigatório como constante de uma enumeração.
     *
     * @param <E>  tipo da enumeração
     * @param tipo classe da enumeração
     * @param nome nome do elemento filho
     * @return a constante correspondente
     * @throws DadosInvalidosException se o filho não existir ou não corresponder a nenhuma constante
     */
    <E extends Enum<E>> E enumeracao(Class<E> tipo, String nome) {
        String valor = texto(nome);
        try {
            return Enum.valueOf(tipo, valor);
        } catch (IllegalArgumentException e) {
            throw new DadosInvalidosException("Valor '" + valor + "' invalido em " + caminho + "/" + nome
                    + " (valores aceitos: " + String.join(", ", nomesDe(tipo)) + ").");
        }
    }

    /**
     * Lê um atributo obrigatório como texto.
     *
     * @param nome nome do atributo
     * @return o valor do atributo
     * @throws DadosInvalidosException se o atributo não existir ou estiver vazio
     */
    String atributo(String nome) {
        String valor = elemento.getAttribute(nome).trim();
        if (valor.isEmpty()) {
            throw new DadosInvalidosException("Atributo obrigatorio ausente: " + caminho + "@" + nome + ".");
        }
        return valor;
    }

    /**
     * Lê um atributo obrigatório como número inteiro.
     *
     * @param nome nome do atributo
     * @return o valor do atributo
     * @throws DadosInvalidosException se o atributo não existir ou não for um número inteiro
     */
    int atributoInteiro(String nome) {
        String valor = atributo(nome);
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Valor '" + valor + "' nao e um numero inteiro valido em "
                    + caminho + "@" + nome + ".");
        }
    }

    private static <E extends Enum<E>> List<String> nomesDe(Class<E> tipo) {
        List<String> nomes = new ArrayList<>();
        for (E constante : tipo.getEnumConstants()) {
            nomes.add(constante.name());
        }
        return nomes;
    }
}
