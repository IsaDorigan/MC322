package model.maquina.componentes;

import model.Auditavel;
import model.exceptions.StatusIlegalException;
import util.Sorteio;

/**
 * Representa o molde utilizado para dar forma à borracha derretida.
 * Com o uso contínuo, acumula resíduos de borracha e sofre desgaste físico,
 * podendo entupir e travar o processo se não for limpo regularmente.
 * 
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class Molde implements Auditavel {
    
    private String tipoFormato;
    private double nivelSujeira;
    private double desgasteFisico;
    private int pecasMoldadasDesdeLimpeza;

    /**
     * Construtor do molde definindo qual o formato de patinho ele produz,
     * inicia limpo e sem nenhum desgaste físico.
     *
     * @param tipoFormato o formato específico deste molde (ex: "Pato Pequeno", "Pato Grande")
     */
    public Molde(String tipoFormato) {
        this.tipoFormato = tipoFormato;
        this.nivelSujeira = 0.0;
        this.desgasteFisico = 0.0;
        this.pecasMoldadasDesdeLimpeza = 0;
    }

    /**
     * Simula a injeção de borracha e a prensagem para moldar um pato
     * Aumenta a sujeira (resíduos de borracha) e o desgaste estrutural do molde
     * 
     * @throws StatusIlegalException se o molde estiver completamente entupido ou estruturalmente quebrado
     */
    public void injetarEMoldar() {
        if (nivelSujeira >= 100.0) {
            throw new StatusIlegalException("O molde de " + tipoFormato + " entupiu completamente com residuos de borracha! Limpeza necessaria.");
        }
        
        if (desgasteFisico >= 100.0) {
            throw new StatusIlegalException("O molde de " + tipoFormato + " quebrou devido a desgaste fisico extremo e precisa ser substituido.");
        }
        
        // Acúmulo de sujeira é rápido, desgaste físico é mais lento
        this.nivelSujeira += Sorteio.entre(2.0, 5.0);
        this.desgasteFisico += Sorteio.entre(0.1, 0.5);
        this.pecasMoldadasDesdeLimpeza++;
    }

    /**
     * Limpa o molde
     */
    public void limpar() {
        this.nivelSujeira = 0.0;
        this.pecasMoldadasDesdeLimpeza = 0;
    }

    /**
     * Verifica se o molde precisa de manutenção 
     *
     * @return true se a sujeira ultrapassar 75% ou o desgaste ultrapassar 80%, false caso contrário
     */
    @Override
    public boolean precisaManutencao() {
        return nivelSujeira > 75.0 || desgasteFisico > 80.0;
    }

    /**
     * Gera um relatório com o diagnóstico de limpeza e se o molde está bom
     *
     * @return uma string informando o status, o nível de sujeira e o desgaste físico
     */
    @Override
    public String gerarRelatorioDiagnostico() {
        String status;
        if (desgasteFisico > 80.0) {
            status = "ATENCAO (Desgaste Critico)";
        } else if (nivelSujeira > 75.0) {
            status = "ATENCAO (Precisa de Limpeza)";
        } else {
            status = "OK";
        }
        
        return String.format("Molde (%s): [%s] | Sujeira: %.1f%% | Desgaste Estrutural: %.1f%% | Pecas feitas: %d", 
                tipoFormato, status, nivelSujeira, desgasteFisico, pecasMoldadasDesdeLimpeza);
    }

    /**
     * @return o formato do molde
     */
    public String getTipoFormato() {
        return tipoFormato;
    }
}