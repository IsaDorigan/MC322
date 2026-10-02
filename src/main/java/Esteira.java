/**
 * Representa uma esteira de transporte da fábrica, responsável por movimentar
 * produtos e matérias-primas entre as estações e máquinas
 */
public class Esteira {

    // ATRIBUTOS
    private Object item;
    private boolean emMovimento;
    private double capacidadeMaxima;
    private int numero;

    /**
     * Construtor da esteira que define sua capacidade máxima e número de identificação
     *
     * @param capacidadeMaxima O peso ou volume máximo que a esteira suporta
     * @param numero           O número de identificação único desta esteira
     */
    public Esteira(double capacidadeMaxima, int numero) {
        this.capacidadeMaxima = capacidadeMaxima;
        this.emMovimento = false;
        this.item = null;
        this.numero = numero;
    }
    
    // MÉTODOS

    /**
     * Liga a esteira colocando-a em movimento 
     */
    public void ligar() {
        emMovimento = true;
        System.out.println("[OK] Esteira " + this.numero + " ligada.");
    }

    /**
     * Desliga a esteira impedindo o movimento
     */ 
    public void desligar() {
        emMovimento = false;
        System.out.println("[OK] Esteira " + this.numero + " desligada.");
    }

    /**
     * Adiciona um item à esteira caso ela esteja ligada e não tenha nenhum outro item.
     * O item pode ser tanto um Produto quanto uma MateriaPrima
     *
     * @param item O objeto a ser colocado na esteira para transporte
     * @return     true se o item foi adicionado com sucesso, false caso contrário
     */
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
        if (item instanceof Produto) {                                                  // se item for da classe Produto
            Produto produto = (Produto) item;                                           // cria uma variável produto de classe Produto, e redeclaramos item como Produto (cast)
            System.out.println("[OK] " + produto.getNome() + " colocado na esteira " + this.numero + ".");  // pega o nome do produto (item) e faz um print
        }                                                                               // precisamos fazer isso pois item é inicialmente declarado como Object

        else if (item instanceof MateriaPrima) {
            MateriaPrima materia = (MateriaPrima) item;
            System.out.println("[OK] " + materia.getNome() + " colocada na esteira " + this.numero + ".");
        }

        return true;
    }

    /**
     * Remove o item que está atualmente na esteira caso ela esteja em movimento
     *
     * @return O item removido da esteira ou null se a esteira estiver vazia ou desligada
     */
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
            System.out.println("[OK] " + produto.getNome() + " removido da esteira " + this.numero + ".");
        } 

        else if (itemRemovido instanceof MateriaPrima) {
            MateriaPrima materia = (MateriaPrima) itemRemovido;
            System.out.println("[OK] " + materia.getNome() + " removida da esteira " + this.numero + ".");
        }

        return itemRemovido;
    }

    /**
     * Verifica se a quantidade fornecida não ultrapassa  a capacidade máxima suportada pela esteira
     *
     * @param quantidade A quantidade de peso ou volume a ser testada
     * @return           true se a quantidade for suportada, false caso exceda a capacidade
     */
    public boolean verificarCapacidade(double quantidade) {
        return quantidade <= capacidadeMaxima;
    }

    /**
     * Simula o trajeto do item pela esteira imprimindo uma mensagem de confirmação se foi percorrido
     */
    public void caminhoEsteira(){
        System.out.println("[OK] Caminho da esteira " + this.numero + " percorrido.");
    }

    /**
     * Obtem o número de identificação da esteira
     *
     * @return O número da esteira
     */
    public int getNumero() {
        return numero;
    }
}
