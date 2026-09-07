public class MaquinaEmbalagem extends Maquina {

    // Chance de aumentar a probabilidade de falha
    private static final double CHANCE_AUMENTAR_FALHA = 0.15;

    public MaquinaEmbalagem() {
        super(
                "Embaladora de Patinhos",
                50.0,
                0.0,
                10.0
        );
    }

    @Override
    public Produto processar(Produto produto) {

        if (!estaLigada()) { 
            System.out.println("[ERRO] A embaladora está desligada.");
            return null;
        }

        if (produto == null) {
            System.out.println("[ERRO] Produto inexistente.");
            return null;
        }

        System.out.println("[OK] Embalando " + produto.getNome() + "...");

        produto.setStatus("embalado");
        System.out.println("[OK] " + produto.getNome() + " embalado.");

        // A máquina não falha diretamente, ela pode aumentar a chance de falha do produto.
        if (Math.random() < CHANCE_AUMENTAR_FALHA) {
            produto.aumentarProbabilidadeFalha(0.15);
            System.out.println("[AVISO] A embalagem aumentou a probabilidade de falha.");
        }

        return produto;
    }

    @Override
    public String getTipo() {
        return "Embalagem";
    }
}