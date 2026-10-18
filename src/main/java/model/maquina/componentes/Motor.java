package model.maquina.componentes;

import model.Auditavel;
import model.exceptions.StatusIlegalException;
import util.Sorteio;

/**
 * Representa o motor de uma maquina na fabrica.
 * Possui ciclo de vida próprio, gerando calor e desgaste a cada operacão
 * 
 * @author Isadora KLuge Dorigan e Guilherme Forte Silva
 */
public class Motor implements Auditavel {
    
    private String modelo;
    private boolean ligado;
    private double desgaste;
    private double temperaturaAtual;

    /**
     * Construtor do motor que inicia o modelo, estado, desgaste e temperatura
     *
     * @param modelo O nome ou código do modelo do motor
     */
    public Motor(String modelo) {
        this.modelo = modelo;
        this.ligado = false;
        this.desgaste = 0.0;
        this.temperaturaAtual = 25.0; // Temperatura ambiente inicial
    }

    /**
     * Liga o motor
     */
    public void ligar() {
        this.ligado = true;
    }

    /**
     * Desliga o motor
     */
    public void desligar() {
        this.ligado = false;
        this.temperaturaAtual = 25.0; // Esfria ao ser desligado
    }

    /**
     * Simula o funcionamento do motor, aumentando a temperatura e o desgaste
     * 
     * @throws StatusIlegalException see houver tentativa de operar o motor enquanto ele estiver desligado
     */ 
    public void operar() {
        if (!ligado) {
            throw new StatusIlegalException("Tentativa de operar o motor " + modelo + " enquanto ele esta desligado.");
        }
        
        this.desgaste += Sorteio.entre(0.5, 2.0);
        this.temperaturaAtual += Sorteio.entre(5.0, 15.0);
    }

    /**
     * Obtem a temperatura atual do motor em graus 
     *
     * @return A temperatura medida no momento
     */
    public double getTemperaturaAtual() {
        return temperaturaAtual;
    }


    /**
     * Verifica se o motor precisa de manutenção pelo aalto desgaste ou superaquecimento
     *
     * @return true se o desgaste ultrapassar 30% ou a temperatura exceder 90°C, false caso contrário
     */
    @Override
    public boolean precisaManutencao() {
        // O motor precisa de atenção se desgastar muito ou superaquecer
        return desgaste > 30.0 || temperaturaAtual > 90.0;
    }

    /**
     * Gera um relatório com o status de operação e integridade do motor
     *
     * @return Uma string contendo o status, desgaste e temperatura atual do motor
     */
    @Override
    public String gerarRelatorioDiagnostico() {
        String status = precisaManutencao() ? "ATENÇÃO (Superaquecido/Desgastado)" : "OK";
        return String.format("Motor %s: [%s] | Desgaste: %.1f%% | Temperatura: %.1f°C", 
                modelo, status, desgaste, temperaturaAtual);
    }

    /**
     * Zera o desgaste do motor em caso de reparo geral da máquina
     */
    public void revisar() {
        this.desgaste = 0.0;
    }
}