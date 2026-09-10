import java.util.Random;

public abstract class Maquina {

    // Atributos
    private String nome;
    private boolean ligada;
    private double capacidadeMaxima;
    private double probabilidadeFalha;
    private double custoOperacao;
    private Random random;

    // Construtor
    public Maquina(String nome, double capacidadeMaxima, double probabilidadeFalha, double custoOperacao) {
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
        this.ligada = false;
        this.probabilidadeFalha = probabilidadeFalha;
        this.custoOperacao = custoOperacao;
        this.random = new Random();
    }

    // MÉTODOS ABSTRATOS

    public abstract Produto processar(Produto produto);

    public abstract String getTipo();

    // MÉTODOS CONCRETOS

    public void ligar() {
        ligada = true;
        System.out.println("[OK] " + getNome() + " ligada.");
    }

    public void desligar() {
        ligada = false;
        System.out.println("[OK] " + getNome() + " desligada.");
    }

    // // Processa a matéria-prima

    public boolean verificarCapacidadeMaquina(double demanda) {
        return demanda <= capacidadeMaxima;
    }

    protected boolean verificarFalha() {
        double aleatorio = random.nextDouble(); // Gera um numero aleatório
        return aleatorio < probabilidadeFalha;  // Se esse número for menor que a probabilidade, então falha
                                                // Simulamos uma "região" onde essa falha ocorre, que vai de 0 até probabilidadeFalha
    }

    // Retorna o nome da máquina
    public String getNome() {
        return nome;
    }

    public double getCustoOperacao() {
        return custoOperacao;
    }

    // Retorna se a máquina está ligada
    public boolean estaLigada() {
        return ligada;
    }
}