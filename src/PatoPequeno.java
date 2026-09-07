public class PatoPequeno extends Produto {
    public PatoPequeno(int id) {
        super(
            id,
            "Pato Pequeno", 
            0.3,
            0.5    
        );
    }

    @Override
    public void processar() {
        setStatus("Processando");
    }

    @Override
    public void processado() {
        setStatus("Processado");
        System.out.println( "[OK] Pato Pequeno acabou de sair do molde!");
    }

    @Override
    public void embalando() {
        setStatus("Embalando");
        System.out.println( "[OK] Pato Pequeno está sendo embalado...!");
    }

    @Override
    public void embalado() {
        setStatus("Embalado");
        System.out.println( "[OK] Pato Pequeno acabou de ser embalado!");
    }

   @Override
    public void inspecionando() {
        setStatus("Inspecionando");
        System.out.println( "[OK] Pato Pequeno está sendo inspecionado...");
    }

    @Override
    public void inspecionado() {
        setStatus("Inspecionado");
        System.out.println( "[OK] Pato Pequeno acabou de ser inspecionado!");
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