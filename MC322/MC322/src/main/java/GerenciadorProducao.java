import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.Auditavel;
import model.Demanda;
import model.Esteira;
import model.MateriaPrima;
import model.PatoGrande;
import model.PatoMedio;
import model.PatoPequeno;
import model.Produto;
import model.enums.Cenario;
import model.enums.StatusDemanda;
import model.exceptions.ArgumentoIlegalException;
import model.exceptions.DemandaInexistenteException;
import model.exceptions.MaquinaIndisponivelException;
import model.exceptions.OrcamentoInsuficienteException;
import model.exceptions.RecursoInsuficienteException;
import model.exceptions.StatusIlegalException;
import model.maquina.Maquina;
import strategy.EstrategiaProducao;
import util.Estilo;


/**
 * Coordena a fábrica de patinhos. No padrão Strategy ele é o CONTEXTO: não decide sozinho
 * qual demanda produzir, delega essa escolha ao objeto EstrategiaProducao atual e só conhece a interface.
 * 
 * @author Isadora KLuge Dorigan e Guilherme Forte Silva
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

    /**
     * Inicializa o gerenciador de produção com o cenário, insumos, esteiras e a estratégia inicial definida
     *
     * @param materiaPrima      A matéria-prima que será consumida na fabricação
     * @param cenario           O cenário atual que define orçamento e dificuldades
     * @param esteira1          A primeira esteira da linha de produção
     * @param esteira2          A segunda esteira da linha de produção
     * @param esteira3          A terceira esteira da linha de produção
     * @param estrategiaInicial A estratégia inicial adotada para seleção de demandas
     */
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

    /**
     * Adiciona uma nova máquina a linha de produção, que segue a ordem de moldagem, embalagem e inspeção
     *
     * @param maquina A maquina a ser inserida na linha de produção
     */
    public void adicionarMaquina(Maquina maquina) {
        maquinas.add(maquina);
    }

     
    /**
     * Troca a estratégia em tempo de execução
     * 
     * @param novaEstrategia A nova estratégia de seleção de demandas a ser aplicada
     * @throws ArgumentoIlegalException Se a estratégia informada for nula
     */
    public void setEstrategia(EstrategiaProducao novaEstrategia) {
        if (novaEstrategia == null) {
            throw new ArgumentoIlegalException("Estrategia invalida: nenhuma estrategia foi informada.");
        }
        this.estrategiaAtual = novaEstrategia;
        System.out.println("[OK] Estrategia alterada para: " + novaEstrategia.getNomeEstrategia());
    }

    // ===========================
    // DEMANDAS
    // ===========================

    /**
     * Registra uma nova demanda no sistema e sincroniza suas estimativas
     *
     * @param demanda A demanda a ser adicionada na lista
     */
    public void registrarDemanda(Demanda demanda) {
        sincronizarEstimativas(demanda);
        demandas.add(demanda);
        System.out.println("[OK] Demanda registrada para " + demanda.getTipoProduto() + ".");
    }

    /**
     * Atualiza a quantidade de uma demanda existente ou cria uma nova caso não exista.
     * Se uma demanda concluída ou cancelada for reaberta, ela vai para o fim da fila
     *
     * @param tipoProduto O nome do tipo de produto da demanda
     * @param quantidade  A nova quantidade desejada para a demanda
     * @throws ArgumentoIlegalException Se o tipo de produto for inválido, se a quantidade for negativa
     *                                  ou se for zero para uma demanda que ainda não existe
     * @throws StatusIlegalException    Se a demanda já estiver em produção
     */
    public void atualizarDemanda(String tipoProduto, int quantidade) {
        if (criarProduto(tipoProduto, 0) == null) {
            throw new ArgumentoIlegalException("Tipo de produto invalido: " + tipoProduto + ".");
        }

        Demanda existente = buscarDemanda(tipoProduto);

        // Se a demanda ainda não existir, cria uma nova
        if (existente == null) {
            if (quantidade == 0) {
                throw new ArgumentoIlegalException("Quantidade zero: nenhuma demanda foi criada para " + tipoProduto + ".");
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

    /**
     * Busca uma demanda específica na lista pelo seu tipo de produto
     *
     * @param tipoProduto O tipo de produto que identifica a demanda
     * @return            A demanda encontrada ou null se não houver registro para este produto
     */
    private Demanda buscarDemanda(String tipoProduto) {
        for (Demanda demanda : demandas) {
            if (demanda.getTipoProduto().equals(tipoProduto)) {
                return demanda;
            }
        }
        return null;
    }

    /**
     * Atualiza as estimativas de custo e consumo da demanda com base nos valores vigentes das máquinas e produtos
     *
     * @param demanda A demanda que terá seus valores estimados atualizados
     */
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

    /**
     * Inicia o processo de fabricação da próxima demanda elegível selecionada automaticamente pela estratégia atual
     *
     * @throws StatusIlegalException          Se não houver estratégia definida
     * @throws DemandaInexistenteException    Se a estratégia não encontrar nenhuma demanda elegível
     * @throws MaquinaIndisponivelException   Se a linha estiver mal configurada ou houver máquina quebrada
     * @throws RecursoInsuficienteException   Se faltar matéria-prima ou a carga exceder a capacidade da linha
     * @throws OrcamentoInsuficienteException Se o budget não cobrir o custo da demanda escolhida
     */
    public void executarProximaProducao() {
        if (estrategiaAtual == null) {
            throw new StatusIlegalException("Nenhuma estrategia de producao definida.");
        }

        for (Demanda demanda : demandas) {
            sincronizarEstimativas(demanda);
        }

        // A lista é entregue somente para leitura: a estratégia apenas decide, não altera nada
        Demanda escolhida = estrategiaAtual.selecionarDemanda(Collections.unmodifiableList(demandas), budget);

        if (escolhida == null) {
            throw new DemandaInexistenteException("A estrategia " + estrategiaAtual.getNomeEstrategia()
                    + " nao encontrou nenhuma demanda elegivel.");
        }

        System.out.println("[ESTRATEGIA] " + estrategiaAtual.getNomeEstrategia() + " escolheu: "
                + escolhida.getTipoProduto() + " (" + escolhida.getQuantidadeProdutos() + " unidade(s)).");

        produzirDemanda(escolhida);
    }

    /**
     * Inicia manualmente a produção de uma demanda específica escolhida pelo usuário, ignorando a estratégia
     *
     * @param tipoProduto O tipo do produto da demanda que será fabricada
     * @throws DemandaInexistenteException    Se não existir demanda para o tipo informado
     * @throws StatusIlegalException          Se a demanda não estiver pendente
     * @throws MaquinaIndisponivelException   Se a linha estiver mal configurada ou houver máquina quebrada
     * @throws RecursoInsuficienteException   Se faltar matéria-prima ou a carga exceder a capacidade da linha
     * @throws OrcamentoInsuficienteException Se o budget não cobrir o custo da demanda
     */
    public void fabricarDemanda(String tipoProduto) {
        Demanda demanda = buscarDemanda(tipoProduto);

        if (demanda == null) {
            throw new DemandaInexistenteException("Nao existe demanda para " + tipoProduto
                    + ". Crie uma no menu Demandas.");
        }

        produzirDemanda(demanda);
    }

    /**
     * Executa o fluxo completo de produção para uma demanda verificando recursos, capacidade e viabilidade financeira
     *
     * @param demanda A demanda que terá seus produtos fabricados
     * @throws StatusIlegalException          Se a demanda não estiver pendente ou estiver vazia
     * @throws ArgumentoIlegalException       Se o tipo de produto da demanda for inválido
     * @throws MaquinaIndisponivelException   Se a linha estiver mal configurada ou houver máquina quebrada
     * @throws RecursoInsuficienteException   Se faltar matéria-prima ou a carga exceder a capacidade da linha
     * @throws OrcamentoInsuficienteException Se o budget não cobrir o custo estimado da demanda
     */
    private void produzirDemanda(Demanda demanda) {
        String tipoProduto = demanda.getTipoProduto();

        if (demanda.getStatus() != StatusDemanda.PENDENTE) {
            throw new StatusIlegalException("A demanda de " + tipoProduto + " nao esta pendente: "
                    + demanda.getStatus().getDescricao() + ".");
        }

        int quantidade = demanda.getQuantidadeProdutos(); // Quantidade de produtos a serem fabricados

        if (quantidade <= 0) {
            throw new StatusIlegalException("A demanda de " + tipoProduto + " esta vazia.");
        }

        Produto modelo = criarProduto(tipoProduto, 0);

        if (modelo == null) { // Verifica se existe o produto
            throw new ArgumentoIlegalException("Tipo de produto invalido: " + tipoProduto + ".");
        }

        // Todas as validações abaixo acontecem ANTES de qualquer alteração de estado (estoque, budget, status)
        validarLinhaConfigurada();
        validarMaquinasOperantes();

        sincronizarEstimativas(demanda);

        // Consumo total estimado = quantidade x consumo unitário do produto cadastrado
        double materiaNecessaria = demanda.calcularMateriaPrimaNecessaria(modelo);

        // Verifica se essa matéria prima está disponível (respeitando a reserva mínima)
        if (!materiaPrima.verificarDisponibilidade(materiaNecessaria)) {
            String aviso = cancelarPorFaltaDeRecursos(demanda, "insumos");
            throw new RecursoInsuficienteException("Materia-prima insuficiente para fabricar a demanda.\n"
                    + "Necessario: " + String.format("%.2f", materiaNecessaria) + " " + materiaPrima.getUnidade()
                    + " | Disponivel para uso: " + String.format("%.2f", materiaPrima.getQuantidadeDisponivel()) + " " + materiaPrima.getUnidade()
                    + "\n" + aviso + "\nNenhuma materia-prima foi consumida.");
        }

        // Verifica capacidade das máquinas e das esteiras
        validarCapacidadeMaquinas(materiaNecessaria);
        validarCapacidadeEsteiras(materiaNecessaria);

        // Viabilidade financeira
        if (!demanda.cabeNoOrcamento(budget)) {
            String aviso = cancelarPorFaltaDeRecursos(demanda, "orçamento");
            throw new OrcamentoInsuficienteException("Budget insuficiente para fabricar a demanda.\n"
                    + "Custo necessario: R$ " + String.format("%.2f", demanda.calcularCustoEstimado())
                    + " | Budget disponivel: R$ " + String.format("%.2f", budget)
                    + "\n" + aviso + "\nNenhum recurso foi consumido.");
        }

        int lote = proximoLote++;
        demanda.iniciarProducao();

        System.out.println("[OK] Iniciando producao de " + quantidade + " " + tipoProduto + " (Lote " + lote + ")");

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
                motivoParada = "uma maquina quebrou";
                break;
            }

            if (custoUnitario > budget) {
                cancelarDemanda = true;
                motivoParada = "o orcamento acabou";
                break;
            }

            // O consumo é calculado a partir da demanda unitária do produto, um patinho por vez
            try {
                materiaPrima.consumir(consumoUnitario);
            } catch (RecursoInsuficienteException e) {
                // O consumo é atômico (o estoque não foi alterado): encerra o lote e preserva o que já foi produzido
                System.out.println("[ERRO] " + e.getMessage());
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
            System.out.println("\n[CANCELADA] Producao interrompida: " + motivoParada + ". Sobraram "
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
        System.out.println("Materia-prima restante: " + String.format("%.2f", materiaPrima.getQuantidade())
                + " " + materiaPrima.getUnidade());
        System.out.println("Custo desta producao: R$ " + String.format("%.2f", budgetInicial - budget));
        System.out.println("Budget restante: R$ " + String.format("%.2f", budget));
    }

    /**
     * Cancela uma demanda e devolve um aviso informando qual recurso motivou o cancelamento
     *
     * @param demanda A demanda que será cancelada
     * @param recurso O nome do recurso que faltou (ex: material ou orçamento)
     * @return        O aviso de cancelamento, para ser exibido junto com a mensagem de erro
     */
    private String cancelarPorFaltaDeRecursos(Demanda demanda, String recurso) {
        demanda.cancelar();
        return "[CANCELADA] Demanda de " + demanda.getTipoProduto() + " cancelada por falta de " + recurso
                + ". Reabasteça e atualize a demanda para reabri-la.";
    }

    /**
     * Leva um único produto por toda a linha de produção, passando sequencialmente por todas as máquinas e esteiras
     *
     * @param produto O produto que será processado pela linha
     * @return        true se o produto passar por toda a linha com sucesso, false caso falhe em alguma etapa
     */
    private boolean processarProduto(Produto produto) {

        for (int i = 0; i < maquinas.size(); i++) {
            Maquina maquina = maquinas.get(i);
            Esteira esteira = esteiras.get(i);

            // A primeira esteira leva matéria-prima até a moldagem; as demais levam o produto
            Object carga = (i == 0) ? materiaPrima : produto;

            if (!transportar(esteira, carga)) {
                System.out.println("[ERRO] A carga não chegou a " + maquina.getNome() + ".");
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

        System.out.println("[OK] Produto passou por toda a linha de producao!");
        return true;
    }

    /**
     * Executa a movimentação de uma carga (produto ou matéria-prima) ligando a esteira e percorrendo o trajeto
     *
     * @param esteira A esteira que realizará o transporte
     * @param carga   O objeto que será movimentado pela esteira
     * @return        true se o transporte for concluído com sucesso, false caso contrário
     */
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

    /**
     * Instancia um novo objeto de produto com base na string informada pelo usuário
     *
     * @param tipoProduto O nome em texto do tipo de produto a ser criado
     * @param id          O identificador único a ser atribuído ao novo produto
     * @return            O objeto Produto instanciado ou null se o tipo for inválido
     */
    private Produto criarProduto(String tipoProduto, int id) {
        if (tipoProduto.equals("Pato Pequeno")) {
            return new PatoPequeno(id);
        }
        else if (tipoProduto.equals("Pato Medio")) {
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

    /**
     * Valida se a linha de produção está montada corretamente com uma esteira correspondente para cada máquina
     *
     * @throws MaquinaIndisponivelException Se não houver máquinas ou se o número de máquinas for diferente do de esteiras
     */
    private void validarLinhaConfigurada() {
        if (maquinas.isEmpty() || maquinas.size() != esteiras.size()) {
            throw new MaquinaIndisponivelException("Linha de producao mal configurada: sao necessarias "
                    + esteiras.size() + " maquinas (uma por esteira).");
        }
    }

    /**
     * Verifica se todas as máquinas da linha estão operacionais e sem defeitos
     *
     * @return true se todas as máquinas estiverem funcionando, false se houver alguma quebrada
     */
    private boolean todasMaquinasOperantes() {
        boolean todasOk = true;

        for (Maquina maquina : maquinas) {
            if (maquina.estaQuebrada()) {
                System.out.println("[ERRO] " + maquina.getNome() + " esta quebrada. Repare em Auditoria > Reparar maquinas.");
                todasOk = false;
            }
        }

        return todasOk;
    }

    /**
     * Garante, antes de iniciar uma produção, que nenhuma máquina da linha esteja quebrada
     *
     * @throws MaquinaIndisponivelException Se houver pelo menos uma máquina quebrada
     */
    private void validarMaquinasOperantes() {
        List<String> quebradas = new ArrayList<>();

        for (Maquina maquina : maquinas) {
            if (maquina.estaQuebrada()) {
                quebradas.add(maquina.getNome());
            }
        }

        if (!quebradas.isEmpty()) {
            throw new MaquinaIndisponivelException(String.join(", ", quebradas)
                    + " quebrada(s). Repare em Auditoria > Reparar maquinas antes de produzir.");
        }
    }

    /**
     * Valida se a quantidade demandada não ultrapassa a capacidade máxima de nenhuma máquina
     *
     * @param quantidade A quantidade de recursos exigida pela operação
     * @throws RecursoInsuficienteException Se alguma máquina não suportar a carga
     */
    private void validarCapacidadeMaquinas(double quantidade) {
        for (Maquina maquina : maquinas) {
            if (!maquina.verificarCapacidadeMaquina(quantidade)) {
                throw new RecursoInsuficienteException("A demanda ultrapassa a capacidade da " + maquina.getNome()
                        + ". Reduza a quantidade da demanda.");
            }
        }
    }

    /**
     * Valida se a quantidade demandada não ultrapassa a capacidade máxima de nenhuma esteira
     *
     * @param quantidade O peso ou volume a ser transportado
     * @throws RecursoInsuficienteException Se alguma esteira não suportar a carga
     */
    private void validarCapacidadeEsteiras(double quantidade) {
        for (Esteira esteira : esteiras) {
            if (!esteira.verificarCapacidade(quantidade)) {
                throw new RecursoInsuficienteException("A quantidade ultrapassa a capacidade da Esteira "
                        + esteira.getNumero() + ". Reduza a quantidade da demanda.");
            }
        }
    }

    /**
     * Calcula o custo total para produzir uma única unidade do produto somando as taxas de todas as máquinas
     *
     * @return O valor do custo de operação por unidade
     */
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

    /**
     * Realiza a compra de mais matéria-prima caso haja orçamento, adicionando-a ao estoque
     *
     * @param quantidade A quantidade de matéria-prima a ser comprada
     * @throws ArgumentoIlegalException       Se a quantidade for menor ou igual a zero
     * @throws OrcamentoInsuficienteException Se o budget não cobrir o custo da compra
     */
    public void comprarMateriaPrima(double quantidade) {
        if (quantidade <= 0) {
            throw new ArgumentoIlegalException("Quantidade invalida: informe um valor maior que zero.");
        }

        double custo = quantidade * materiaPrima.getCustoPorUnidade();

        if (custo > budget) {
            throw new OrcamentoInsuficienteException("Budget insuficiente para a compra.\n"
                    + "Custo: R$ " + String.format("%.2f", custo)
                    + " | Budget disponivel: R$ " + String.format("%.2f", budget)
                    + "\nNenhuma compra foi realizada.");
        }

        materiaPrima.adicionarEstoque(quantidade);
        budget -= custo;

        System.out.println("[OK] Compra realizada.");
        System.out.println("Valor pago: R$ " + String.format("%.2f", custo));
        System.out.println("Budget restante: R$ " + String.format("%.2f", budget));
    }

    /**
     * Exibe  uma lista numerada com o relatório de diagnóstico e o custo de reparo de cada máquina
     */
    public void exibirMaquinas() {
        for (int i = 0; i < maquinas.size(); i++) {
            Maquina maquina = maquinas.get(i);
            System.out.println((i + 1) + " - " + maquina.gerarRelatorioDiagnostico()
                    + " | Reparo: R$ " + String.format("%.2f", maquina.getCustoReparo()));
        }
    }

    /**
     * Realiza o reparo de uma máquina específica pagando o custo com o orçamento disponível da fábrica
     *
     * @param indice O índice numérico (a partir de 0) da máquina na lista
     * @throws ArgumentoIlegalException       Se não existir máquina no índice informado
     * @throws StatusIlegalException          Se a máquina não precisar de reparo
     * @throws OrcamentoInsuficienteException Se o budget não cobrir o custo do reparo
     */
    public void repararMaquina(int indice) {
        if (indice < 0 || indice >= maquinas.size()) {
            throw new ArgumentoIlegalException("Maquina inexistente.");
        }

        Maquina maquina = maquinas.get(indice);

        if (!maquina.precisaManutencao()) {
            throw new StatusIlegalException(maquina.getNome() + " ainda esta em boa forma, nao precisa de reparo.");
        }

        if (maquina.getCustoReparo() > budget) {
            throw new OrcamentoInsuficienteException("Budget insuficiente para o reparo.\n"
                    + "Custo do reparo: R$ " + String.format("%.2f", maquina.getCustoReparo())
                    + " | Budget disponivel: R$ " + String.format("%.2f", budget) + "\nNenhum reparo foi realizado.");
        }

        budget -= maquina.getCustoReparo();
        maquina.reparar();
        System.out.println("Reparo pago: R$ " + String.format("%.2f", maquina.getCustoReparo()));
        System.out.println("Budget restante: R$ " + String.format("%.2f", budget));
    }

    // ===========================
    // AUDITORIA (polimorfismo sobre Auditavel)
    // ===========================

    /**
     * Gera e exibe um relatório contendo o status atual de todas as máquinas e dos produtos armazenados que exigem atenção
     */
    public void gerarAuditoriaGeral() {
        Estilo.titulo("AUDITORIA GERAL DA FÁBRICA");

        System.out.println("\n[MAQUINAS]");
        int maquinasEmAtencao = auditar(maquinas, false);

        System.out.println("\n[PRODUTOS EM ESTOQUE QUE EXIGEM ATENCAO]");
        int produtosEmAtencao = auditar(produtosFabricados, true);
        if (produtosEmAtencao == 0) {
            System.out.println("Nenhum produto em risco.");
        }

        System.out.println("\n" + Estilo.linhaSimples());
        System.out.println("Componentes auditados: " + (maquinas.size() + produtosFabricados.size())
                + " (" + maquinas.size() + " maquinas, " + produtosFabricados.size() + " produtos)");
        System.out.println("Exigem atencao: " + (maquinasEmAtencao + produtosEmAtencao));
        System.out.println("Cenario: " + cenario.getNome() + " | Budget: R$ " + String.format("%.2f", budget));
    }

    /**
     * Exibe um relatório de auditoria focado  no diagnóstico de todas as máquinas
     */
    public void gerarAuditoriaMaquinas() {
        Estilo.titulo("AUDITORIA DAS MAQUINAS");
        auditar(maquinas, false);
    }

    /**
     * Exibe um relatório de auditoria focado no estado de conservação dos produtos já finalizados
     */
    public void gerarAuditoriaProdutos() {
        Estilo.titulo("AUDITORIA DOS PRODUTOS");
        if (produtosFabricados.isEmpty()) {
            System.out.println("Nenhum produto no armazem para auditar.");
            return;
        }
        auditar(produtosFabricados, false);
    }

    /**
     * Percorre uma lista de itens auditáveis e imprime relatórios, contabilizando quantos exigem manutenção
     *
     * @param itens          A lista de objetos que implementam a interface Auditavel
     * @param somenteAtencao Define se apenas os itens com problemas devem ser listados na tela
     * @return               A quantidade de itens na lista que precisam de atenção
     */
    private int auditar(List<? extends Auditavel> itens, boolean somenteAtencao) {
        int emAtencao = 0;

        for (Auditavel item : itens) {
            boolean precisa = item.precisaManutencao();

            if (precisa) {
                emAtencao++;
            }

            if (precisa || !somenteAtencao) {
                String marca = precisa ? "[ATENCAO] " : "[OK] ";
                System.out.println("- " + marca + item.gerarRelatorioDiagnostico());
            }
        }

        return emAtencao;
    }

    // ===========================
    // EXIBIÇÕES
    // ===========================

    /**
     * Exibe no console o valor atual disponível no orçamento da fábrica
     */
    public void exibirBudget() {
        Estilo.titulo("BUDGET");
        System.out.println("Budget atual: R$ " + String.format("%.2f", budget));
    }

    /**
     * Lista todas as demandas cadastradas indicando seus estados, estimativas de consumo 
     * e qual será a próxima a ser fabricada
     */
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
            String marca = (demanda == proxima) ? "  <== PROXIMA" : "";
            System.out.println("- " + demanda.getTipoProduto() + " | " + demanda.getQuantidadeProdutos() + " unid. | "
                    + demanda.getStatus().getDescricao()
                    + " | Custo est.: R$ " + String.format("%.2f", demanda.calcularCustoEstimado())
                    + " | Borracha est.: " + String.format("%.2f", demanda.calcularConsumoEstimado()) + " " + materiaPrima.getUnidade()
                    + marca);
        }

        if (estrategiaAtual != null) {
            System.out.println("\nEstrategia atual: " + estrategiaAtual.getNomeEstrategia());
        }
    }

    /**
     * Exibe um relatório dos produtos finalizados no armazém, agrupados por tipo e lote, mostrando 
     * suas médias de qualidade e risco
     */
    public void exibirArmazem() {
        Estilo.titulo("ARMAZEM - PATINHOS ACABADOS");

        if (produtosFabricados.isEmpty()) {
            System.out.println("O armazem esta vazio.");
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

                String statusRisco = (emRisco == 0) ? "Risco: OK" : "Risco: ATENCAO (" + emRisco + " em risco)";

                System.out.println("   Lote " + porLote.getKey() + " | " + produtos.size() + " unid. | Qualidade media: "
                        + String.format("%.0f%%", 100 * somaQualidade / produtos.size())
                        + " | Risco medio: " + String.format("%.0f%%", 100 * somaRisco / produtos.size())
                        + " | " + statusRisco);
            }
        }

        System.out.println("\nTotal de produtos armazenados: " + produtosFabricados.size());
    }

    /**
     * Exibe a quantidade de matéria-prima bruta atualmente no estoque, informando reservas e volume disponível para novas produções
     */
    public void exibirEstoqueMateriaPrima() {
        Estilo.titulo("ESTOQUE DE MATERIA-PRIMA");
        System.out.println(materiaPrima.getNome() + ": " + String.format("%.2f", materiaPrima.getQuantidade())
                + " " + materiaPrima.getUnidade());
        System.out.println("Reserva minima: " + String.format("%.2f", materiaPrima.getQuantidadeMinima())
                + " " + materiaPrima.getUnidade() + " | Disponivel para producao: "
                + String.format("%.2f", materiaPrima.getQuantidadeDisponivel()) + " " + materiaPrima.getUnidade());
        System.out.println("Preco de compra: R$ " + String.format("%.2f", materiaPrima.getCustoPorUnidade())
                + " por " + materiaPrima.getUnidade());
    }

    /**
     * Obtém o valor atualizado do orçamento da fábrica
     *
     * @return O budget disponível
     */
    public double getBudget() {
        return budget;
    }

    /**
     * Obtém o cenário de operação atual em que a fábrica está sendo executada
     *
     * @return O cenário da simulação
     */
    public Cenario getCenario() {
        return cenario;
    }

    /**
     * Obtém a estratégia de seleção de demandas que está ativamente em uso
     *
     * @return A estratégia de produção atual
     */
    public EstrategiaProducao getEstrategiaAtual() {
        return estrategiaAtual;
    }

    /**
     * Obtém o número total de máquinas instaladas e configuradas na linha de produção
     *
     * @return A quantidade de máquinas
     */
    public int getQuantidadeMaquinas() {
        return maquinas.size();
    }

    /**
     * Obtém o objeto que gerencia o estoque e o consumo de matéria-prima da fábrica
     *
     * @return A matéria-prima utilizada
     */
    public MateriaPrima getMateriaPrima() {
        return materiaPrima;
    }
}