import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // PLANTA INDUSTRIAL

        // Matéria-prima disponível na fábrica
        MateriaPrima borracha = new MateriaPrima(
                1,
                "Borracha",
                100.0,
                " kg ",
                5.0
        );

        // Produtos que podem ser fabricados
        Produto patoPequeno = new Produto(1, "Patinho Pequeno", 5);
        patoPequeno.definirDemandaMateriaPrima(5.0);

        Produto patoMedio = new Produto(2, "Patinho Médio", 10);
        patoMedio.definirDemandaMateriaPrima(10.0);

        Produto patoGrande = new Produto(3, "Patinho Grande", 20);
        patoGrande.definirDemandaMateriaPrima(20.0);

        // Máquina de processamento
        Maquina maquina = new Maquina("Moldadora de Patinhos", 30.0);

        // Esteira
        Esteira esteira = new Esteira(30.0);

        // Estação de inspeção
        EstacaoInspecao estacao = new EstacaoInspecao();


        // TELA INICIAL
        System.out.println("========================================");
        System.out.println("       FÁBRICA DE PATINHOS");
        System.out.println("========================================");
        System.out.println("Bem-vindo à nossa fábrica!");
        System.out.println("Aqui transformamos borracha em");
        System.out.println("divertidos patinhos de borracha.");
        System.out.println("========================================");


        // MENU PRINCIPAL
        int opcao = 0;

        while (opcao != 3) {

            System.out.println();
            System.out.println("========================================");
            System.out.println("          MENU PRINCIPAL");
            System.out.println("========================================");
            System.out.println("1 - Iniciar produção");
            System.out.println("2 - Consultar estoque");
            System.out.println("3 - Sair");
            System.out.print("Escolha: ");

            // Garante que o usuário digite um número inteiro
            while (!scanner.hasNextInt()) {
                System.out.println("[ERRO] Digite apenas números.");
                scanner.next();
                System.out.print("Escolha: ");
            }

            opcao = scanner.nextInt();

  
            // OPÇÃO 1 - PRODUÇÃO
            if (opcao == 1) {

                System.out.println();
                System.out.println("========================================");
                System.out.println("          PRODUTOS DISPONÍVEIS");
                System.out.println("========================================");
                System.out.println("1 - Patinho Pequeno (" 
                        + patoPequeno.getDemandaMateriaPrima() + " kg)");
                System.out.println("2 - Patinho Médio (" 
                        + patoMedio.getDemandaMateriaPrima() + " kg)");
                System.out.println("3 - Patinho Grande (" 
                        + patoGrande.getDemandaMateriaPrima() + " kg)");

                System.out.print("Selecione o produto: ");

                while (!scanner.hasNextInt()) {
                    System.out.println("[ERRO] Digite apenas números.");
                    scanner.next();
                    System.out.print("Selecione o produto: ");
                }

                int escolhaProduto = scanner.nextInt();

                Produto produtoEscolhido = null;

                if (escolhaProduto == 1) {
                    produtoEscolhido = patoPequeno;
                } else if (escolhaProduto == 2) {
                    produtoEscolhido = patoMedio;
                } else if (escolhaProduto == 3) {
                    produtoEscolhido = patoGrande;
                } else {
                    System.out.println("[ERRO] Produto inválido.");
                    continue;
                }


                
                // DEMANDA
                System.out.println();
                System.out.println("Produto escolhido: "
                        + produtoEscolhido.getNome());

                System.out.print("Informe a demanda de borracha (kg): ");

                while (!scanner.hasNextDouble()) {
                    System.out.println("[ERRO] Digite apenas números.");
                    scanner.next();
                    System.out.print("Informe a demanda de borracha (kg): ");
                }

                double demanda = scanner.nextDouble();


                // VERIFICAÇÕES
                if (demanda <= 0) {
                    System.out.println("[ERRO] A demanda deve ser maior que zero.");
                    continue;
                }

                System.out.println();
                System.out.println("[OK] Verificando disponibilidade de borracha...");
                System.out.println("[OK] Quantidade necessária disponível! Estoque atual: " + borracha.getQuantidade() +" Kg " + borracha.getNome());

                if (!borracha.verificarDisponibilidade(demanda)) {
                    System.out.println("[ERRO] Borracha insuficiente.");
                    System.out.println("Estoque atual: "
                            + borracha.getQuantidade() + borracha.getNome());
                    continue;
                }

                if (!maquina.estaLigada()) {
                    maquina.ligar();
                }

                if (!esteira.verificarCapacidade(demanda)) {
                    System.out.println("[ERRO] A demanda ultrapassa a capacidade da esteira.");
                    continue;
                }

                // Define a demanda do produto
                produtoEscolhido.definirDemandaMateriaPrima(demanda);


                // TRANSPORTE DA MATÉRIA-PRIMA
                esteira.ligar();

                System.out.println();
                System.out.println("[OK] Colocando borracha na esteira...");

                if (!esteira.adicionarItem(borracha)) {
                    System.out.println("[ERRO] Não foi possível colocar a borracha na esteira.");
                    continue;
                }

                System.out.println("[OK] Borracha transportada até a máquina.");

                MateriaPrima materiaPrimaTransportada =
                        (MateriaPrima) esteira.removerItem();



                // PROCESSAMENTO
                Produto produtoProcessado = maquina.processar(
                        materiaPrimaTransportada,
                        produtoEscolhido,
                        demanda
                );

                if (produtoProcessado == null) {
                    continue;
                }


                // TRANSPORTE DO PRODUTO
                System.out.println();
                System.out.println("[OK] Colocando produto na esteira...");

                if (!esteira.adicionarItem(produtoProcessado)) {
                    System.out.println("[ERRO] Não foi possível colocar o produto na esteira.");
                    continue;
                }

                System.out.println("[OK] Produto transportado para a inspeção.");

                Produto produtoParaInspecao =
                        (Produto) esteira.removerItem();


                // INSPEÇÃO
                estacao.ativar();

                estacao.inspecionar(produtoParaInspecao);


        
                // FINALIZAÇÂO
                System.out.println();
                System.out.println("========================================");
                System.out.println("     PRODUÇÃO CONCLUÍDA COM SUCESSO!");
                System.out.println("========================================");
                System.out.println("Produto: " + produtoProcessado.getNome());
                System.out.println("Estoque restante: "
                        + borracha.getQuantidade() + borracha.getNome());
                System.out.println("========================================");

            }


            // OPÇÃO 2 - ESTOQUE
            else if (opcao == 2) {

                System.out.println();
                System.out.println("========================================");
                System.out.println("             ESTOQUE");
                System.out.println("========================================");
                System.out.println("Matéria-prima: " + borracha.getNome());
                System.out.println("Quantidade: "
                        + borracha.getQuantidade() + " Kg "+ borracha.getNome());
                System.out.println("========================================");

            }



            // OPÇÃO 3 - SAIR
            else if (opcao == 3) {

                System.out.println();
                System.out.println("Encerrando a fábrica...");
                System.out.println("Até a próxima!");

            }


        
            // OPÇÃO INVÁLIDA
            else {

                System.out.println("[ERRO] Opção inválida.");

            }
        }

        scanner.close();
    }
}