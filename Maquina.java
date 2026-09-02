public class Maquina {
    // Atributos
    private String nome;
    private boolean ligada;
    private double capacidadeMaxima;

    // Construtor
    public Maquina(String nome, double capacidadeMaxima) {
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
        this.ligada = false;
}

 // Liga a máquina
    public void ligar() {
        ligada = true;
        System.out.println("[OK] Máquina ligada.");
    }

    // Desliga a máquina
    public void desligar() {
        ligada = false;
        System.out.println("[OK] Máquina desligada.");
    }

    // Processa a matéria-prima
    public Produto processar(MateriaPrima materiaPrima, Produto produto, double demanda) {

        // Verifica se a máquina esta ligada
        if (!ligada) { //Se ligada=false, !ligada=true, e assim entramos no if
            System.out.println("[ERRO] A máquina está desligada.");
            return null;
        }

        // Verifica se a demanda não ultrapassa a capacidade da máquina
        if (demanda > capacidadeMaxima) {
            System.out.println("[ERRO] A demanda ultrapassa a capacidade máxima da máquina.");
            return null;
        }

        // Verifica se há matéria-prima suficiente
        if (!materiaPrima.verificarDisponibilidade(demanda)) { // mesma lógica de verificar se a máquina está ligada
            System.out.println("[ERRO] Matéria-prima insuficiente.");
            return null;
        }

        // Consome a matéria-prima
        materiaPrima.consumir(demanda);

        System.out.println("[OK] Máquina processando " + demanda + " Kg de " + materiaPrima.getNome() + "...");

        // Finaliza o processo do produto
        produto.processado();

        return produto;
    }

    // Retorna o nome da máquina
    public String getNome() {
        return nome;
    }

    // Retorna se a máquina está ligada
    public boolean estaLigada() {
        return ligada;
    }
}