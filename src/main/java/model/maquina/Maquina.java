package model.maquina;

import model.maquina.componentes.Molde;

import model.exceptions.ArgumentoIlegalException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Auditavel;
import model.Produto;
import model.enums.Cenario;
import model.enums.StatusMaquina;
import model.enums.StatusProduto;
import model.maquina.componentes.AtuadorMecanico;
import model.maquina.componentes.Motor;
import model.maquina.componentes.SensorTemperatura;
import util.Sorteio;

/**
 * Classe base das máquinas da linha de produção.
 * Implementa Auditavel e modela o desgaste progressivo:
 *  - a cada uso a saúde (0 a 100) cai um valor aleatório pequeno;
 *  - quanto menor a saúde, maior a probabilidade de falha (inversamente proporcional);
 *  - com saúde 0 a máquina QUEBRA e só volta a operar depois de reparada.
 * O método processar() é um Template Method: as verificações e o desgaste ficam
 * aqui (DRY) e cada subclasse implementa apenas o seu trabalho em executarProcessamento().
 */
public abstract class Maquina implements Auditavel {

    private static final double SAUDE_MAXIMA = 100.0;
    private static final double DESGASTE_MAXIMO_POR_USO = 3.0;  // antes do multiplicador do cenário
    private static final double LIMIAR_MANUTENCAO_PADRAO = 30.0;
    private static final double FATOR_CUSTO_REPARO = 3.0;       // reparo custa 3x a operação


    // ATRIBUTOS
    
    private final String nome;
    private StatusMaquina status;
    private final double capacidadeMaxima;
    private final double probabilidadeFalhaBase;  // com saúde 100% e já ajustada pelo cenário
    private final double custoOperacao;
    private final double custoReparo;
    private final Cenario cenario;

    private double saude;
    private double limiarManutencao;
    private int usos;
    private int falhas;

    // Relações de composição
    private final Motor motor;                            
    private final SensorTemperatura sensor;               
    private final List<AtuadorMecanico> atuadores;        

    
    /**
     * Construtor padrão da Máquina
     *
     * @param nome                   O nome da máquina
     * @param capacidadeMaxima       O volume ou quantidade máxima que a máquina suporta
     * @param probabilidadeFalhaBase A probabilidade de falha com saúde a 100%
     * @param custoOperacao          O custo debitado por cada uso da máquina
     * @param cenario                O cenário atual (afeta multiplicadores de desgaste e falha)
     */
    public Maquina(String nome, double capacidadeMaxima, double probabilidadeFalhaBase,
                   double custoOperacao, Cenario cenario, int qtdAtuadores) {
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
        this.probabilidadeFalhaBase = probabilidadeFalhaBase * cenario.getMultiplicadorFalhaMaquina();
        this.custoOperacao = custoOperacao;
        this.custoReparo = custoOperacao * FATOR_CUSTO_REPARO;
        this.cenario = cenario;
        this.status = StatusMaquina.DESLIGADA;
        this.saude = SAUDE_MAXIMA;
        this.limiarManutencao = LIMIAR_MANUTENCAO_PADRAO;
        this.usos = 0;
        this.falhas = 0;

        // O "Todo" instancia as "Partes", amarrando os ciclos de vida.
        this.motor = new Motor("G4-" + nome.replaceAll("\\s+", ""));
        this.sensor = new SensorTemperatura("STX-" + nome.replaceAll("\\s+", ""));
        this.atuadores = new ArrayList<>();
        for (int i = 0; i < qtdAtuadores; i++) {
            this.atuadores.add(new AtuadorMecanico("Atuador " + (i + 1), 50.0));
        }
    }

    /**
     * Executa o processamento específico da máquina sobre um produto
     * Cada subclasse deve implementar o comportamento correspondente ao seu tipo de máquina
     *
     * @param produto O produto que será processado pela máquina
     * @return        O produto processado ou null caso o produto seja descartado
     */
    protected abstract Produto executarProcessamento(Produto produto);

    /**
     * Descrição do getter
     * 
     * @return Retorna o tipo
     */
    public abstract String getTipo();

   /**
    * Obtém a mensagem temática exibida quando a máquina apresenta falha
    *
    * @return A mensagem descritiva da falha
    */
    protected abstract String getMensagemFalha();


    /**
     * Processa um produto na máquina, aplicando desgaste e verificando possíveis falhas
     *
     * @param produto O produto a ser processado pela máquina
     * @return        O produto resultante do processamento, ou null caso falhe ou a máquina esteja inoperante
     */
    public final Produto processar(Produto produto) {
        if (estaQuebrada()) {
            System.out.println("[ERRO] " + nome + " está quebrada.");
            return null;
        }

        motor.operar();
        sensor.lerTemperatura(motor.getTemperaturaAtual());
        for (AtuadorMecanico atuador : atuadores) {
            atuador.acionar();
        }

        if (!estaLigada()) {
            System.out.println("[ERRO] " + nome + " está desligada.");
            return null;
        }

        if (produto == null) {
            System.out.println("[ERRO] Produto inexistente.");
            return null;
        }

        Produto resultado;

        if (verificarFalha()) {
            falhas++;
            System.out.println("[ERRO] " + getMensagemFalha());
            produto.setStatus(StatusProduto.PERDIDO);
            resultado = null;
        } else {
            resultado = executarProcessamento(produto);
        }

        desgastar();  // todo uso, com sucesso ou falha, desgasta a máquina
        return resultado;
    }


    // CONTROLE DA MÁQUINA

    /**
     * Altera o status da máquina para LIGADA, permitindo seu funcionamento caso não esteja quebrada
     */
    public void ligar() {
        if (estaQuebrada()) {
            System.out.println("[ERRO] " + nome + " está quebrada e não pode ser ligada.");
            return;
        }
        status = StatusMaquina.LIGADA;
        motor.ligar();
        System.out.println("[OK] " + nome + " ligada.");
    }

    /**
     * Altera o status da máquina para DESLIGADA, interrompendo seu funcionamento
     */
    public void desligar() {
        if (estaQuebrada()) {
            return;  // uma máquina quebrada continua quebrada
        }
        status = StatusMaquina.DESLIGADA;
        motor.desligar();
        System.out.println("[OK] " + nome + " desligada.");
    }

    /**
     * Realiza o reparo da máquina, restaurando sua saúde ao valor máximo e redefinindo seu status
     */
    public void reparar() {
        saude = SAUDE_MAXIMA;
        status = StatusMaquina.DESLIGADA;

        motor.revisar();
        sensor.calibrar();
        for (AtuadorMecanico atuador : atuadores) {
            atuador.lubrificar();
        }
        System.out.println("[OK] " + nome + " e suas pecas foram reparadas.");
    }

    
    /**
     * Aplica o desgaste progressivo à máquina após cada uso, 
     * diminuindo sua saúde com base no cenário
     */
    private void desgastar() {
        boolean precisavaManutencao = precisaManutencao();

        double desgaste = Sorteio.entre(0, DESGASTE_MAXIMO_POR_USO) * cenario.getMultiplicadorDesgaste();
        saude = Math.max(0, saude - desgaste);
        usos++;

        if (saude <= 0) {
            status = StatusMaquina.QUEBRADA;
            System.out.println("[ALERTA] " + nome + " quebrou de tanto trabalhar! Repare em Auditoria > Reparar máquinas.");
        } else if (!precisavaManutencao && precisaManutencao()) {
            System.out.println("[AVISO] " + nome + " está desgastada e precisa de manutenção (saúde "
                    + String.format("%.1f", saude) + ").");
        }
    }

    /**
     * Calcula a probabilidade de falha atual da máquina baseada na proporção de sua saúde
     *
     * @return A probabilidade de falha efetiva, variando de 0.0 a 1.0
     */
    public double getProbabilidadeFalhaEfetiva() {
        double saudeSegura = Math.max(saude, 1.0);
        return Math.min(1.0, probabilidadeFalhaBase * (SAUDE_MAXIMA / saudeSegura));
    }

    /**
     * Sorteia e verifica se ocorre uma falha durante a operação com base na probabilidade efetiva
     *
     * @return true se ocorrer uma falha, false caso contrário
     */
    protected boolean verificarFalha() {
        return Sorteio.ocorre(getProbabilidadeFalhaEfetiva());
    }

    /**
     * Aumenta a probabilidade de falha de um produto dependendo de um sorteio e do cenário atual
     *
     * @param produto    O produto que terá o risco de falha possivelmente aumentado
     * @param chanceBase A chance base de ocorrência do aumento de risco na máquina
     * @param aumento    O valor a ser acrescido na probabilidade de falha do produto
     * @param mensagem   A mensagem de aviso a ser exibida caso o risco de fato aumente
     */
    protected void possivelmenteAumentarRisco(Produto produto, double chanceBase, double aumento, String mensagem) {
        if (Sorteio.ocorre(chanceBase * cenario.getMultiplicadorFalhaProduto())) {
            produto.aumentarProbabilidadeFalha(aumento);
            System.out.println("[AVISO] " + mensagem);
        }
    }


    /**
     * Gera um relatório formatado com os dados e o status atual de operação e saúde da máquina
     * Faz a auditoria em cascata
     *
     * @return o relatório completo de diagnóstico
     */
    @Override
    public String gerarRelatorioDiagnostico() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s [%s] | Saude: %.1f/100 | Falhas: %d",
                nome, status.getDescricao(), saude, falhas));
                
        // Propaga o relatório para as partes internas (formatado hierarquicamente)
        sb.append("\n    \\_ ").append(motor.gerarRelatorioDiagnostico());
        sb.append("\n    \\_ ").append(sensor.gerarRelatorioDiagnostico());
        for (AtuadorMecanico atuador : atuadores) {
            sb.append("\n    \\_ ").append(atuador.gerarRelatorioDiagnostico());
        }
        
        return sb.toString();
    }

    /**
     * Verifica se a máquina necessita de manutenção preventiva com base na sua saúde atual
     *
     * @return true se a saúde estiver abaixo do limiar de manutenção ou das partes que precisam de manutenção,
     *  false caso contrário
     */
    @Override
    public boolean precisaManutencao() {
        boolean partesPrecisam = motor.precisaManutencao() || sensor.precisaManutencao();
        for (AtuadorMecanico atuador : atuadores) {
            if (atuador.precisaManutencao()) partesPrecisam = true;
        }
        return saude < limiarManutencao || partesPrecisam;
    }


    /**
     * Verifica se a demanda solicitada está dentro da capacidade máxima suportada pela máquina
     *
     * @param demanda A quantidade ou volume demandado
     * @return        true se a máquina suporta a demanda, false caso contrário
     */
    public boolean verificarCapacidadeMaquina(double demanda) {
        return demanda <= capacidadeMaxima;
    }


    /**
     * @return uma cópia imutavel da lista de atuadores
     */
    public List<AtuadorMecanico> getAtuadores() {
        return Collections.unmodifiableList(atuadores);
    }


    /**
     * Obtem o nome de identificação da máquina.
     *
     * @return O nome da máquina
     */
    public String getNome() {
        return nome;
    }

    /**
     * Obtém o custo debitado por cada utilização da máquina.
     *
     * @return O custo de operação
     */
    public double getCustoOperacao() {
        return custoOperacao;
    }

    /**
     * Obtém o valor necessário para realizar o reparo completo da máquina
     *
     * @return O custo de reparo
     */
    public double getCustoReparo() {
        return custoReparo;
    }

    /**
     * Obtém o valor atual da saúde da máquina
     *
     * @return A saúde da máquina (0 a 100)
     */
    public double getSaude() {
        return saude;
    }

    /**
     * Obtem o estado atual de operação em que a máquina se encontra
     *
     * @return O status da máquina
     */
    public StatusMaquina getStatus() {
        return status;
    }

    /**
     * Verifica se o status atual da máquina é LIGADA
     *
     * @return true se estiver ligada, false caso contrário
     */
    public boolean estaLigada() {
        return status == StatusMaquina.LIGADA;
    }

    /**
     * Verifica se o status atual da máquina é QUEBRADA
     *
     * @return true se estiver quebrada, false caso contrário
     */
    public boolean estaQuebrada() {
        return status == StatusMaquina.QUEBRADA;
    }

    /**
     * Obtem a quantidade total de vezes que a máquina operou.
     *
     * @return O número de usos
     */
    public int getUsos() {
        return usos;
    }

    /**
     * Obtem o número total de falhas que ocorreram durante as operações da máquina
     *
     * @return O número de falhas
     */
    public int getFalhas() {
        return falhas;
    }

    /**
     * Obtem o nível de saúde abaixo do qual a máquina requer manutenção preventiva
     *
     * @return O limiar de manutenção
     */
    public double getLimiarManutencao() {
        return limiarManutencao;
    }

    /**
     * Define um novo valor para o limiar em que a máquina passará a exigir manutenção preventiva
     *
     * @param limiarManutencao O novo valor para o limiar de manutenção (deve estar entre 0 e a saúde máxima)
     */
    public void setLimiarManutencao(double limiarManutencao) {
        if (limiarManutencao >= 0 && limiarManutencao <= SAUDE_MAXIMA) {
            this.limiarManutencao = limiarManutencao;
        }
    }

    // PERSISTÊNCIA (estado como instantâneo imutável: as Partes continuam encapsuladas)

    /**
     * Instantâneo imutável do estado variável da máquina e de todos os seus subcomponentes.
     * A configuração fixa (capacidade, custos, probabilidade base) não faz parte do estado,
     * pois é reconstruída pelo construtor a partir do tipo da máquina e do cenário.
     *
     * @param nome             nome da máquina
     * @param status           status operacional
     * @param saude            saúde atual (0 a 100)
     * @param limiarManutencao saúde abaixo da qual a máquina pede manutenção
     * @param usos             total de usos
     * @param falhas           total de falhas
     * @param motor            estado do motor
     * @param sensor           estado do sensor de temperatura
     * @param atuadores        estado dos atuadores, na ordem de instalação
     * @param moldes           estado dos moldes (vazio nas máquinas que não possuem moldes)
     */
    public record Estado(String nome, StatusMaquina status, double saude, double limiarManutencao,
                         int usos, int falhas, Motor.Estado motor, SensorTemperatura.Estado sensor,
                         List<AtuadorMecanico.Estado> atuadores, List<Molde.Estado> moldes) { }

    /**
     * Obtém uma cópia imutável do estado atual da máquina e de suas partes.
     *
     * @return o estado atual da máquina
     */
    public Estado obterEstado() {
        List<AtuadorMecanico.Estado> estadosAtuadores = new ArrayList<>();
        for (AtuadorMecanico atuador : atuadores) {
            estadosAtuadores.add(atuador.obterEstado());
        }
        return new Estado(nome, status, saude, limiarManutencao, usos, falhas,
                motor.obterEstado(), sensor.obterEstado(),
                Collections.unmodifiableList(estadosAtuadores),
                Collections.unmodifiableList(obterEstadosMoldes()));
    }

    /**
     * Restaura o estado da máquina e de suas partes a partir de um instantâneo.
     * Todas as regras do domínio são verificadas; em caso de erro, a máquina deve ser descartada.
     *
     * @param estado o estado a ser restaurado
     * @throws ArgumentoIlegalException se o estado for nulo, pertencer a outra máquina, tiver valores fora
     *                                  das regras do domínio ou não corresponder à composição da máquina
     */
    public void restaurarEstado(Estado estado) {
        if (estado == null) {
            throw new ArgumentoIlegalException("Estado da maquina ausente.");
        }
        if (!nome.equals(estado.nome())) {
            throw new ArgumentoIlegalException("Maquina incompativel: esperado " + nome
                    + ", mas os dados informam " + estado.nome() + ".");
        }
        if (estado.status() == null) {
            throw new ArgumentoIlegalException("Status da maquina " + nome + " ausente.");
        }
        if (!(estado.saude() >= 0 && estado.saude() <= SAUDE_MAXIMA)) {
            throw new ArgumentoIlegalException("A saude da " + nome + " deve estar entre 0 e " + SAUDE_MAXIMA + ".");
        }
        if (!(estado.limiarManutencao() >= 0 && estado.limiarManutencao() <= SAUDE_MAXIMA)) {
            throw new ArgumentoIlegalException("O limiar de manutencao da " + nome + " deve estar entre 0 e "
                    + SAUDE_MAXIMA + ".");
        }
        if (estado.usos() < 0 || estado.falhas() < 0 || estado.falhas() > estado.usos()) {
            throw new ArgumentoIlegalException("Usos e falhas da " + nome
                    + " invalidos (nao negativos e com falhas <= usos).");
        }
        if ((estado.status() == StatusMaquina.QUEBRADA) != (estado.saude() <= 0)) {
            throw new ArgumentoIlegalException("A " + nome + " so pode estar QUEBRADA com saude zero (e vice-versa).");
        }
        if (estado.atuadores() == null || estado.atuadores().size() != atuadores.size()) {
            throw new ArgumentoIlegalException("A " + nome + " possui " + atuadores.size()
                    + " atuador(es), mas os dados informam "
                    + (estado.atuadores() == null ? 0 : estado.atuadores().size()) + ".");
        }

        motor.restaurarEstado(estado.motor());
        sensor.restaurarEstado(estado.sensor());
        for (int i = 0; i < atuadores.size(); i++) {
            atuadores.get(i).restaurarEstado(estado.atuadores().get(i));
        }
        restaurarEstadosMoldes(estado.moldes() == null ? List.of() : estado.moldes());

        this.status = estado.status();
        this.saude = estado.saude();
        this.limiarManutencao = estado.limiarManutencao();
        this.usos = estado.usos();
        this.falhas = estado.falhas();
    }

    /**
     * Estados dos moldes da máquina. Por padrão a máquina não possui moldes;
     * subclasses com moldes sobrescrevem este método.
     *
     * @return lista com o estado de cada molde (vazia por padrão)
     */
    protected List<Molde.Estado> obterEstadosMoldes() {
        return new ArrayList<>();
    }

    /**
     * Restaura os moldes da máquina. Por padrão a máquina não possui moldes,
     * portanto qualquer molde informado é incompatível.
     *
     * @param estadosMoldes estados dos moldes a restaurar
     * @throws ArgumentoIlegalException se forem informados moldes para uma máquina que não os possui
     */
    protected void restaurarEstadosMoldes(List<Molde.Estado> estadosMoldes) {
        if (!estadosMoldes.isEmpty()) {
            throw new ArgumentoIlegalException("A " + nome + " nao possui moldes, mas os dados informam "
                    + estadosMoldes.size() + ".");
        }
    }
}
