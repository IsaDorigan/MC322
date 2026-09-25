import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Coordena a fábrica de patinhos. No padrão Strategy ele é o CONTEXTO: não decide sozinho
 * qual demanda produzir, delega essa escolha ao objeto EstrategiaProducao atual e só conhece a interface.
 */
public class GerenciadorProducao {

    // ATRIBUTOS
    private ArrayList<Demanda> demandas;
    private ArrayList<Produto> produtosFabricados;
    private ArrayList<Maquina> maquinas;
    private ArrayList<Esteira> esteiras;  // esteiras[i] leva a carga até maquinas[i]

    private MateriaPrima materiaPrima;
    private double budget;
    private Cenario cenario;
    private EstrategiaProducao estrategiaAtual;

    private int proximoLote = 1;
    private int proximoIdProduto = 1;

    // CONSTRUTOR
    public GerenciadorProducao(MateriaPrima materiaPrima, Cenario cenario,
                               Esteira esteira1, Esteira esteira2, Esteira esteira3,
                               EstrategiaProducao estrategiaInicial) {
        this.materiaPrima = materiaPrima;
        this.cenario = cenario;
        this.budget = cenario.getOrcamentoInicial();
        this.estrategiaAtual = estrategiaInicial;

        this.demandas = new ArrayList<>();
        this.produtosFabricados = new ArrayList<>();
        this.maquinas = new ArrayList<>();

        this.esteiras = new ArrayList<>();
        this.esteiras.add(esteira1);
        this.esteiras.add(esteira2);
        this.esteiras.add(esteira3);
    }

    // ===========================
    // CONFIGURAÇÃO
    // ===========================

    // As máquinas devem ser adicionadas na ordem da linha: moldagem, embalagem, inspeção
    public void adicionarMaquina(Maquina maquina) {
        maquinas.add(maquina);
    }

    // Troca a estratégia em tempo de execução
    public void setEstrategia(EstrategiaProducao novaEstrategia) {
        if (novaEstrategia == null) {
            System.out.println("[ERRO] Estratégia inválida.");
            return;
        }
        this.estrategiaAtual = novaEstrategia;
        System.out.println("[OK] Estratégia alterada para: " + novaEstrategia.getNomeEstrategia());
    }

    // ===========================
    // DEMANDAS
    // ===========================

    // REGISTRAR DEMANDA
    public void registrarDemanda(Demanda demanda) {
        sincronizarEstimativas(demanda);
        demandas.add(demanda);
        System.out.println("[OK] Demanda registrada para " + demanda.getTipoProduto() + ".");
    }

    // ATUALIZAR DEMANDA
    public void atualizarDemanda(String tipoProduto, int quantidade) {
        if (criarProduto(tipoProduto, 0) == null) {
            System.out.println("[ERRO] Tipo de produto inválido.");
            return;
        }

        Demanda existente = buscarDemanda(tipoProduto);

        // Se a demanda ainda não existir, cria uma nova
        if (existente == null) {
            if (quantidade == 0) {
                System.out.println("[ERRO] Quantidade zero: nenhuma demanda foi criada.");
                return;
            }
            registrarDemanda(new Demanda(tipoProduto, quantidade));
            return;
        }

        boolean estavaFechada = existente.getStatus() != StatusDemanda.PENDENTE;

        if (!existente.atualizarQuantidade(quantidade)) {
            return;
        }

        // Uma demanda reaberta é um pedido novo: vai para o fim da fila (ordem de chegada)
        if (estavaFechada && existente.getStatus() == StatusDemanda.PENDENTE) {
            demandas.remove(existente);
            demandas.add(existente);
        }

        sincronizarEstimativas(existente);

        if (existente.getStatus() == StatusDemanda.CANCELADA) {
            System.out.println("[OK] Demanda de " + tipoProduto + " cancelada (quantidade zero).");
        } else {
            System.out.println("[OK] Demanda de " + tipoProduto + " atualizada para " + quantidade + " unidade(s).");
        }
    }

    private Demanda buscarDemanda(String tipoProduto) {
        for (Demanda demanda : demandas) {
            if (demanda.getTipoProduto().equals(tipoProduto)) {
                return demanda;
            }
        }
        return null;
    }

    // Preenche na demanda o consumo e o custo unitários (o produto e as máquinas é que sabem esses valores)
    private void sincronizarEstimativas(Demanda demanda) {
        Produto modelo = criarProduto(demanda.getTipoProduto(), 0);
        if (modelo != null) {
            demanda.atualizarEstimativas(modelo.getQuantidadeMateriaPrimaPorUnidade(),
                    calcularCustoOperacaoPorUnidade());
        }
    }

    // ===========================
    // PRODUÇÃO
    // ===========================

    // Usa a ESTRATÉGIA ATUAL para escolher a demanda e fabricá-la
    public void executarProximaProducao() {
        if (estrategiaAtual == null) {
            System.out.println("[ERRO] Nenhuma estratégia de produção definida.");
            return;
        }

        for (Demanda demanda : demandas) {
            sincronizarEstimativas(demanda);
        }

        // A lista é entregue somente para leitura: a estratégia apenas decide, não altera nada
        Demanda escolhida = estrategiaAtual.selecionarDemanda(Collections.unmodifiableList(demandas), budget);

        if (escolhida == null) {
            System.out.println("[ERRO] A estratégia " + estrategiaAtual.getNomeEstrategia()
                    + " não encontrou nenhuma demanda elegível.");
            return;
        }

        System.out.println("[ESTRATÉGIA] " + estrategiaAtual.getNomeEstrategia() + " escolheu: "
                + escolhida.getTipoProduto() + " (" + escolhida.getQuantidadeProdutos() + " unidade(s)).");

        produzirDemanda(escolhida);
    }

    // FABRICAR DEMANDA de um tipo específico (escolhido pelo usuário)
    public void fabricarDemanda(String tipoProduto) {
        Demanda demanda = buscarDemanda(tipoProduto);

        if (demanda == null) {
            System.out.println("[ERRO] Não existe demanda para " + tipoProduto + ".");
            return;
        }

        produzirDemanda(demanda);
    }

    // Fluxo completo de produção de uma demanda
    private void produzirDemanda(Demanda demanda) {
        String tipoProduto = demanda.getTipoProduto();

        if (demanda.getStatus() != StatusDemanda.PENDENTE) {
            System.out.println("[ERRO] A demanda de " + tipoProduto + " não está pendente: "
                    + demanda.getStatus().getDescricao() + ".");
            return;
        }

        int quantidade = demanda.getQuantidadeProdutos(); // Quantidade de produtos a serem fabricados

        if (quantidade <= 0) {
            System.out.println("[ERRO] A demanda está vazia.");
            return;
        }

        Produto modelo = criarProduto(tipoProduto, 0);

        if (modelo == null) { // Verifica se existe o produto
            System.out.println("[ERRO] Tipo de produto inválido.");
            return;
        }

        if (!linhaConfigurada() || !todasMaquinasOperantes()) {
            return;
        }

        sincronizarEstimativas(demanda);

        // Consumo total estimado = quantidade x consumo unitário do produto cadastrado
        double materiaNecessaria = demanda.calcularMateriaPrimaNecessaria(modelo);

        // Verifica se essa matéria prima está disponível (respeitando a reserva mínima)
        if (!materiaPrima.verificarDisponibilidade(materiaNecessaria)) {
            System.out.println("[ERRO] Matéria-prima insuficiente.");
            System.out.println("Necessário: " + materiaNecessaria + " " + materiaPrima.getUnidade()
                    + " | Disponível para uso: " + materiaPrima.getQuantidadeDisponivel() + " " + materiaPrima.getUnidade());
            cancelarPorFaltaDeRecursos(demanda, "insumos");
            return;
        }

        // Verifica capacidade das máquinas e das esteiras
        if (!verificarCapacidadeMaquinas(materiaNecessaria) || !verificarCapacidadeEsteiras(materiaNecessaria)) {
            return;
        }

        // Viabilidade financeira
        if (!demanda.cabeNoOrcamento(budget)) {
            System.out.println("[ERRO] Budget insuficiente.");
            System.out.println("Custo necessário: R$ " + String.format("%.2f", demanda.calcularCustoEstimado()));
            System.out.println("Budget disponível: R$ " + String.format("%.2f", budget));
            cancelarPorFaltaDeRecursos(demanda, "orçamento");
            return;
        }

        int lote = proximoLote++;
        demanda.iniciarProducao();

        System.out.println("[OK] Iniciando produção de " + quantidade + " " + tipoProduto + " (Lote " + lote + ")");

        double custoUnitario = calcularCustoOperacaoPorUnidade();
        double consumoUnitario = modelo.getQuantidadeMateriaPrimaPorUnidade();
        double budgetInicial = budget;

        int fabricadosComSucesso = 0;
        boolean cancelarDemanda = false;   // parou por falta de orçamento/insumos
        String motivoParada = null;

        // Fabrica cada produto individualmente
        for (int i = 0; i < quantidade; i++) {
            System.out.println("\n--- Produto " + (i + 1) + " de " + quantidade + " ---");

            if (!todasMaquinasOperantes()) {
                motivoParada = "uma máquina quebrou";
                break;
            }

            if (custoUnitario > budget) {
                cancelarDemanda = true;
                motivoParada = "o orçamento acabou";
                break;
            }

            // O consumo é calculado a partir da demanda unitária do produto, um patinho por vez
            if (!materiaPrima.consumir(consumoUnitario)) {
                cancelarDemanda = true;
                motivoParada = "a borracha acabou";
                break;
            }

            budget -= custoUnitario;

            Produto produto = criarProduto(tipoProduto, proximoIdProduto++);
            produto.aumentarProbabilidadeFalha(cenario.getRiscoInicialProduto());
            produto.setLote(lote);

            if (processarProduto(produto)) {
                produtosFabricados.add(produto);
                produto.registrarFabricacao();
                fabricadosComSucesso++;
                System.out.println("[OK] Produto armazenado.");
            }
        }

        // Atualiza o estado da demanda de forma consistente
        demanda.registrarProduzidos(fabricadosComSucesso);

        if (cancelarDemanda) {
            demanda.cancelar();
            System.out.println("\n[CANCELADA] Produção interrompida: " + motivoParada + ". Sobraram "
                    + demanda.getQuantidadeProdutos() + " unidade(s) sem produzir.");
        } else if (demanda.getQuantidadeProdutos() == 0) {
            demanda.concluir();
            System.out.println("\n[OK] Demanda atendida! Revoada entregue.");
        } else {
            demanda.devolverParaFila();
            String motivo = (motivoParada != null) ? " (" + motivoParada + ")" : " (patinhos perdidos na linha)";
            System.out.println("\n[OK] " + fabricadosComSucesso + " produto(s) fabricado(s)" + motivo
                    + ". Faltam " + demanda.getQuantidadeProdutos() + "; a demanda voltou para a fila.");
        }

        System.out.println("Quantidade de produtos fabricados: " + produtosFabricados.size());
        System.out.println("Matéria-prima restante: " + String.format("%.2f", materiaPrima.getQuantidade())
                + " " + materiaPrima.getUnidade());
        System.out.println("Custo desta produção: R$ " + String.format("%.2f", budgetInicial - budget));
        System.out.println("Budget restante: R$ " + String.format("%.2f", budget));
    }

    private void cancelarPorFaltaDeRecursos(Demanda demanda, String recurso) {
        demanda.cancelar();
        System.out.println("[CANCELADA] Demanda de " + demanda.getTipoProduto() + " cancelada por falta de " + recurso
                + ". Reabasteça e atualize a demanda para reabri-la.");
    }

    // Leva o patinho por toda a linha: cada máquina recebe a carga da sua esteira (DRY: um único laço)
    private boolean processarProduto(Produto produto) {

        for (int i = 0; i < maquinas.size(); i++) {
            Maquina maquina = maquinas.get(i);
            Esteira esteira = esteiras.get(i);

            // A primeira esteira leva matéria-prima até a moldagem; as demais levam o produto
            Object carga = (i == 0) ? materiaPrima : produto;

            if (!transportar(esteira, carga)) {
                System.out.println("[ERRO] A carga não chegou à " + maquina.getNome() + ".");
                return false;
            }

            maquina.ligar();
            Produto resultado = maquina.processar(produto);
            maquina.desligar();

            if (resultado == null) {
                System.out.println("[ERRO] " + produto.getNome() + " não passou da etapa de " + maquina.getTipo() + ".");
                return false;
            }
        }

        System.out.println("[OK] Produto passou por toda a linha de produção!");
        return true;
    }

    // Liga a esteira, coloca a carga, percorre o caminho e retira a carga
    private boolean transportar(Esteira esteira, Object carga) {
        esteira.ligar();

        boolean sucesso = esteira.adicionarItem(carga);
        if (sucesso) {
            esteira.caminhoEsteira();
            sucesso = esteira.removerItem() != null;
        }

        esteira.desligar();
        return sucesso;
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

    // ===========================
    // VERIFICAÇÕES
    // ===========================

    // A linha precisa ter uma esteira para cada máquina
    private boolean linhaConfigurada() {
        if (maquinas.isEmpty() || maquinas.size() != esteiras.size()) {
            System.out.println("[ERRO] Linha de produção mal configurada: são necessárias "
                    + esteiras.size() + " máquinas (uma por esteira).");
            return false;
        }
        return true;
    }

    // Nenhuma máquina quebrada pode entrar na produção
    private boolean todasMaquinasOperantes() {
        boolean todasOk = true;

        for (Maquina maquina : maquinas) {
            if (maquina.estaQuebrada()) {
                System.out.println("[ERRO] " + maquina.getNome() + " está quebrada. Repare em Auditoria > Reparar máquinas.");
                todasOk = false;
            }
        }

        return todasOk;
    }

    // VERIFICA CAPACIDADE DAS MÁQUINAS
    private boolean verificarCapacidadeMaquinas(double quantidade) {
        for (Maquina maquina : maquinas) {
            if (!maquina.verificarCapacidadeMaquina(quantidade)) {
                System.out.println("[ERRO] A demanda ultrapassa a capacidade da " + maquina.getNome() + ".");
                return false;
            }
        }

        return true;
    }

    // VERIFICAR CAPACIDADE DAS ESTEIRAS
    private boolean verificarCapacidadeEsteiras(double quantidade) {
        for (Esteira esteira : esteiras) {
            if (!esteira.verificarCapacidade(quantidade)) {
                System.out.println("[ERRO] A quantidade ultrapassa a capacidade da Esteira " + esteira.getNumero() + ".");
                return false;
            }
        }

        return true;
    }

    // Custo de operação para fabricar UM patinho (soma do custo de cada máquina da linha)
    private double calcularCustoOperacaoPorUnidade() {
        double custoMaquinas = 0.0;

        for (Maquina maquina : maquinas) {
            custoMaquinas += maquina.getCustoOperacao();
        }
        return custoMaquinas;
    }

    // ===========================
    // COMPRAS E MANUTENÇÃO
    // ===========================

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
        System.out.println("Valor pago: R$ " + String.format("%.2f", custo));
        System.out.println("Budget restante: R$ " + String.format("%.2f", budget));
    }

    // Lista as máquinas numeradas (usado no menu de reparo)
    public void exibirMaquinas() {
        for (int i = 0; i < maquinas.size(); i++) {
            Maquina maquina = maquinas.get(i);
            System.out.println((i + 1) + " - " + maquina.gerarRelatorioDiagnostico()
                    + " | Reparo: R$ " + String.format("%.2f", maquina.getCustoReparo()));
        }
    }

    // Repara uma máquina (índice a partir de 0) pagando o custo do reparo
    public void repararMaquina(int indice) {
        if (indice < 0 || indice >= maquinas.size()) {
            System.out.println("[ERRO] Máquina inexistente.");
            return;
        }

        Maquina maquina = maquinas.get(indice);

        if (!maquina.precisaManutencao()) {
            System.out.println("[ERRO] " + maquina.getNome() + " ainda está em boa forma, não precisa de reparo.");
            return;
        }

        if (maquina.getCustoReparo() > budget) {
            System.out.println("[ERRO] Budget insuficiente para o reparo (R$ "
                    + String.format("%.2f", maquina.getCustoReparo()) + ").");
            return;
        }

        budget -= maquina.getCustoReparo();
        maquina.reparar();
        System.out.println("Reparo pago: R$ " + String.format("%.2f", maquina.getCustoReparo()));
        System.out.println("Budget restante: R$ " + String.format("%.2f", budget));
    }

    // ===========================
    // AUDITORIA (polimorfismo sobre Auditavel)
    // ===========================

    public void gerarAuditoriaGeral() {
        Estilo.titulo("AUDITORIA GERAL DA FÁBRICA");

        System.out.println("\n[MÁQUINAS]");
        int maquinasEmAtencao = auditar(maquinas, false);

        System.out.println("\n[PRODUTOS EM ESTOQUE QUE EXIGEM ATENÇÃO]");
        int produtosEmAtencao = auditar(produtosFabricados, true);
        if (produtosEmAtencao == 0) {
            System.out.println("Nenhum produto em risco.");
        }

        System.out.println("\n" + Estilo.linhaSimples());
        System.out.println("Componentes auditados: " + (maquinas.size() + produtosFabricados.size())
                + " (" + maquinas.size() + " máquinas, " + produtosFabricados.size() + " produtos)");
        System.out.println("Exigem atenção: " + (maquinasEmAtencao + produtosEmAtencao));
        System.out.println("Cenário: " + cenario.getNome() + " | Budget: R$ " + String.format("%.2f", budget));
    }

    public void gerarAuditoriaMaquinas() {
        Estilo.titulo("AUDITORIA DAS MÁQUINAS");
        auditar(maquinas, false);
    }

    public void gerarAuditoriaProdutos() {
        Estilo.titulo("AUDITORIA DOS PRODUTOS");
        if (produtosFabricados.isEmpty()) {
            System.out.println("Nenhum produto no armazém para auditar.");
            return;
        }
        auditar(produtosFabricados, false);
    }

    // Percorre qualquer lista de Auditavel (máquinas, produtos...) sem saber a classe concreta.
    // Retorna quantos itens exigem atenção.
    private int auditar(List<? extends Auditavel> itens, boolean somenteAtencao) {
        int emAtencao = 0;

        for (Auditavel item : itens) {
            boolean precisa = item.precisaManutencao();

            if (precisa) {
                emAtencao++;
            }

            if (precisa || !somenteAtencao) {
                String marca = precisa ? "[ATENÇÃO] " : "[OK] ";
                System.out.println("- " + marca + item.gerarRelatorioDiagnostico());
            }
        }

        return emAtencao;
    }

    // ===========================
    // EXIBIÇÕES
    // ===========================

    // Exibe o budget
    public void exibirBudget() {
        Estilo.titulo("BUDGET");
        System.out.println("Budget atual: R$ " + String.format("%.2f", budget));
    }

    // Lista as demandas, seus estados e qual seria a próxima segundo a estratégia atual
    public void exibirDemandas() {
        Estilo.titulo("DEMANDAS");

        if (demandas.isEmpty()) {
            System.out.println("Nenhuma demanda cadastrada.");
            return;
        }

        for (Demanda demanda : demandas) {
            sincronizarEstimativas(demanda);
        }

        Demanda proxima = (estrategiaAtual == null) ? null
                : estrategiaAtual.selecionarDemanda(Collections.unmodifiableList(demandas), budget);

        for (Demanda demanda : demandas) {
            String marca = (demanda == proxima) ? "  <== PRÓXIMA" : "";
            System.out.println("- " + demanda.getTipoProduto() + " | " + demanda.getQuantidadeProdutos() + " unid. | "
                    + demanda.getStatus().getDescricao()
                    + " | Custo est.: R$ " + String.format("%.2f", demanda.calcularCustoEstimado())
                    + " | Borracha est.: " + String.format("%.2f", demanda.calcularConsumoEstimado()) + " " + materiaPrima.getUnidade()
                    + marca);
        }

        if (estrategiaAtual != null) {
            System.out.println("\nEstratégia atual: " + estrategiaAtual.getNomeEstrategia());
        }
    }

    // Exibe o armazém de PRODUTOS ACABADOS, agrupado por tipo e lote
    public void exibirArmazem() {
        Estilo.titulo("ARMAZÉM - PATINHOS ACABADOS");

        if (produtosFabricados.isEmpty()) {
            System.out.println("O armazém está vazio.");
            return;
        }

        // tipo -> (lote -> produtos)
        Map<String, Map<Integer, List<Produto>>> agrupado = new LinkedHashMap<>();

        for (Produto produto : produtosFabricados) {
            agrupado.computeIfAbsent(produto.getNome(), k -> new TreeMap<>())
                    .computeIfAbsent(produto.getLote(), k -> new ArrayList<>())
                    .add(produto);
        }

        for (Map.Entry<String, Map<Integer, List<Produto>>> porTipo : agrupado.entrySet()) {
            int totalTipo = 0;
            for (List<Produto> doLote : porTipo.getValue().values()) {
                totalTipo += doLote.size();
            }

            System.out.println("\n" + Estilo.negrito(porTipo.getKey()) + " - total: " + totalTipo + " unid.");

            for (Map.Entry<Integer, List<Produto>> porLote : porTipo.getValue().entrySet()) {
                List<Produto> produtos = porLote.getValue();

                double somaQualidade = 0;
                double somaRisco = 0;
                int emRisco = 0;

                for (Produto produto : produtos) {
                    somaQualidade += produto.getQualidade();
                    somaRisco += produto.getProbabilidadeFalhaAcumulada();
                    if (produto.precisaManutencao()) {
                        emRisco++;
                    }
                }

                String statusRisco = (emRisco == 0) ? "Risco: OK" : "Risco: ATENÇÃO (" + emRisco + " em risco)";

                System.out.println("   Lote " + porLote.getKey() + " | " + produtos.size() + " unid. | Qualidade média: "
                        + String.format("%.0f%%", 100 * somaQualidade / produtos.size())
                        + " | Risco médio: " + String.format("%.0f%%", 100 * somaRisco / produtos.size())
                        + " | " + statusRisco);
            }
        }

        System.out.println("\nTotal de produtos armazenados: " + produtosFabricados.size());
    }

    // EXIBE ESTOQUE DE MATÉRIA-PRIMA (insumos brutos, diferente do armazém)
    public void exibirEstoqueMateriaPrima() {
        Estilo.titulo("ESTOQUE DE MATÉRIA-PRIMA");
        System.out.println(materiaPrima.getNome() + ": " + String.format("%.2f", materiaPrima.getQuantidade())
                + " " + materiaPrima.getUnidade());
        System.out.println("Reserva mínima: " + String.format("%.2f", materiaPrima.getQuantidadeMinima())
                + " " + materiaPrima.getUnidade() + " | Disponível para produção: "
                + String.format("%.2f", materiaPrima.getQuantidadeDisponivel()) + " " + materiaPrima.getUnidade());
        System.out.println("Preço de compra: R$ " + String.format("%.2f", materiaPrima.getCustoPorUnidade())
                + " por " + materiaPrima.getUnidade());
    }

    // GETTERS
    public double getBudget() {
        return budget;
    }

    public Cenario getCenario() {
        return cenario;
    }

    public EstrategiaProducao getEstrategiaAtual() {
        return estrategiaAtual;
    }

    public int getQuantidadeMaquinas() {
        return maquinas.size();
    }

    public MateriaPrima getMateriaPrima() {
        return materiaPrima;
    }
}