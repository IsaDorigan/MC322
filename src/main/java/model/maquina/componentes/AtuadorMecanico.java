package model.maquina.componentes;

import model.exceptions.ArgumentoIlegalException;

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

    /**
     * Instantâneo imutável do estado variável do atuador.
     *
     * @param funcao        função (identificação) do atuador
     * @param ciclosUso     ciclos de uso desde a última lubrificação
     * @param forcaAplicada força aplicada pelo atuador, em newtons
     */
    public record Estado(String funcao, int ciclosUso, double forcaAplicada) { }

    /**
     * Obtém uma cópia imutável do estado atual do atuador.
     *
     * @return o estado atual do atuador
     */
    public Estado obterEstado() {
        return new Estado(funcao, ciclosUso, forcaAplicada);
    }

    /**
     * Restaura o estado variável do atuador a partir de um instantâneo, validando os valores.
     *
     * @param estado o estado a ser restaurado
     * @throws ArgumentoIlegalException se o estado for nulo, de outro atuador ou tiver valores inválidos
     */
    public void restaurarEstado(Estado estado) {
        if (estado == null) {
            throw new ArgumentoIlegalException("Estado do atuador ausente.");
        }
        if (!funcao.equals(estado.funcao())) {
            throw new ArgumentoIlegalException("Atuador incompativel: esperado " + funcao
                    + ", mas os dados informam " + estado.funcao() + ".");
        }
        if (estado.ciclosUso() < 0) {
            throw new ArgumentoIlegalException("Os ciclos de uso do atuador " + funcao + " nao podem ser negativos.");
        }
        this.ciclosUso = estado.ciclosUso();
    }
}
