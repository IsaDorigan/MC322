package model.maquina.componentes;

import model.Auditavel;
import model.exceptions.ArgumentoIlegalException;
import util.Sorteio;

/**
 * Sensor responsável por monitorar o calor interno da máquina.
 * Com o uso contínuo, pode perder a calibração e precisar de manutenção.
 * 
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class SensorTemperatura implements Auditavel {
    
    private String codigoIdentificacao;
    private boolean calibrado;
    private int leiturasRealizadas;

    /**
     * Construtor do senso de temperatura que o inicializa já pronto para uso
     *
     * @param codigoIdentificacao O código único de identificação deste sensor
     */
    public SensorTemperatura(String codigoIdentificacao) {
        this.codigoIdentificacao = codigoIdentificacao;
        this.calibrado = true;
        this.leiturasRealizadas = 0;
    }

    /**
     * Faz a leitura da temperatura de um componente 
     * Se o sensor estiver descalibrado dpelo uso excessivo, adiciona ruído a leitura
     * 
     * @param temperaturaReal a temperatura real do componente que está sendo monitorado
     * @return a temperatura capturada pelo sensor, podendo conter distorções se descalibrado
     * @throws ArgumentoIlegalException se a temperatura fornecida for fisicamente impossível (abaixo do zero absoluto)
     */
    public double lerTemperatura(double temperaturaReal) {
        if (temperaturaReal < -273.15) {
            throw new ArgumentoIlegalException("Temperatura invalida no sensor " + codigoIdentificacao + 
                    ": valores abaixo do zero absoluto (-273.15°C) sao impossiveis.");
        }

        leiturasRealizadas++;
        
        // Com o tempo o sensor pode descalibrar
        if (leiturasRealizadas > 20 && Sorteio.ocorre(0.2)) {
            calibrado = false;
        }
        
        if (!calibrado) {
            // Se estiver descalibrado, aplica um ruído na leitura
            return temperaturaReal + Sorteio.entre(-15.0, 15.0);
        }
        
        return temperaturaReal;
    }

    /**
     * Restaura a precisão do sensor, redefinindo o contador de leituras e marcando-o como calibrado
     */
    public void calibrar() {
        this.calibrado = true;
        this.leiturasRealizadas = 0;
    }

    /**
     * Verifica se o sensor perdeu sua calibração e precisa de intervençao
     *
     * @return true se o sensor estiver descalibrado, false caso contrário
     */
    @Override
    public boolean precisaManutencao() {
        return !calibrado;
    }

    /**
     * Gera um relatório formatado com o diagnóstico de precisão e uso do sensor.
     *
     * @return uma string contendo o status de calibração e o número de leituras realizadas
     */
    @Override
    public String gerarRelatorioDiagnostico() {
        String status = calibrado ? "OK" : "DESCALIBRADO";
        return String.format("Sensor de Temperatura %s: [%s] | Leituras desde calibracao: %d", 
                codigoIdentificacao, status, leiturasRealizadas);
    }
}