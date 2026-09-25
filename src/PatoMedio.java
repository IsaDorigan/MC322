public class PatoMedio extends Produto {

    public PatoMedio(int id) {
        super(
            id,
            "Pato Médio",
            0.45,  // kg de borracha por unidade
            0.6    // qualidade
        );
    }

    @Override
    public double calcularTempoProducao() {
        return 3.0;
    }

    @Override
    public String getTipo() {
        return "Pato Médio";
    }
}