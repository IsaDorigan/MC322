import java.util.Scanner;

/**
 * Leitura validada do teclado. Lê sempre linhas inteiras, o que evita 
 * os problemas clássicos de misturar leituras numéricas e de texto
 * 
 * EntradaConsole
 */
public class EntradaConsole {

    private final Scanner scanner;

    /**
     * Construtor da classe responsável pela entrada de dados
     *
     * @param scanner O objeto Scanner utilizado para ler as entradas do console
     */
    public EntradaConsole(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Exibe uma mensagem de orientação e lê a próxima linha inteira digitada pelo usuário
     *
     * @param prompt O texto que será exibido antes de aguardar a leitura
     * @return       A string lida do teclado sem os espaços em branco nas extremidades
     */
    public String lerLinha(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Lê e valida um número inteiro dentro de um intervalo especificado.
     * A leitura se repete em loop até que um valor válido seja fornecido
     *
     * @param prompt O texto que será exibido antes de aguardar a leitura
     * @param min    O valor mínimo aceitável
     * @param max    O valor máximo aceitável
     * @return       O número inteiro validado
     */
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

    /**
     * Lê e valida um número decimal maior ou igual a zero, aceitando separação por vírgula ou ponto
     *
     * @param prompt O texto que será exibido antes de aguardar a leitura
     * @return       O número decimal validado e não negativo
     */
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

    /**
     * Lê um número inteiro longo de opcional. Enter vazio devolve null
     *
     * @param prompt O texto que será exibido antes de aguardar a leitura
     * @return       O valor do tipo Long digitado ou null caso a entrada seja vazia
     */
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
