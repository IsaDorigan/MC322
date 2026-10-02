/**
 * Contrato para qualquer componente da fábrica que possa ser auditado
 * (máquinas, produtos, etc.), mesmo que não tenham uma classe pai em comum.
 */
public interface Auditavel {

    /**
     * Gera um relatório com o estado atual de saúde ou qualidade do componente
     *
     * @return O diagnóstico formatado em texto para uso em relatórios de auditoria
     */
    String gerarRelatorioDiagnostico();

   /**
    * Verifica se o componente precisa de intervenção
    *
    * @return true se o componente precisa de manutenção, false caso contrário
    */
    boolean precisaManutencao();
}
