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

    /**
     * Instantâneo imutável do estado variável do sensor.
     *
     * @param codigoIdentificacao código de identificação do sensor
     * @param calibrado           se o sensor está calibrado
     * @param leiturasRealizadas  leituras feitas desde a última calibração
     */
    public record Estado(String codigoIdentificacao, boolean calibrado, int leiturasRealizadas) { }

    /**
     * Obtém uma cópia imutável do estado atual do sensor.
     *
     * @return o estado atual do sensor
     */
    public Estado obterEstado() {
        return new Estado(codigoIdentificacao, calibrado, leiturasRealizadas);
    }

    /**
     * Restaura o estado variável do sensor a partir de um instantâneo, validando os valores.
     *
     * @param estado o estado a ser restaurado
     * @throws ArgumentoIlegalException se o estado for nulo, de outro sensor ou tiver valores inválidos
     */
    public void restaurarEstado(Estado estado) {
        if (estado == null) {
            throw new ArgumentoIlegalException("Estado do sensor ausente.");
        }
        if (!codigoIdentificacao.equals(estado.codigoIdentificacao())) {
            throw new ArgumentoIlegalException("Sensor incompativel: esperado " + codigoIdentificacao
                    + ", mas os dados informam " + estado.codigoIdentificacao() + ".");
        }
        if (estado.leiturasRealizadas() < 0) {
            throw new ArgumentoIlegalException("O numero de leituras do sensor " + codigoIdentificacao
                    + " nao pode ser negativo.");
        }
        this.calibrado = estado.calibrado();
        this.leiturasRealizadas = estado.leiturasRealizadas();
    }
}
