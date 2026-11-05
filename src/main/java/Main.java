import java.nio.file.Path;
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
import model.LinhaDeProducao;
import model.exceptions.PlantaException;
import persistence.PersistenciaPlanta;
import service.GerenciadorProducao;
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

    /** Cria a classe principal do sistema */
    public Main(){}

    /** Quantidade mínima de borracha, em kg, que deve permanecer no estoque. */
    private static final double RESERVA_MINIMA_BORRACHA = 5.0; 
    /** Preço da borracha por quilograma. */
    private static final double PRECO_BORRACHA_POR_KG = 5.0;
    /** Arquivo XML onde o estado da planta é salvo, relativo à pasta de execução (ignorado pelo git). */
    private static final Path ARQUIVO_PERSISTENCIA = Path.of("dados", "planta.xml");

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
            // SEMENTE (escolhida no início)
            // ========================================
            configurarSemente(entrada);

            // ========================================
            // ESTRATÉGIAS (só o Main conhece as classes concretas)
            // ========================================
            List<EstrategiaProducao> estrategias = List.of(
                    new EstrategiaFilaIndiana(),
                    new EstrategiaRevoadaGigante(),
                    new EstrategiaNinhadaCheia()
            );

            // ========================================
            // PLANTA: carrega o estado salvo ou cria uma nova
            // ========================================
            PersistenciaPlanta persistencia = new PersistenciaPlanta(ARQUIVO_PERSISTENCIA);
            GerenciadorProducao gerenciador = carregarEstadoSalvo(persistencia, estrategias);
            if (gerenciador == null) {
                gerenciador = criarPlantaNova(entrada, estrategias);
            }

            // ========================================
            // MENU
            // ========================================
            new MenuFabrica(gerenciador, entrada, estrategias, persistencia).executar();

        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada encerrada. Fabrica desligada. Ate a proxima!");
        }
    }

    /**
     * Tenta recuperar o estado salvo em uma execução anterior.
     * A ausência do arquivo é uma situação normal (primeira execução) e apenas inicia uma planta nova.
     * Se o arquivo existir mas estiver inválido, o problema é mostrado e também se inicia uma planta nova;
     * o arquivo defeituoso só é sobrescrito se o usuário salvar depois.
     *
     * @param persistencia o componente de persistência
     * @param estrategias  as estratégias disponíveis, entre as quais se escolhe a salva
     * @return a planta recuperada, ou {@code null} se for preciso criar uma planta nova
     */
    private static GerenciadorProducao carregarEstadoSalvo(PersistenciaPlanta persistencia,
                                                           List<EstrategiaProducao> estrategias) {
        if (!persistencia.arquivoExiste()) {
            System.out.println("\nNenhum estado salvo encontrado. Iniciando uma planta nova.");
            return null;
        }
        try {
            GerenciadorProducao gerenciador = persistencia.carregar(estrategias);
            System.out.println("[OK] Estado anterior da planta recuperado de " + persistencia.getArquivo() + ".");
            return gerenciador;
        } catch (PlantaException e) {
            System.out.println();
            System.out.println(Estilo.vermelho("[ERRO] Nao foi possivel recuperar o estado salvo."));
            System.out.println(e.getMessage());
            System.out.println("Iniciando uma planta nova. O arquivo " + persistencia.getArquivo()
                    + " so sera substituido se voce salvar o estado.");
            return null;
        }
    }

    /**
     * Cria uma planta nova com os dados padrão do projeto, após o usuário escolher o cenário.
     *
     * @param entrada     o utilitário de leitura do console
     * @param estrategias as estratégias disponíveis (a primeira é a inicial)
     * @return a planta nova
     */
    private static GerenciadorProducao criarPlantaNova(EntradaConsole entrada, List<EstrategiaProducao> estrategias) {
        Cenario cenario = escolherCenario(entrada);

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

        // Máquinas na ordem da linha de produção
        LinhaDeProducao linhaPrincipal = new LinhaDeProducao("Bancada Principal");
        linhaPrincipal.adicionarMaquina(new MaquinaMoldagem(cenario));
        linhaPrincipal.adicionarMaquina(new MaquinaEmbalagem(cenario));
        linhaPrincipal.adicionarMaquina(new MaquinaInspecao(cenario));

        return new GerenciadorProducao(
                borracha, cenario, esteira1, esteira2, esteira3, estrategias.get(0), linhaPrincipal);
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
