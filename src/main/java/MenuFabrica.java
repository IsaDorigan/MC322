import java.util.List;

import model.MateriaPrima;
import strategy.EstrategiaProducao;
import util.EntradaConsole;
import util.Estilo;

/**
 * Menu de console da fábrica (menu principal + submenus).
 * Só conhece a interface EstrategiaProducao: as estratégias concretas chegam prontas do Main.
 * 
 * @author Isadora KLuge Dorigan e Guilherme Forte Silva
 */
public class MenuFabrica {

    private static final String[] PRODUTOS = {"Pato Pequeno", "Pato Medio", "Pato Grande"};
    private static final int QUANTIDADE_MAXIMA = 1_000_000;

    private final GerenciadorProducao gerenciador;
    private final EntradaConsole entrada;
    private final List<EstrategiaProducao> estrategias;

    /**
     * Inicializa o menu da fábrica com os utilitários de entrada, o gerenciador principal e as estratégias disponíveis
     *
     * @param gerenciador O controlador central que executa as regras de negócio da fábrica
     * @param entrada     O utilitário responsável pela leitura e validação de dados do console
     * @param estrategias A lista de estratégias de produção que podem ser selecionadas
     */
    public MenuFabrica(GerenciadorProducao gerenciador, EntradaConsole entrada, List<EstrategiaProducao> estrategias) {
        this.gerenciador = gerenciador;
        this.entrada = entrada;
        this.estrategias = estrategias;
    }

    // ============================================
    // MENU PRINCIPAL
    // ============================================

    /**
     * Inicia o loop do menu principal exibindo as opções gerais e redirecionando para os submenus até que o usuário decida sair
     */
    public void executar() {
        int opcao;

        do {
            imprimirCabecalho();

            Estilo.secao("MENU PRINCIPAL");
            imprimirOpcao(1, "Demandas", "(atualizar e listar pedidos)");
            imprimirOpcao(2, "Fabricacao", "(pela estrategia ou por tipo)");
            imprimirOpcao(3, "Consultar", "(armazem, estoque e budget)");
            imprimirOpcao(4, "Comprar materia-prima", "(reabastecer borracha)");
            imprimirOpcao(5, "Gerenciar estrategia", "(trocar a ordem de producao)");
            imprimirOpcao(6, "Auditoria", "(relatorios e reparo de maquinas)");
            imprimirOpcao(0, "Sair");

            opcao = entrada.lerInteiro("ESCOLHA: ", 0, 6);

            switch (opcao) {
                case 1:
                    menuDemandas();
                    break;
                case 2:
                    menuFabricacao();
                    break;
                case 3:
                    menuConsultar();
                    break;
                case 4:
                    comprarMateriaPrima();
                    break;
                case 5:
                    menuEstrategia();
                    break;
                case 6:
                    menuAuditoria();
                    break;
                default:
                    break;  // 0: sai do laço
            }
        } while (opcao != 0);

        System.out.println("\nEncerrando a fabrica...");
        System.out.println("Ate a proxima, e que a lagoa esteja sempre tranquila!");
    }

    
    /**
     * Imprime painel de status mostrado a cada volta do menu principal (o banner de boas-vindas não se repete)
     */
    private void imprimirCabecalho() {
        System.out.println();
        System.out.println(Estilo.ciano(Estilo.linhaDupla()));
        System.out.println(Estilo.ciano(Estilo.caixa("[FABRICA DE PATINHOS DE BORRACHA]")));
        System.out.println(Estilo.ciano(Estilo.caixa("ESTRATEGIA ATUAL: [" + gerenciador.getEstrategiaAtual().getNomeEstrategia() + "]")));
        System.out.println(Estilo.ciano(Estilo.caixa("CENARIO ATIVO: [" + gerenciador.getCenario().getNome() + "]")));
        System.out.println(Estilo.ciano(Estilo.caixa("BUDGET ATUAL: R$ " + String.format("%.2f", gerenciador.getBudget()))));
        System.out.println(Estilo.ciano(Estilo.linhaDupla()));
    }

    /**
     * Exibe uma opção simples de menu formatada com seu número identificador e texto
     *
     * @param numero O número da opção que o usuário deve digitar
     * @param texto  O texto descritivo da opção
     */
    private void imprimirOpcao(int numero, String texto) {
        System.out.println(numero + " - " + texto);
    }

    /**
     * Exibe uma opção de menu detalhada e alinhada em colunas com um título e uma descrição auxiliar
     *
     * @param numero    O número da opção que o usuário deve digitar
     * @param titulo    O título principal da ação
     * @param descricao O texto explicativo adicional da ação
     */
    // Versão alinhada em colunas: título e descrição
    private void imprimirOpcao(int numero, String titulo, String descricao) {
        System.out.println(String.format("%d - %-22s %s", numero, titulo, descricao));
    }

    // ============================================
    // SUBMENU: DEMANDAS
    // ============================================

    /**
     * Exibe e gerencia o submenu focado na criação, atualização e listagem de demandas de produção
     */
    private void menuDemandas() {
        int opcao;

        do {
            System.out.println();
            Estilo.secao("DEMANDAS");
            for (int i = 0; i < PRODUTOS.length; i++) {
                imprimirOpcao(i + 1, "Atualizar demanda de " + PRODUTOS[i]);
            }
            int opcaoListar = PRODUTOS.length + 1;
            imprimirOpcao(opcaoListar, "Listar demandas (status e proxima da fila)");
            imprimirOpcao(0, "Voltar");

            opcao = entrada.lerInteiro("ESCOLHA: ", 0, opcaoListar);

            if (opcao >= 1 && opcao <= PRODUTOS.length) {
                String produto = PRODUTOS[opcao - 1];
                int quantidade = entrada.lerInteiro(
                        "Informe a quantidade de " + produto + " desejada (0 cancela o pedido): ", 0, QUANTIDADE_MAXIMA);
                gerenciador.atualizarDemanda(produto, quantidade);
            } else if (opcao == opcaoListar) {
                gerenciador.exibirDemandas();
            }
        } while (opcao != 0);
    }

    // ============================================
    // SUBMENU: FABRICAÇÃO
    // ============================================

    /**
     * Exibe e gerencia o submenu responsável por iniciar a linha de produção, seja pela estratégia ativa ou por escolha manual
     */
    private void menuFabricacao() {
        int opcao;

        do {
            System.out.println();
            Estilo.secao("FABRICACAO");
            imprimirOpcao(1, "Processar proxima demanda (estrategia: " + gerenciador.getEstrategiaAtual().getNomeEstrategia() + ")");
            for (int i = 0; i < PRODUTOS.length; i++) {
                imprimirOpcao(i + 2, "Fabricar item especifico: " + PRODUTOS[i]);
            }
            imprimirOpcao(0, "Voltar");

            opcao = entrada.lerInteiro("ESCOLHA: ", 0, PRODUTOS.length + 1);

            if (opcao == 1) {
                gerenciador.executarProximaProducao();
            } else if (opcao >= 2) {
                gerenciador.fabricarDemanda(PRODUTOS[opcao - 2]);
            }
        } while (opcao != 0);
    }

    // ============================================
    // SUBMENU: CONSULTAR
    // ============================================

    /**
     * Exibe e gerencia o submenu para visualização de relatórios do armazém, do estoque bruto e do orçamento disponível
     */
    private void menuConsultar() {
        int opcao;

        do {
            System.out.println();
            Estilo.secao("CONSULTAR");
            imprimirOpcao(1, "Ver armazem (produtos acabados)");
            imprimirOpcao(2, "Ver estoque de materia-prima");
            imprimirOpcao(3, "Ver budget");
            imprimirOpcao(0, "Voltar");

            opcao = entrada.lerInteiro("ESCOLHA: ", 0, 3);

            switch (opcao) {
                 case 1:
                    gerenciador.exibirArmazem();
                    break;
                case 2:
                    gerenciador.exibirEstoqueMateriaPrima();
                    break;
                case 3:
                    gerenciador.exibirBudget();
                    break;
                default:
                    break;
            }
        } while (opcao != 0);
    }

    // ============================================
    // COMPRAR MATÉRIA-PRIMA (reabastecimento)
    // ============================================

    /**
     * Inicia o fluxo de interação com o usuário para a aquisição de novos lotes de matéria-prima
     */
    private void comprarMateriaPrima() {
        MateriaPrima materia = gerenciador.getMateriaPrima();

        System.out.println();
        Estilo.secao("COMPRAR MATERIA-PRIMA");
        System.out.println(materia.getNome() + ": R$ " + String.format("%.2f", materia.getCustoPorUnidade())
                + " por " + materia.getUnidade() + " | Estoque atual: "
                + String.format("%.2f", materia.getQuantidade()) + " " + materia.getUnidade()
                + " | Budget: R$ " + String.format("%.2f", gerenciador.getBudget()));

        double quantidade = entrada.lerDecimalNaoNegativo(
                "Informe a quantidade de " + materia.getNome().toLowerCase() + " (" + materia.getUnidade()
                        + ") a comprar (0 cancela): ");

        if (quantidade == 0) {
            System.out.println("Compra cancelada.");
            return;
        }

        gerenciador.comprarMateriaPrima(quantidade);
    }

    // ============================================
    // SUBMENU: ESTRATÉGIA
    // ============================================

    /**
     * Exibe e gerencia o submenu que permite visualizar as estratégias disponíveis e alterar a ordem lógica de fabricação
     */
    private void menuEstrategia() {
        int opcao;

        do {
            System.out.println();
            Estilo.secao("GERENCIAR ESTRATEGIA");

            for (int i = 0; i < estrategias.size(); i++) {
                EstrategiaProducao estrategia = estrategias.get(i);
                String ativa = (estrategia == gerenciador.getEstrategiaAtual()) ? "  <== ATIVA" : "";
                imprimirOpcao(i + 1, estrategia.getNomeEstrategia() + ativa);
            }
            imprimirOpcao(0, "Voltar");

            opcao = entrada.lerInteiro("ESCOLHA: ", 0, estrategias.size());

            if (opcao >= 1) {
                gerenciador.setEstrategia(estrategias.get(opcao - 1));
                gerenciador.exibirDemandas();  // mostra na hora o impacto na ordem de produção
            }
        } while (opcao != 0);
    }

    // ============================================
    // SUBMENU: AUDITORIA
    // ============================================

    /**
     * Exibe e gerencia o submenu de relatórios diagnósticos e encaminha o usuário para as opções de manutenção preventiva
     */
    private void menuAuditoria() {
        int opcao;

        do {
            System.out.println();
            Estilo.secao("AUDITORIA");
            imprimirOpcao(1, "Relatorio geral");
            imprimirOpcao(2, "Detalhar maquinas");
            imprimirOpcao(3, "Detalhar produtos");
            imprimirOpcao(4, "Reparar maquinas");
            imprimirOpcao(0, "Voltar");

            opcao = entrada.lerInteiro("ESCOLHA: ", 0, 4);

            switch (opcao) {
                 case 1:
                    gerenciador.gerarAuditoriaGeral();
                    break;
                case 2:
                    gerenciador.gerarAuditoriaMaquinas();
                    break;
                case 3:
                    gerenciador.gerarAuditoriaProdutos();
                    break;
                case 4:
                    repararMaquinas();
                    break;
                default:
                    break;
            }
        } while (opcao != 0);
    }

    /**
     * Exibe a oficina de reparos e permite ao usuário escolher e consertar uma máquina danificada utilizando o orçamento da fábrica
     */
    private void repararMaquinas() {
        Estilo.titulo("OFICINA DE REPAROS");
        gerenciador.exibirMaquinas();

        int escolha = entrada.lerInteiro("Maquina a reparar (0 cancela): ", 0, gerenciador.getQuantidadeMaquinas());

        if (escolha > 0) {
            gerenciador.repararMaquina(escolha - 1);
        }
    }
}