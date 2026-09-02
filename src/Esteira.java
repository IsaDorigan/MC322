public class Esteira {

    // ATRIBUTOS
    private Object item;
    private boolean emMovimento;
    private double capacidadeMaxima;

    // CONSTRUTOR
    public Esteira(double capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
        this.emMovimento = false;
        this.item = null;
    }

    // MÉTODOS

    public void ligar() {
        emMovimento = true;
        System.out.println("[OK] Esteira ligada.");
    }

    public void desligar() {
        emMovimento = false;
        System.out.println("[OK] Esteira desligada.");
    }

    public boolean adicionarItem(Object item) {
        if (!emMovimento) {
            System.out.println("[ERRO] A esteira está desligada.");
            return false;
        }

        if (this.item != null) {
            System.out.println("[ERRO] A esteira já possui um item.");
            return false;
        }

        this.item = item;

        // Verifica se é produto ou matéria prima antes de colocar na esteira
        if (item instanceof Produto) {                                                  // se item fo da classe Produto
            Produto produto = (Produto) item;                                           // cria uma variável produto de classe Produto, e redeclaramos item como Produto (cast)
            System.out.println("[OK] " + produto.getNome() + " colocado na esteira.");  // pega o nome do produto (item) e faz um print
        }                                                                               // precisamos fazer isso pois item é inicialmente declarado como Object

        else if (item instanceof MateriaPrima) {
            MateriaPrima materia = (MateriaPrima) item;
            System.out.println("[OK] " + materia.getNome() + " colocada na esteira.");
        }

        return true;
    }

    public Object removerItem() {

        if (!emMovimento) {
            System.out.println("[ERRO] A esteira está desligada.");
            return null;
        }

        if (item == null) {
            System.out.println("[ERRO] Não há nenhum item na esteira.");
            return null;
        }

        Object itemRemovido = item;
        item = null;

        // Verifica se é um produto ou matéria prima antes de remover da esteira
        if (itemRemovido instanceof Produto) {
            Produto produto = (Produto) itemRemovido;
            System.out.println("[OK] " + produto.getNome() + " removido da esteira.");
        } 

        else if (itemRemovido instanceof MateriaPrima) {
            MateriaPrima materia = (MateriaPrima) itemRemovido;
            System.out.println("[OK] " + materia.getNome() + " removida da esteira.");
        }

        return itemRemovido;
    }

    public boolean verificarCapacidade(double quantidade) {
        return quantidade <= capacidadeMaxima;
    }
}
