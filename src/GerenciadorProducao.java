import java.util.ArrayList;

public class GerenciadorProducao {

    // ATRIBUTOS
    private ArrayList<Demanda> demandas;
    private ArrayList<Produto> produtosFabricados;
    private ArrayList<Maquina> maquinas;

    private MateriaPrima materiaPrima;
    private double budget;

    private Esteira esteira1;
    private Esteira esteira2;
    private Esteira esteira3;

    // CONSTRUTOR
    public GerenciadorProducao(MateriaPrima materiaPrima, double budget, Esteira esteira1, Esteira esteira2, Esteira esteira3) {
        this.materiaPrima = materiaPrima;
        this.budget = budget;

        this.demandas = new ArrayList<>();
        this.produtosFabricados = new ArrayList<>();
        this.maquinas = new ArrayList<>();

        this.esteira1 = esteira1;
        this.esteira2 = esteira2;
        this.esteira3 = esteira3;
    }

    // ===========================
    // MÉTODOS
    // ===========================

    // ADICIONAR MÁQUINA
    public void adicionarMaquina(Maquina maquina) {
        maquinas.add(maquina);
    }

    // REGISTRAR DEMANDA
    public void registrarDemanda(Demanda demanda) {
        demandas.add(demanda);
        System.out.println("[OK] Demanda registrada para " + demanda.getTipoProduto() + ".");
    }

    // ATUALIZAR DEMANDA
    public void atualizarDemanda(String tipoProduto, int quantidade) {
        for (Demanda demanda : demandas) { // Para cada Demanda existente dentro da lista demandas, chame de demanda
            if (demanda.getTipoProduto().equals(tipoProduto)) { // Verifica se o tipo dessa demanda é igual ao tipo de produto pedido pelo usuário
                demanda.atualizarQuantidade(quantidade);
                System.out.println("[OK] Demanda de "+ tipoProduto + " atualizada para " + quantidade + " unidade(s).");

                return;
            }
        }

        // Se a demanda ainda não existir, cria uma nova
        Demanda novaDemanda = new Demanda(tipoProduto, quantidade);
        demandas.add(novaDemanda);

        System.out.println("[OK] Nova demanda de " + tipoProduto + " registrada.");
    }


    // FABRICAR DEMANDA
    public void fabricarDemanda(String tipoProduto) {
        
        Demanda demandaEncontrada = null; // Cria uma variável inicialmente nula

        // Procura a demanda dentro do arraylist demandas
        for (Demanda demanda : demandas) {
            if (demanda.getTipoProduto().equals(tipoProduto)) {
                demandaEncontrada = demanda;
                break;
            }
        }

        // Se não encontrar nenhuma demanda
        if (demandaEncontrada == null) {
            System.out.println("[ERRO] Não existe demanda para " + tipoProduto + ".");
            return;
        }

        int quantidade = demandaEncontrada.getQuantidadeProdutos(); // Quantidade de produtos a serem fabricados

        if (quantidade <= 0) {
            System.out.println("[ERRO] A demanda está vazia.");
            return;
        }

        Produto modelo = criarProduto(tipoProduto, 0);

        if (modelo == null) { // Verifica se existe o produto
            System.out.println("[ERRO] Tipo de produto inválido.");
            return;
        }

        // Calcula a matéria-prima necessária
        double materiaNecessaria = demandaEncontrada.calcularMateriaPrimaNecessaria(modelo);

        // Verifica se essa matéria prima está disponível
        if (!materiaPrima.verificarDisponibilidade(materiaNecessaria)) {
            System.out.println("[ERRO] Matéria-prima insuficiente.");
            System.out.println("Necessário: " + materiaNecessaria + materiaPrima.getUnidade());
            return;
        }

        // Verifica capacidade das máquinas
        if (!verificarCapacidadeMaquinas(materiaNecessaria)) {
            return;
        }

        if (!verificarCapacidadeEsteiras(materiaNecessaria)) {
            return;
        }

        // Calcula o custo total
        double custoTotal = calcularCustoProducao(quantidade);

        if (custoTotal > budget) {
            System.out.println("[ERRO] Budget insuficiente.");
            System.out.println("Custo necessário: R$ " + String.format("%.2f", custoTotal));
            System.out.println("Budget disponível: R$ " + String.format("%.2f", budget));
            return;
        }
 
        System.out.println("[OK] Iniciando produção de " + quantidade + " " + tipoProduto);

        // Consome a matéria-prima da demanda
        materiaPrima.consumir(materiaNecessaria);

        int fabricadosComSucesso = 0;

        // Fabrica cada produto individualmente
        for (int i = 0; i < quantidade; i++) {
            System.out.println("\n--- Produto "+ (i + 1)+ " de "+ quantidade+ " ---");

            Produto produto = criarProduto(tipoProduto,Produto.getTotalProdutosFabricados()+ i + 1);
            boolean sucesso = processarProduto(produto);

            if (sucesso) {
                produtosFabricados.add(produto);
                produto.registrarFabricacao();
                fabricadosComSucesso++;
                System.out.println("[OK] Produto armazenado.");
            }
        }

        // Desconta o custo da produção
        budget -= custoTotal;

        // Se todos foram produzidos
        if (fabricadosComSucesso == quantidade) {
            demandaEncontrada.atender();
            System.out.println("\n[OK] Demanda atendida!");
        } 
        else {
            demandaEncontrada.atualizarQuantidade(quantidade - fabricadosComSucesso);
            System.out.println("\n[OK] "+ fabricadosComSucesso+ " produto(s) fabricado(s).");
        }

        System.out.println("Quantidade de produtos fabricados: " + produtosFabricados.size());
        System.out.println("Materia prima restante: " + materiaPrima.getQuantidade() + " Kg");
        System.out.println("Budget restante: R$ "+ budget);
        System.out.println("Custo de necessário: " + custoTotal);
    }

    // PROCESSAR PRODUTO PELA MÁQUINA MOLDAGEM
    private boolean processarProduto(Produto produto) {

        // Início
        esteira1.ligar();
        esteira1.adicionarItem(materiaPrima);
        esteira1.caminhoEsteira();
        Object materiaTransportada = esteira1.removerItem();
        esteira1.desligar();

        if (materiaTransportada == null) {
            System.out.println("[ERRO] A matéria-prima não chegou à ");
            return false;
        }

        // Moldagem
        MaquinaMoldagem moldadora = null;
        for (Maquina maquina : maquinas) {
            if (maquina instanceof MaquinaMoldagem) {
                moldadora = (MaquinaMoldagem) maquina;
                break;
            }
        }

        if (moldadora == null) {
            System.out.println("[ERRO] Máquina de moldagem não encontrada.");
            return false;
        }

        moldadora.ligar();
        Produto resultado = moldadora.processar(produto);
        moldadora.desligar();

        if (resultado == null) {
            System.out.println("[ERRO] Produto não pôde ser moldado.");
            return false;
        }


        // Embalagem
        esteira2.ligar();

        if (!esteira2.adicionarItem(resultado)) {
            esteira2.desligar();
            return false;
        }

        esteira2.caminhoEsteira();
        Object produtoTransportado = esteira2.removerItem();
        esteira2.desligar();

        if (produtoTransportado == null) {
            System.out.println("[ERRO] O produto não chegou à embaladora.");
            return false;
        }

        MaquinaEmbalagem embaladora = null;

        for (Maquina maquina : maquinas) {
            if (maquina instanceof MaquinaEmbalagem) {
                embaladora = (MaquinaEmbalagem) maquina;
                break;
            }
        }

        if (embaladora == null) {
            System.out.println("[ERRO] Máquina de embalagem não encontrada.");
            return false;
        }

        embaladora.ligar();
        resultado = embaladora.processar((Produto) produtoTransportado);
        embaladora.desligar();

        if (resultado == null) {
            System.out.println("[ERRO] Produto não pôde ser embalado.");
            return false;
        }


        // Inspeção
        esteira3.ligar();

        if (!esteira3.adicionarItem(resultado)) {
            esteira3.desligar();
            return false;
        }

        esteira3.caminhoEsteira();
        Object produtoInspecao = esteira3.removerItem();
        esteira3.desligar();

        if (produtoInspecao == null) {
            System.out.println("[ERRO] O produto não chegou à inspeção.");
            return false;
        }

        MaquinaInspecao inspecao = null;

        for (Maquina maquina : maquinas) {
            if (maquina instanceof MaquinaInspecao) {
                inspecao = (MaquinaInspecao) maquina;
                break;
            }
        }

        if (inspecao == null) {
            System.out.println("[ERRO] Máquina de inspeção não encontrada.");
            return false;
        }

        inspecao.ligar();
        resultado = inspecao.processar((Produto) produtoInspecao);
        inspecao.desligar();

        if (resultado == null) {
            System.out.println("[ERRO] Produto foi reprovado na inspeção.");
            return false;
        }

        System.out.println("[OK] Produto passou por toda a linha de produção!");

        return true;
    }

    // CRIA O PRODUTO PEDIDO PELO CLIENTE
    private Produto criarProduto(String tipoProduto, int id) {
        if (tipoProduto.equals("Pato Pequeno")) {
            return new PatoPequeno(id);
        } 
        else if (tipoProduto.equals("Pato Médio")) {
            return new PatoMedio(id);
        } 
        else if (tipoProduto.equals("Pato Grande")) {
            return new PatoGrande(id);
        }

        return null;
    }


    // VERIFICA CAPACIDADE DAS MÁQUINAS
    private boolean verificarCapacidadeMaquinas(double quantidade) {
        for (Maquina maquina : maquinas) {
            if (!maquina.verificarCapacidadeMaquina(quantidade)) {
                System.out.println("[ERRO] A demanda ultrapassa "+ "a capacidade da "+ maquina.getNome()+ ".");
                return false;
            }
        }

        return true;
    }

    // VERIFICAR CAPACIDADE DA ESTEIRA
    private boolean verificarCapacidadeEsteiras(double quantidade) {
        if (!esteira1.verificarCapacidade(quantidade)) {
            System.out.println("[ERRO] A quantidade ultrapassa a capacidade da Esteira 1.");
            return false;
        }

        if (!esteira2.verificarCapacidade(quantidade)) {
            System.out.println("[ERRO] A quantidade ultrapassa a capacidade da Esteira 2.");
            return false;
        }

        if (!esteira3.verificarCapacidade(quantidade)) {
            System.out.println("[ERRO] A quantidade ultrapassa a capacidade da Esteira 3.");
            return false;
        }

    return true;
}

    // Função para calcular o custo de produção
    private double calcularCustoProducao(int quantidade) {

        double custoMaquinas = 0.0;

        for (Maquina maquina : maquinas) {
            custoMaquinas += maquina.getCustoOperacao();
        }
        return custoMaquinas * quantidade;
    }

    // COMPRA MATÉRIA-PRIMA
    public void comprarMateriaPrima(double quantidade) {
        if (quantidade <= 0) {
            System.out.println("[ERRO] Quantidade inválida.");
            return;
        }

        double custo = quantidade * materiaPrima.getCustoPorUnidade();

        if (custo > budget) {
            System.out.println("[ERRO] Budget insuficiente para a compra.");
            System.out.println("Custo: R$ " + String.format("%.2f", custo));
            return;
        }

        materiaPrima.adicionarEstoque(quantidade);
        budget -= custo;

        System.out.println("[OK] Compra realizada.");
        System.out.println("Valor pago: R$ "+ String.format("%.2f", custo));
        System.out.println("Budget restante: R$ "+ String.format("%.2f", budget));
    }

    // Exibe o budget
    public void exibirBudget() {
        System.out.println("\n========================================");
        System.out.println("             BUDGET");
        System.out.println("========================================");
        System.out.println("Budget atual: R$ "+ String.format("%.2f", budget));
    }

    // Exibe o armazém
    public void exibirArmazem() {

        System.out.println("\n========================================");
        System.out.println("             ARMAZÉM");
        System.out.println("========================================");

        if (produtosFabricados.isEmpty()) {
            System.out.println("O armazém está vazio.");
            return;
        }

        for (Produto produto : produtosFabricados) {
            System.out.println("- "+ produto.getNome() + " " + produto.getId() + " | Status: "+ produto.getStatus()+ " | Qualidade: "+ produto.getQualidade());
        }

        System.out.println("\nTotal de produtos armazenados: "+ produtosFabricados.size());
    }

    // EXIBE ESTOQUE DE MATÉRIA-PRIMA
    public void exibirEstoqueMateriaPrima() {

        System.out.println("\n========================================");
        System.out.println("       ESTOQUE DE MATÉRIA-PRIMA");
        System.out.println("========================================");
        System.out.println(materiaPrima.getNome()+ ": "+ materiaPrima.getQuantidade()+ " "+ materiaPrima.getUnidade());
    }

    public double getBudget() {
        return budget;
    }
}