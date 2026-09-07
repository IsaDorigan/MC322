public class MaquinaInspecao extends Maquina {

    public MaquinaInspecao() {
        super(
                "Máquina de Inspeção",
                50.0,
                0.10,
                20.0
        );
    }

    @Override
    public Produto processar(Produto produto) {
        if (!estaLigada()) {
            System.out.println("[ERRO] A máquina de inspeção está desligada.");
            return null;
        }

        if (produto == null) {
            System.out.println("[ERRO] Produto inexistente.");
            return null;
        }

        System.out.println("[OK] Iniciando inspeção...");

        // A máquina pode falhar diretamente
        if (verificarFalha()) {
            System.out.println("[ERRO] A máquina de inspeção apresentou uma falha!");
            produto.setStatus("Inspeção Falhou");
            return null;
        }

        // A qualidade é diretamente proporcional à chance de rejeição na inspeção
        // Somamos essa chance de rejeição devido à qualidade com a probabilidade de falha acumulada
        double chanceRejeicao = (produto.getQualidade() * 0.30) + produto.getProbabilidadeFalhaAcumulada();

        // Limita a chance a 100%
        if (chanceRejeicao > 1.0) {
            chanceRejeicao = 1.0;
        }

        if (Math.random() < chanceRejeicao) {
            produto.setStatus("Rejeitado");
            System.out.println("[ERRO] " + produto.getNome() + " foi rejeitado na inspeção.");
            return null;
        }

        produto.setStatus("Inspecionado");

        System.out.println("[OK] " + produto.getNome() + " aprovado na inspeção!");

        return produto;
    }

    @Override
    public String getTipo() {
        return "Inspeção";
    }
}