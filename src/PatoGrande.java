public class PatoGrande extends Produto {
    public PatoGrande(int id) {
        super(
            id,
            "Pato Grande", 
            0.6,
            0.7);
    }

    @Override
    public void processar() {
        setStatus("Processando");
    }

    @Override
    public void processado() {
        setStatus("Processado");
        System.out.println( "[OK] Pato Grande acabou de sair do molde!");
    }

    @Override
    public void embalando() {
        setStatus("Embalando");
        System.out.println( "[OK] Pato Grande está sendo embalado...!");
    }

    @Override
    public void embalado() {
        setStatus("Embalado");
        System.out.println( "[OK] Pato Grande acabou de ser embalado!");
    }

   @Override
    public void inspecionando() {
        setStatus("Inspecionando");
        System.out.println( "[OK] Pato Grande está sendo inspecionado...");
    }

    @Override
    public void inspecionado() {
        setStatus("Inspecionado");
        System.out.println( "[OK] Pato Grande acabou de ser inspecionado!");
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
