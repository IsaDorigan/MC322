import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    private static final double RESERVA_MINIMA_BORRACHA = 5.0;  // kg que nunca podem ser consumidos
    private static final double PRECO_BORRACHA_POR_KG = 5.0;

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            EntradaConsole entrada = new EntradaConsole(scanner);

            // Banner de boas-vindas: impresso UMA vez, antes do menu
            imprimirBoasVindas();

            // ========================================
            // SEMENTE E CENÁRIO (escolhidos no início)
            // ========================================
            configurarSemente(entrada);
            Cenario cenario = escolherCenario(entrada);

            // ========================================
            // MATÉRIA-PRIMA E ESTEIRAS
            // ========================================
            MateriaPrima borracha = new MateriaPrima(
                    1,
                    "Borracha",
                    cenario.getEstoqueInicialBorracha(),
                    "kg",
                    PRECO_BORRACHA_POR_KG,
                    RESERVA_MINIMA_BORRACHA
            );

            Esteira esteira1 = new Esteira(100, 1); // Matéria prima até moldagem
            Esteira esteira2 = new Esteira(100, 2); // Produto da moldagem até embalagem
            Esteira esteira3 = new Esteira(100, 3); // embalagem para inspeção

            // ========================================
            // ESTRATÉGIAS (só o Main conhece as classes concretas)
            // ========================================
            List<EstrategiaProducao> estrategias = List.of(
                    new EstrategiaFilaIndiana(),
                    new EstrategiaRevoadaGigante(),
                    new EstrategiaNinhadaCheia()
            );

            // ========================================
            // GERENCIADOR E MÁQUINAS (na ordem da linha de produção)
            // ========================================
            GerenciadorProducao gerenciador = new GerenciadorProducao(
                    borracha, cenario, esteira1, esteira2, esteira3, estrategias.get(0));

            gerenciador.adicionarMaquina(new MaquinaMoldagem(cenario));
            gerenciador.adicionarMaquina(new MaquinaEmbalagem(cenario));
            gerenciador.adicionarMaquina(new MaquinaInspecao(cenario));

            // ========================================
            // MENU
            // ========================================
            new MenuFabrica(gerenciador, entrada, estrategias).executar();

        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada encerrada. Fábrica desligada. Até a próxima!");
        }
    }

    private static void imprimirBoasVindas() {
        System.out.println();
        System.out.println(Estilo.ciano(Estilo.linhaDupla()));
        System.out.println(Estilo.ciano(Estilo.centralizar("FÁBRICA DE PATINHOS")));
        System.out.println(Estilo.ciano(Estilo.linhaDupla()));
        System.out.println("\nBem-vindo à fábrica de patinhos de borracha!");
        System.out.println("Aqui produzimos patinhos pequenos, médios e grandes.");
        System.out.println("\nDesenvolvido por:");
        System.out.println("Isadora Kluge Dorigan e Guilherme Forte Silva");
    }

    // Semente opcional: permite repetir exatamente a mesma simulação (bom para comparar cenários)
    private static void configurarSemente(EntradaConsole entrada) {
        System.out.println();
        Long semente = entrada.lerLongOpcional("Semente da simulação (número, ou Enter para sorteio livre): ");

        if (semente != null) {
            Sorteio.definirSemente(semente);
            System.out.println("[OK] Simulação reproduzível com a semente " + semente + ".");
        }
    }

    private static Cenario escolherCenario(EntradaConsole entrada) {
        Cenario[] cenarios = Cenario.values();

        System.out.println();
        Estilo.secao("ESCOLHA O CENÁRIO");

        for (int i = 0; i < cenarios.length; i++) {
            Cenario cenario = cenarios[i];
            System.out.println((i + 1) + " - " + cenario.getNome() + ": " + cenario.getDescricao());
            System.out.println("    Budget inicial: R$ " + String.format("%.2f", cenario.getOrcamentoInicial())
                    + " | Borracha inicial: " + cenario.getEstoqueInicialBorracha() + " kg");
        }

        int escolha = entrada.lerInteiro("ESCOLHA: ", 1, cenarios.length);
        Cenario escolhido = cenarios[escolha - 1];

        System.out.println("[OK] Cenário " + escolhido.getNome() + " ativado.");
        return escolhido;
    }
}
