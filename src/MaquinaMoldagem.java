public class MaquinaMoldagem extends Maquina {

    private static final double chanceAumentarFalha = 0.1;

    public MaquinaMoldagem() {
        super("Moldadora de Patinhos",
            50,
            0,
            15
        );
    }

    @Override
    public Produto processar(Produto produto) {
        // Se maquina não estiver ligada, para a produção
        if(!estaLigada()){
            System.out.println("[ERRO] A moldadora está desligada.");
            return null;
        }
        // Se o produto for nulo, para a produção
        if(produto == null) {
            System.out.println("[ERRO] Produto não existe.");
            return null;
        }
        
        System.out.println("[OK] Moldando " + produto.getNome() + "...");

        produto.processar();

        // A moldadora não falha diretamente, ela pode aumentar a chance de falha do produto.
        if (Math.random() < chanceAumentarFalha) {
            produto.aumentarProbabilidadeFalha(0.10);
            System.out.println("[AVISO] A moldagem aumentou a probabilidade de falha.");
        }

        produto.processado();

        return produto;
    }

    @Override
    public String getTipo() {
        return "Moldagem";
    }
}