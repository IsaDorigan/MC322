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

    // CONSTRUTOR
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

    // MÉTODOS ABSTRATOS

    // O trabalho específico da máquina (moldar, embalar, inspecionar).
    // Retorna o produto processado ou null se ele foi descartado.
    protected abstract Produto executarProcessamento(Produto produto);

    public abstract String getTipo();

    // Mensagem temática exibida quando a máquina falha
    protected abstract String getMensagemFalha();

    // TEMPLATE METHOD

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

    public void ligar() {
        if (estaQuebrada()) {
            System.out.println("[ERRO] " + nome + " está quebrada e não pode ser ligada.");
            return;
        }
        status = StatusMaquina.LIGADA;
        System.out.println("[OK] " + nome + " ligada.");
    }

    public void desligar() {
        if (estaQuebrada()) {
            return;  // uma máquina quebrada continua quebrada
        }
        status = StatusMaquina.DESLIGADA;
        System.out.println("[OK] " + nome + " desligada.");
    }

    // Repara a máquina: saúde volta ao máximo e ela sai do estado QUEBRADA
    public void reparar() {
        saude = SAUDE_MAXIMA;
        status = StatusMaquina.DESLIGADA;
        System.out.println("[OK] " + nome + " foi reparada e está com saúde total.");
    }

    // DESGASTE E FALHA

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

    // Probabilidade de falha atual: inversamente proporcional à saúde (saúde 100 = base, saúde 50 = o dobro...)
    public double getProbabilidadeFalhaEfetiva() {
        double saudeSegura = Math.max(saude, 1.0);
        return Math.min(1.0, probabilidadeFalhaBase * (SAUDE_MAXIMA / saudeSegura));
    }

    protected boolean verificarFalha() {
        return Sorteio.ocorre(getProbabilidadeFalhaEfetiva());
    }

    // Algumas máquinas não falham diretamente: elas podem aumentar o risco do produto
    protected void possivelmenteAumentarRisco(Produto produto, double chanceBase, double aumento, String mensagem) {
        if (Sorteio.ocorre(chanceBase * cenario.getMultiplicadorFalhaProduto())) {
            produto.aumentarProbabilidadeFalha(aumento);
            System.out.println("[AVISO] " + mensagem);
        }
    }

    // AUDITAVEL

    @Override
    public String gerarRelatorioDiagnostico() {
        return String.format("%s [%s] | Saúde: %.1f/100 | Prob. de falha atual: %.1f%% | Usos: %d | Falhas: %d",
                nome, status.getDescricao(), saude, getProbabilidadeFalhaEfetiva() * 100, usos, falhas);
    }

    @Override
    public boolean precisaManutencao() {
        return saude < limiarManutencao;
    }

    // CAPACIDADE

    public boolean verificarCapacidadeMaquina(double demanda) {
        return demanda <= capacidadeMaxima;
    }

    // GETTERS E SETTERS

    public String getNome() {
        return nome;
    }

    public double getCustoOperacao() {
        return custoOperacao;
    }

    public double getCustoReparo() {
        return custoReparo;
    }

    public double getSaude() {
        return saude;
    }

    public StatusMaquina getStatus() {
        return status;
    }

    public boolean estaLigada() {
        return status == StatusMaquina.LIGADA;
    }

    public boolean estaQuebrada() {
        return status == StatusMaquina.QUEBRADA;
    }

    public int getUsos() {
        return usos;
    }

    public int getFalhas() {
        return falhas;
    }

    public double getLimiarManutencao() {
        return limiarManutencao;
    }

    public void setLimiarManutencao(double limiarManutencao) {
        if (limiarManutencao >= 0 && limiarManutencao <= SAUDE_MAXIMA) {
            this.limiarManutencao = limiarManutencao;
        }
    }
}
