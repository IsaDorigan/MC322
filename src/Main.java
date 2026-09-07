import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // ========================================
        // INTRODUÇÃO
        // ========================================

        System.out.println("\n========================================");
        System.out.println("       FÁBRICA DE PATINHOS");
        System.out.println("========================================");
        System.out.println("\nBem-vindo à fábrica de patinhos de borracha!");
        System.out.println("Aqui produzimos patinhos pequenos, " + "médios e grandes.");
        System.out.println("\nDesenvolvido por:");
        System.out.println("Isadora Kluge Dorigan e Guilherme Forte Silva");

        // ========================================
        // MATÉRIA-PRIMA
        // ========================================

        MateriaPrima borracha =
                new MateriaPrima(
                        1,
                        "Borracha",
                        100.0,
                        "kg",
                        5.0
                );

        // ========================================
        // ESTEIRAS
        // ========================================
        Esteira esteira1 = new Esteira(100,1); // Matéria prima até moldagem
        Esteira esteira2 = new Esteira(100,2); // Produto da moldagem até embalagem
        Esteira esteira3 = new Esteira(100,3); // embalagem para inspeção

        // ========================================
        // GERENCIADOR
        // ========================================

        double budgetInicial = 1000.0;

        GerenciadorProducao gerenciador = new GerenciadorProducao(borracha, budgetInicial, esteira1, esteira2, esteira3);

        // ========================================
        // MÁQUINAS
        // ========================================

        Maquina moldadora = new MaquinaMoldagem();
        Maquina embaladora = new MaquinaEmbalagem();
        Maquina inspecao = new MaquinaInspecao();

        gerenciador.adicionarMaquina(moldadora);
        gerenciador.adicionarMaquina(embaladora);
        gerenciador.adicionarMaquina(inspecao);

        // ========================================
        // MENU
        // ========================================

        int opcao = -1;

        while (opcao != 0) {

            System.out.println("\n========================================");
            System.out.println("              MENU INICIAL");
            System.out.println("========================================" );
            System.out.println("\nATUALIZAR DEMANDAS");
            System.out.println("1 - Atualizar demanda de Pato Pequeno");
            System.out.println("2 - Atualizar demanda de Pato Médio");
            System.out.println("3 - Atualizar demanda de Pato Grande");
            System.out.println("\nFABRICAR");
            System.out.println("4 - Fabricar Pato Pequeno");
            System.out.println("5 - Fabricar Pato Médio");
            System.out.println("6 - Fabricar Pato Grande");
            System.out.println("\nCONSULTAR");
            System.out.println("7 - Ver armazém");
            System.out.println("8 - Ver estoque de matéria-prima");
            System.out.println("9 - Ver budget");
            System.out.println("\nCOMPRAR MATÉRIA-PRIMA");
            System.out.println("10 - Comprar borracha");
            System.out.println("\n0 - Sair");
            System.out.print("\nEscolha: ");


            // ====================================
            // VALIDAÇÃO DA OPÇÃO
            // ====================================

            while (!scanner.hasNextInt()) {
                System.out.println("[ERRO] Digite apenas números.");
                scanner.next();
                System.out.print("Escolha: ");
            }
            opcao = scanner.nextInt();


            // ====================================
            // ATUALIZAR DEMANDA -
            // ====================================

            if (opcao == 1) {
                int quantidade = lerQuantidade(scanner, "Pato Pequeno");
                gerenciador.atualizarDemanda("Pato Pequeno", quantidade);
            } 

            else if (opcao == 2) {
                int quantidade = lerQuantidade(scanner, "Pato Médio");
                gerenciador.atualizarDemanda("Pato Médio",quantidade);
            } 
            else if (opcao == 3) {
                int quantidade = lerQuantidade(scanner,"Pato Grande");
                gerenciador.atualizarDemanda("Pato Grande",quantidade);
            }


            // ====================================
            // FABRICAR PATOS
            // ====================================

            else if (opcao == 4) {
                gerenciador.fabricarDemanda("Pato Pequeno");
            } 

            else if (opcao == 5) {
                gerenciador.fabricarDemanda("Pato Médio");
            } 
            
            else if (opcao == 6) {
                gerenciador.fabricarDemanda("Pato Grande");
            }


            // ====================================
            // ARMAZÉM
            // ====================================

            else if (opcao == 7) {
                gerenciador.exibirArmazem();
            }


            // ====================================
            // ESTOQUE
            // ====================================
            
            else if (opcao == 8) {
                gerenciador.exibirEstoqueMateriaPrima();
            }


            // ====================================
            // BUDGET
            // ====================================

            else if (opcao == 9) {
                gerenciador.exibirBudget();
            }


            // ====================================
            // COMPRAR MATÉRIA-PRIMA
            // ====================================

            else if (opcao == 10) {

                System.out.print("Informe a quantidade de borracha (kg): ");

                while (!scanner.hasNextDouble()) {
                    System.out.println("[ERRO] Digite apenas números.");
                    scanner.next();
                    System.out.print("Informe a quantidade de borracha (kg): ");
                }

                double quantidade = scanner.nextDouble();
                gerenciador.comprarMateriaPrima(quantidade);
             }

            // ====================================
            // SAIR
            // ====================================

            else if (opcao == 0) {
                System.out.println("\nEncerrando a fábrica...");
                System.out.println("Até a próxima!");
            }

            // ====================================
            // OPÇÃO INVÁLIDA
            // ====================================

            else {
                System.out.println("[ERRO] Opção inválida.");
            }
        }

        scanner.close();
    }

    // ============================================
    // LÊ QUANTIDADE DE PRODUTOS
    // ============================================

    private static int lerQuantidade(Scanner scanner,String nomeProduto) {
        System.out.print("Informe a quantidade de " + nomeProduto + " desejada: ");

        while (!scanner.hasNextInt()) {
            System.out.println("[ERRO] Digite apenas números inteiros.");
            scanner.next();
            System.out.print("Informe a quantidade de " + nomeProduto + " desejada: ");
        }

        int quantidade = scanner.nextInt();

        while (quantidade < 0) {
            System.out.println("[ERRO] A quantidade não pode ser negativa.");
            System.out.print("Informe novamente: ");

            while (!scanner.hasNextInt()) {
                System.out.println("[ERRO] Digite apenas números inteiros.");
                scanner.next();
            }

            quantidade = scanner.nextInt();
        }

        return quantidade;
    }
}