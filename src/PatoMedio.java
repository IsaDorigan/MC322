public class PatoMedio extends Produto {
    public PatoMedio(int id) {
        super(
            id,
            "Pato Medioo", 
            0.45,
            0.6);
    }

    @Override
    public void processar() {
        setStatus("Processando");
    }

    @Override
    public void processado() {
        setStatus("Processado");
        System.out.println( "[OK] Pato Médio acabou de sair do molde!");
    }

    @Override
    public void embalando() {
        setStatus("Embalando");
        System.out.println( "[OK] Pato Médio está sendo embalado...!");
    }

    @Override
    public void embalado() {
        setStatus("Embalado");
        System.out.println( "[OK] Pato Médio acabou de ser embalado!");
    }

   @Override
    public void inspecionando() {
        setStatus("Inspecionando");
        System.out.println( "[OK] Pato Médio está sendo inspecionado...");
    }

    @Override
    public void inspecionado() {
        setStatus("Inspecionado");
        System.out.println( "[OK] Pato Médio acabou de ser inspecionado!");
    }

    @Override
    public double calcularTempoProducao() {
        return 3.0;
    }

    @Override
    public String getTipo() {
        return "Pato Medio";
    }
}