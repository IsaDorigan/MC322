public class Esteira {

    private Object item;
    private boolean emMovimento;
    private double capacidadeMaxima;

    public Esteira(double capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
        this.emMovimento = false;
        this.item = null;
    }

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

        System.out.println("[OK] Item colocado na esteira.");
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

        System.out.println("[OK] Item removido da esteira.");

        return itemRemovido;
    }

    public boolean verificarCapacidade(double quantidade) {
        return quantidade <= capacidadeMaxima;
    }
}
