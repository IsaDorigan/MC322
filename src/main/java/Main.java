import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import model.Esteira;
import model.MateriaPrima;
import model.enums.Cenario;
import model.maquina.MaquinaEmbalagem;
import model.maquina.MaquinaInspecao;
import model.maquina.MaquinaMoldagem;
import strategy.*;
import util.EntradaConsole;
import util.Estilo;
import util.Sorteio;


/**
 * Classe principal que inicializa o sistema da Fábrica de Patinhos configurando 
 * o ambiente, cenário, máquinas e menu
 * 
 * @author Isadora KLuge Dorigan e Guilherme Forte Silva
 */
public class Main {

    private static final double RESERVA_MINIMA_BORRACHA = 5.0;  // kg que nunca podem ser consumidos
    private static final double PRECO_BORRACHA_POR_KG = 5.0;

    /**
     * Ponto de entrada do programa, responsável por configurar o console em UTF-8, inicializar os componentes e iniciar o menu principal
     *
     * @param args Argumentos de linha de comando passados na execução (não utilizados nesta aplicação)
     */
    public static void main(String[] args) {

        // Garante que a saída do Java use UTF-8 no terminal
        System.setOut(new PrintStream(
            new FileOutputStream(FileDescriptor.out),
            true,
            StandardCharsets.UTF_8
        ));

        System.setErr(new PrintStream(
            new FileOutputStream(FileDescriptor.err),
            true,
            StandardCharsets.UTF_8
        ));
        

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
            System.out.println("\nEntrada encerrada. Fabrica desligada. Ate a proxima!");
        }
    }

    /**
     * Exibe o banner de boas-vindas contendo o nome da fábrica e os créditos aos desenvolvedores
     */
    private static void imprimirBoasVindas() {
        System.out.println();
        System.out.println(Estilo.ciano(Estilo.linhaDupla()));
        System.out.println(Estilo.ciano(Estilo.centralizar("FABRICA DE PATINHOS")));
        System.out.println(Estilo.ciano(Estilo.linhaDupla()));
        System.out.println("\nBem-vindo a fabrica de patinhos de borracha!");
        System.out.println("Aqui produzimos patinhos pequenos, medios e grandes.");
        System.out.println("\nDesenvolvido por:");
        System.out.println("Isadora Kluge Dorigan e Guilherme Forte Silva");
    }

    /**
     * Permite definir uma semente numérica opcional para tornar os eventos aleatórios da simulação reprodutíveis
     *
     * @param entrada O utilitário responsável por capturar e validar a entrada de dados via console
     */
    private static void configurarSemente(EntradaConsole entrada) {
        System.out.println();
        Long semente = entrada.lerLongOpcional("Semente da simulacao (numero, ou Enter para sorteio livre): ");

        if (semente != null) {
            Sorteio.definirSemente(semente);
            System.out.println("[OK] Simulacao reproduzivel com a semente " + semente + ".");
        }
    }

    /**
     * Exibe as opções de cenário disponíveis no sistema e aguarda o usuário escolher a dificuldade da simulação
     *
     * @param entrada O utilitário responsável por capturar e validar a entrada de dados via console
     * @return        O cenário selecionado pelo usuário para configurar o orçamento, estoque e multiplicadores
     */
    private static Cenario escolherCenario(EntradaConsole entrada) {
        Cenario[] cenarios = Cenario.values();

        System.out.println();
        Estilo.secao("ESCOLHA O CENARIO");

        for (int i = 0; i < cenarios.length; i++) {
            Cenario cenario = cenarios[i];
            System.out.println((i + 1) + " - " + cenario.getNome() + ": " + cenario.getDescricao());
            System.out.println("    Budget inicial: R$ " + String.format("%.2f", cenario.getOrcamentoInicial())
                    + " | Borracha inicial: " + cenario.getEstoqueInicialBorracha() + " kg");
        }

        int escolha = entrada.lerInteiro("ESCOLHA: ", 1, cenarios.length);
        Cenario escolhido = cenarios[escolha - 1];

        System.out.println("[OK] Cenario " + escolhido.getNome() + " ativado.");
        return escolhido;
    }
}
