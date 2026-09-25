public class PatoPequeno extends Produto {

    public PatoPequeno(int id) {
        super(
            id,
            "Pato Pequeno",
            0.3,   // kg de borracha por unidade
            0.5    // qualidade
        );
    }

    @Override
    public double calcularTempoProducao() {
        return 2.0;
    }

    @Override
    public String getTipo() {
        return "Pato Pequeno";
    }
}