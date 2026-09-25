/**
 * Utilitário visual do console: separadores, títulos e cores ANSI.
 * As cores só são usadas se o terminal provavelmente suporta (variável TERM ou WT_SESSION)
 * e podem ser desligadas definindo a variável de ambiente NO_COLOR.
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

    private static boolean coresSuportadas() {
        if (System.getenv("NO_COLOR") != null) {
            return false;
        }
        return System.getenv("TERM") != null || System.getenv("WT_SESSION") != null;
    }

    private static String colorir(String texto, String codigo) {
        return CORES_ATIVAS ? codigo + texto + RESET : texto;
    }

    public static String negrito(String texto) { return colorir(texto, NEGRITO); }
    public static String verde(String texto) { return colorir(texto, VERDE); }
    public static String amarelo(String texto) { return colorir(texto, AMARELO); }
    public static String vermelho(String texto) { return colorir(texto, VERMELHO); }
    public static String ciano(String texto) { return colorir(texto, CIANO); }

    public static String linhaDupla() {
        return "=".repeat(LARGURA);
    }

    public static String linhaSimples() {
        return "-".repeat(LARGURA);
    }

    // Linha de caixa alinhada: | texto        |
    public static String caixa(String texto) {
        return String.format("| %-" + (LARGURA - 4) + "s |", texto);
    }

    public static String centralizar(String texto) {
        int espacos = Math.max(0, (LARGURA - texto.length()) / 2);
        return " ".repeat(espacos) + texto;
    }

    // Título de bloco (relatórios)
    public static void titulo(String texto) {
        System.out.println();
        System.out.println(ciano(linhaDupla()));
        System.out.println(ciano(centralizar(texto)));
        System.out.println(ciano(linhaDupla()));
    }

    // Cabeçalho de seção de menu
    public static void secao(String texto) {
        System.out.println(ciano(linhaSimples()));
        System.out.println(negrito("[" + texto + "]"));
        System.out.println(ciano(linhaSimples()));
    }
}
