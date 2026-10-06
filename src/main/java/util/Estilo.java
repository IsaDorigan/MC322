package util;
/**
 * Utilitário visual do console: separadores, títulos e cores ANSI.
 * As cores só são usadas se o terminal provavelmente suporta (variável TERM ou WT_SESSION)
 * e podem ser desligadas definindo a variável de ambiente NO_COLOR.
 * 
 * @author Isadora KLuge Dorigan e Guilherme Forte Silva
 */
public final class Estilo {

    public static final int LARGURA = 62;

    private static final String RESET = "\u001B[0m";
    private static final String NEGRITO = "\u001B[1m";
    private static final String VERDE = "\u001B[32m";
    private static final String AMARELO = "\u001B[33m";
    private static final String VERMELHO = "\u001B[31m";
    private static final String CIANO = "\u001B[36m";

    private static final boolean CORES_ATIVAS = coresSuportadas();

    private Estilo() {
    }

    /**
     * Verifica se o terminal atual suporta a exibição de cores ANSI analisando as variáveis de ambiente
     *
     * @return true se as cores são suportadas e não foram desativadas, false caso contrário
     */
    private static boolean coresSuportadas() {
        if (System.getenv("NO_COLOR") != null) {
            return false;
        }
        return System.getenv("TERM") != null || System.getenv("WT_SESSION") != null;
    }

    /**
     * Aplica um código de cor ANSI ao texto informado caso as cores estejam ativas no terminal
     *
     * @param texto  O texto que receberá a formatação de cor
     * @param codigo O código ANSI correspondente à cor ou estilo desejado
     * @return       O texto formatado com o código ANSI e reset no final, ou o texto original se as cores estiverem inativas
     */
    private static String colorir(String texto, String codigo) {
        return CORES_ATIVAS ? codigo + texto + RESET : texto;
    }

    /**
     * Formata o texto fornecido aplicando o estilo em negrito
     *
     * @param texto O texto a ser formatado
     * @return      O texto com o código ANSI de negrito aplicado
     */
    public static String negrito(String texto) { return colorir(texto, NEGRITO); }
    
    /**
     * Formata o texto fornecido aplicando a cor verde
     *
     * @param texto O texto a ser formatado
     * @return      O texto com o código ANSI da cor verde aplicado
     */
    public static String verde(String texto) { return colorir(texto, VERDE); }
    
    /**
     * Formata o texto fornecido aplicando a cor amarela
     *
     * @param texto O texto a ser formatado
     * @return      O texto com o código ANSI da cor amarela aplicado
     */
    public static String amarelo(String texto) { return colorir(texto, AMARELO); }
    
    /**
     * Formata o texto fornecido aplicando a cor vermelha
     *
     * @param texto O texto a ser formatado
     * @return      O texto com o código ANSI da cor vermelha aplicado
     */
    public static String vermelho(String texto) { return colorir(texto, VERMELHO); }
   
    /**
     * Formata o texto fornecido aplicando a cor ciano
     *
     * @param texto O texto a ser formatado
     * @return      O texto com o código ANSI da cor ciano aplicado
     */
    public static String ciano(String texto) { return colorir(texto, CIANO); }

    /**
     * Gera uma linha delimitadora dupla preenchendo a largura padrão definida para o console
     *
     * @return Uma string contendo a linha dupla formada por sinais de igual
     */
    public static String linhaDupla() {
        return "=".repeat(LARGURA);
    }

    /**
     * Gera uma linha delimitadora simples preenchendo a largura padrão definida para o console
     *
     * @return Uma string contendo a linha simples formada por traços
     */
    public static String linhaSimples() {
        return "-".repeat(LARGURA);
    }

   /**
     * Formata o texto dentro de uma estrutura visual de caixa, alinhado com bordas laterais
     *
     * @param texto O texto a ser encapsulado na caixa
     * @return      A string formatada no formato de caixa
     */
    public static String caixa(String texto) {
        return String.format("| %-" + (LARGURA - 4) + "s |", texto);
    }

    /**
     * Centraliza o texto adicionando espaços em branco à esquerda com base na largura padrão da tela
     *
     * @param texto O texto a ser centralizado
     * @return      O texto com o espaçamento adequado para exibição centralizada
     */
    public static String centralizar(String texto) {
        int espacos = Math.max(0, (LARGURA - texto.length()) / 2);
        return " ".repeat(espacos) + texto;
    }

    /**
     * Exibe um título em destaque, formatado com cor ciano, linhas duplas e texto centralizado
     *
     * @param texto O texto do título a ser exibido
     */
    public static void titulo(String texto) {
        System.out.println();
        System.out.println(ciano(linhaDupla()));
        System.out.println(ciano(centralizar(texto)));
        System.out.println(ciano(linhaDupla()));
    }

    /**
     * Exibe  um cabeçalho para uma seção de menu, formatado com cor ciano, texto em negrito e linhas simples
     *
     * @param texto O nome da seção a ser exibida
     */
    public static void secao(String texto) {
        System.out.println(ciano(linhaSimples()));
        System.out.println(negrito("[" + texto + "]"));
        System.out.println(ciano(linhaSimples()));
    }
}
