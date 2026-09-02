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

    // MÉTODOS

    // Liga a máquina
    public void ligar() {
        ligada = true;
        System.out.println("[OK] " + getNome() + " ligada.");
    }

    // Desliga a máquina
    public void desligar() {
        ligada = false;
        System.out.println("[OK] " + getNome() + " desligada.");
    }

    // Processa a matéria-prima
    public Produto processar(MateriaPrima materiaPrima, Produto produto, double demanda) {

        // Verifica se a máquina esta ligada
        if (!ligada) { //Se ligada=false, !ligada=true, e assim entramos no if
            System.out.println("[ERRO] A " + getNome() + " está desligada.");
            return null;
        }

        // Verifica se a demanda não ultrapassa a capacidade da máquina
        if (demanda > capacidadeMaxima) {
            System.out.println("[ERRO] A demanda ultrapassa a capacidade máxima da " + getNome());
            return null;
        }

        // Verifica se há matéria-prima suficiente
        if (!materiaPrima.verificarDisponibilidade(demanda)) { // mesma lógica de verificar se a máquina está ligada
            System.out.println("[ERRO] Matéria-prima insuficiente.");
            return null;
        }

        // Sempre que processamos um produto, devemos consumir matéria prima
        materiaPrima.consumir(demanda);

        System.out.println("[OK] " + getNome() + " processando " + demanda + " Kg de " + materiaPrima.getNome() + "...");

        // Finaliza o processo do produto, marcando-o como processado
        produto.processado();

        return produto;
    }

    public boolean verificarCapacidadeMaquina(double demanda) {
        return demanda <= capacidadeMaxima;
    }

    // GETTERS

    // Retorna o nome da máquina
    public String getNome() {
        return nome;
    }

    // Retorna se a máquina está ligada
    public boolean estaLigada() {
        return ligada;
    }
}