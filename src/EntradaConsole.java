import java.util.Scanner;

/**
 * Leitura validada do teclado. Lê sempre linhas inteiras (nextLine),
 * o que evita os problemas clássicos de misturar nextInt() com nextLine().
 * Se a entrada acabar, o Scanner lança NoSuchElementException (tratada no Main).
 */
public class EntradaConsole {

    private final Scanner scanner;

    public EntradaConsole(Scanner scanner) {
        this.scanner = scanner;
    }

    public String lerLinha(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // Lê um inteiro entre min e max (inclusive), repetindo até ser válido
    public int lerInteiro(String prompt, int min, int max) {
        while (true) {
            String texto = lerLinha(prompt);

            try {
                int valor = Integer.parseInt(texto);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.println("[ERRO] Digite um número entre " + min + " e " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("[ERRO] Digite apenas números inteiros.");
            }
        }
    }

    // Lê um número decimal maior ou igual a zero (aceita vírgula ou ponto)
    public double lerDecimalNaoNegativo(String prompt) {
        while (true) {
            String texto = lerLinha(prompt).replace(',', '.');

            try {
                double valor = Double.parseDouble(texto);
                if (valor >= 0 && !Double.isNaN(valor) && !Double.isInfinite(valor)) {
                    return valor;
                }
                System.out.println("[ERRO] O valor não pode ser negativo.");
            } catch (NumberFormatException e) {
                System.out.println("[ERRO] Digite apenas números.");
            }
        }
    }

    // Lê um número inteiro longo opcional: Enter vazio devolve null
    public Long lerLongOpcional(String prompt) {
        while (true) {
            String texto = lerLinha(prompt);

            if (texto.isEmpty()) {
                return null;
            }

            try {
                return Long.parseLong(texto);
            } catch (NumberFormatException e) {
                System.out.println("[ERRO] Digite apenas números inteiros (ou Enter para sorteio livre).");
            }
        }
    }
}
