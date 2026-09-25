public class PatoGrande extends Produto {

    public PatoGrande(int id) {
        super(
            id,
            "Pato Grande",
            0.6,   // kg de borracha por unidade
            0.7    // qualidade
        );
    }

    @Override
    public double calcularTempoProducao() {
        return 4.0;
    }

    @Override
    public String getTipo() {
        return "Pato Grande";
    }
}