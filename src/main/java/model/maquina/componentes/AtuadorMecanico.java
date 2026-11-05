package model.maquina.componentes;

import model.Auditavel;
import model.exceptions.StatusIlegalException;
import util.Sorteio;

/**
 * Representa as partes móveis da máquina (como braços robóticos, prensas e cilindros, etc).
 * Precisa de lubrificação regular para não travar devido ao atrito.
 * 
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class AtuadorMecanico implements Auditavel {
    
    private String funcao;
    private int ciclosUso;
    private double forcaAplicada;

    /**
     * Construtor do atuador mecânico definindo sua função específica e a força que exerce
     *
     * @param funcao a finalidade mecânica deste atuador (ex: "Prensa do Molde", "Braco Embalador")
     * @param forcaAplicada a quantidade de força padrão em Newtons aplicada pelo atuador
     */
    public AtuadorMecanico(String funcao, double forcaAplicada) {
        this.funcao = funcao;
        this.forcaAplicada = forcaAplicada;
        this.ciclosUso = 0;
    }

    /**
     * Tenta movimentar o atuador para executar um processo mecânico
     * O excesso de ciclos sem lubrificação aumenta a probabilidade de travamento
     * 
     * @throws StatusIlegalException se o componente travar por excesso de atrito e falta de lubrificação
     */
    public void acionar() {
        ciclosUso++;
        
        // Se já passou de muitos ciclos, pode  travar durante o acionamento
        if (ciclosUso > 40 && Sorteio.ocorre(0.15)) {
            throw new StatusIlegalException("O atuador mecanico (" + funcao + ") travou por falta de lubrificacao!");
        }
    }

    /**
     * Aplica lubrificação no componente, zerando o desgaste de fricção e os ciclos de uso contínuo
     */
    public void lubrificar() {
        this.ciclosUso = 0;
    }

    /**
     * Verifica se o atuador ultrapassou o limite seguro de uso sem lubrificação
     *
     * @return true se o número de ciclos de uso exceder 35, false caso contrário
     */
    @Override
    public boolean precisaManutencao() {
        // Exige manutenção preventiva após 35 usos
        return ciclosUso > 35;
    }

    /**
     * Gera um relatório descrevendo o status atual de funcionamento do atuador
     *
     * @return uma string informando se precisa de lubrificação, os ciclos executados e a força aplicada
     */
    @Override
    public String gerarRelatorioDiagnostico() {
        String status = precisaManutencao() ? "PRECISA DE LUBRIFICACAO" : "OK";
        return String.format("Atuador Mecanico (%s): [%s] | Ciclos: %d | Forca: %.1fN", 
                funcao, status, ciclosUso, forcaAplicada);
    }
}
