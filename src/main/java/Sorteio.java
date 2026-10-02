import java.util.Random;

/**
 * Fonte de números aleatórios da fábrica.
 * Com uma semente definida, as simulações ficam reproduzíveis
 * (útil para comparar os cenários Ideal e Apocalíptico).
 * 
 * Sorteio
 */
public final class Sorteio {

    private static Random random = new Random();

    private Sorteio() {
    }

    /**
     * Define uma semente fixa para o gerador de números aleatórios 
     *
     * @param semente O valor da semente a ser utilizada
     */
    public static void definirSemente(long semente) {
        random = new Random(semente);
    }

    /**
     * Sorteia e verifica se um evento ocorre com base na probabilidade informada
     *
     * @param probabilidade A chance do evento ocorrer variando de 0.0 a 1.0
     * @return              true se o evento sorteado ocorrer, false caso contrário
     */
    public static boolean ocorre(double probabilidade) {
        return random.nextDouble() < probabilidade;
    }

    /**
     * Gera um número decimal aleatório dentro do intervalo especificado
     *
     * @param min O valor mínimo (inclusivo) do intervalo
     * @param max O valor máximo (exclusivo) do intervalo
     * @return    Um número decimal aleatório entre o valor mínimo e o máximo
     */
    public static double entre(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }
}
