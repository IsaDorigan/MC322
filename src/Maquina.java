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
    // public Produto processar(MateriaPrima materiaPrima, Produto produto, double demanda) {

    //     // Verifica se a máquina esta ligada
    //     if (!ligada) { //Se ligada=false, !ligada=true, e assim entramos no if
    //         System.out.println("[ERRO] A " + getNome() + " está desligada.");
    //         return null;
    //     }

    //     // Verifica se a demanda não ultrapassa a capacidade da máquina
    //     if (demanda > capacidadeMaxima) {
    //         System.out.println("[ERRO] A demanda ultrapassa a capacidade máxima da " + getNome());
    //         return null;
    //     }

    //     // Verifica se há matéria-prima suficiente
    //     if (!materiaPrima.verificarDisponibilidade(demanda)) { // mesma lógica de verificar se a máquina está ligada
    //         System.out.println("[ERRO] Matéria-prima insuficiente.");
    //         return null;
    //     }

    //     // Sempre que processamos um produto, devemos consumir matéria prima
    //     materiaPrima.consumir(demanda);

    //     // Processa o produto
    //     produto.processar();

    //     // Marca o produto como processado
    //     produto.processado();
       
    //     return produto;
    // }

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