import java.util.Random;

/**
 * Fonte única de números aleatórios da fábrica.
 * Com uma semente definida, as simulações ficam reproduzíveis
 * (útil para comparar os cenários Ideal e Apocalíptico).
 */
public final class Sorteio {

    private static Random random = new Random();

    private Sorteio() {
    }

    public static void definirSemente(long semente) {
        random = new Random(semente);
    }

    // Retorna true com a probabilidade informada (0.0 a 1.0)
    public static boolean ocorre(double probabilidade) {
        return random.nextDouble() < probabilidade;
    }

    // Número aleatório entre min (inclusive) e max (exclusivo)
    public static double entre(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }
}
