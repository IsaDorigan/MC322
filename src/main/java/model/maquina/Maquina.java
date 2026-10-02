package model.maquina;
import model.Auditavel;
import model.Produto;
import model.enums.Cenario;
import model.enums.StatusMaquina;
import model.enums.StatusProduto;
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
                   double custoOperacao, Cenario cenario) {
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
        System.out.println("[OK] " + nome + " desligada.");
    }

    /**
     * Realiza o reparo da máquina, restaurando sua saúde ao valor máximo e redefinindo seu status
     */
    public void reparar() {
        saude = SAUDE_MAXIMA;
        status = StatusMaquina.DESLIGADA;
        System.out.println("[OK] " + nome + " foi reparada e está com saúde total.");
    }

    
    /**
     * Aplica o desgaste progressivo à máquina após cada uso, diminuindo sua saúde com base no cenário
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
     *
     * @return Uma string contendo o relatório completo de diagnóstico
     */
    @Override
    public String gerarRelatorioDiagnostico() {
        return String.format("%s [%s] | Saúde: %.1f/100 | Prob. de falha atual: %.1f%% | Usos: %d | Falhas: %d",
                nome, status.getDescricao(), saude, getProbabilidadeFalhaEfetiva() * 100, usos, falhas);
    }

    /**
     * Verifica se a máquina necessita de manutenção preventiva com base na sua saúde atual
     *
     * @return true se a saúde estiver abaixo do limiar de manutenção, false caso contrário
     */
    @Override
    public boolean precisaManutencao() {
        return saude < limiarManutencao;
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
}
