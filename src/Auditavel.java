/**
 * Contrato para qualquer componente da fábrica que possa ser auditado
 * (máquinas, produtos, etc.), mesmo que não tenham uma classe pai em comum.
 */
public interface Auditavel {

    // Estado de saúde/qualidade do componente, em texto, para relatórios
    String gerarRelatorioDiagnostico();

    // Indica se o componente precisa de intervenção
    boolean precisaManutencao();
}
