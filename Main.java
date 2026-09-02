import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // ==============================
        // PLANTA INDUSTRIAL
        // ==============================

        // Matéria-prima disponível na fábrica
        MateriaPrima borracha = new MateriaPrima(
                1,
                "Borracha",
                100.0,
                " kg",
                5.0
        );

        // Produtos disponíveis
        Produto patoPequeno = new Produto(
                1,
                "Pato Pequeno",
                0.3
        );

        Produto patoMedio = new Produto(
                2,
                "Pato Médio",
                0.45
        );

        Produto patoGrande = new Produto(
                3,
                "Pato Grande",
                0.6
        );

        // Equipamentos
        Maquina maquina = new Maquina(
                "Moldadora de Patinhos",
                50.0
        );

        Esteira esteira = new Esteira(50.0);

        EstacaoInspecao inspecao = new EstacaoInspecao();


        // ==============================
        // MENU PRINCIPAL
        // ==============================

        int opcao = 0;

        while (opcao != 3) {

            System.out.println("\n========================================");
            System.out.println("        FÁBRICA DE PATINHOS");
            System.out.println("========================================");
            System.out.println("1 - Iniciar produção");
            System.out.println("2 - Consultar estoque");
            System.out.println("3 - Sair");
            System.out.print("Escolha: ");

            // Garante que a entrada seja numérica
            while (!scanner.hasNextInt()) {
                System.out.println("[ERRO] Digite apenas números.");
                scanner.next();
                System.out.print("Escolha: ");
            }

            opcao = scanner.nextInt();


            // ==============================
            // INICIAR PRODUÇÃO
            // ==============================

            if (opcao == 1) {

                System.out.println("\nProdutos disponíveis:");
                System.out.println("1 - " + patoPequeno.getNome() + " (demanda: " + patoPequeno.getDemandaMateriaPrima() + " kg)");
                System.out.println("2 - " + patoMedio.getNome() + " (demanda: " + patoMedio.getDemandaMateriaPrima() + " kg)");
                System.out.println("3 - " + patoGrande.getNome() + " (demanda: " + patoGrande.getDemandaMateriaPrima() + " kg)");

                System.out.print("\n");
                System.out.print("Selecione o produto: ");

                while (!scanner.hasNextInt()) {
                    System.out.println("[ERRO] Digite apenas números.");
                    scanner.next();
                    System.out.print("Selecione o produto: ");
                }

                int escolhaProduto = scanner.nextInt();

                Produto produto;

                if (escolhaProduto == 1) {
                    produto = patoPequeno;
                } else if (escolhaProduto == 2) {
                    produto = patoMedio;
                } else if (escolhaProduto == 3) {
                    produto = patoGrande;
                } else {
                    System.out.println("[ERRO] Produto inválido.");
                    continue;
                }


                // ==============================
                // DEMANDA
                // ==============================

                System.out.print("Informe a demanda de matéria-prima (kg): ");

                while (!scanner.hasNextDouble()) {
                    System.out.println("[ERRO] Digite apenas números.");
                    scanner.next();
                    System.out.print("Informe a demanda de matéria-prima (kg): ");
                }

                double demanda = scanner.nextDouble();
                System.out.print("\n");


                // ==============================
                // VERIFICAÇÕES
                // ==============================

                System.out.println("[OK] Verificando disponibilidade de matéria-prima...");

                if (demanda <= 0) {
                    System.out.println("[ERRO] A demanda deve ser maior que zero.");
                    continue;
                }

                if (!borracha.verificarDisponibilidade(demanda)) {
                    System.out.println("[ERRO] Matéria-prima insuficiente.");
                    continue;
                }

                if (!esteira.verificarCapacidade(demanda)) {
                    System.out.println("[ERRO] A demanda ultrapassa a capacidade da esteira.");
                    continue;
                }


                // ==============================
                // PRODUÇÃO
                // ==============================

                System.out.println("[OK] Matéria-prima disponível.\n");

                esteira.ligar();
                maquina.ligar();
                esteira.adicionarItem(borracha);
                Object item = esteira.removerItem();

                if (item == null) {
                    esteira.desligar();
                    maquina.desligar();
                    continue;
                }

                System.out.println("[OK] Matéria-prima transportada até a máquina.");

                // Máquina processa a matéria-prima
                Produto produtoProcessado = maquina.processar(borracha, produto, demanda);
    
                // Coloca o produto processado na esteira e o remove após ele ter andado
                esteira.adicionarItem(produtoProcessado);
                Object produtoNaInspecao = esteira.removerItem();

                System.out.println("[OK] " + produto.getNome() + " transportado para inspeção.");
               

                if (produtoNaInspecao instanceof Produto) {

                    // Liga a estação de inspeção
                    inspecao.ativar();

                    // Inspeciona o produto
                    inspecao.inspecionar((Produto) produtoNaInspecao);
                }

                // Desliga os equipamentos
                maquina.desligar();
                esteira.desligar();
                inspecao.desativar();

                System.out.println("\n========================================");
                System.out.println("     PRODUÇÃO CONCLUÍDA COM SUCESSO");
                System.out.println("========================================");
                System.out.println("Produto: " + produto.getNome());
                System.out.println("Estoque restante: "
                        + borracha.getQuantidade() + " Kg de " + borracha.getNome());
            }


            // ==============================
            // CONSULTAR ESTOQUE
            // ==============================

            else if (opcao == 2) {

                System.out.println("\nEstoque de matéria-prima:");
                System.out.println(borracha.getNome() + ": " + borracha.getQuantidade()+ " kg"
                );
            }


            // ==============================
            // SAIR
            // ==============================

            else if (opcao == 3) {
                System.out.println("");
                System.out.println("Encerrando a fábrica...");
                System.out.println("Até a próxima!");
            } 
        }

        scanner.close();
    }

}
