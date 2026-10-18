package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.exceptions.ArgumentoIlegalException;
import model.maquina.Maquina;

/**
 * Representa o Agrupador das unidades produtivas (Máquinas) da fábrica.
 * Faz uma relação de Agregação (1:N), onde as máquinas têm ciclo de vida independente e podem ser adicionadas 
 * ou removidas da linha.
 * 
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class LinhaDeProducao implements Auditavel {

    private String nome;
    private List<Maquina> maquinas; 

    /**
     * Construtor do agrupador que inicializa uma nova linha vazia
     *
     * @param nome o nome de identificação desta linha
     */
    public LinhaDeProducao(String nome) {
        this.nome = nome;
        this.maquinas = new ArrayList<>();
    }

    /**
     * Instala uma nova máquina na linha de produção
     *
     * @param maquina A máquina a ser adicionada
     * @throws ArgumentoIlegalException se tentar adicionar uma máquina nula
     */
    public void adicionarMaquina(Maquina maquina) {
        if (maquina == null) {
            throw new ArgumentoIlegalException("Nao e possivel adicionar uma maquina nula a linha de producao.");
        }
        this.maquinas.add(maquina);
        System.out.println("[OK] " + maquina.getNome() + " instalada na " + nome + ".");
    }

    /**
     * Desinstala uma máquina da linha de produção
     * Isso prova que a relação é de Agregação, pois a máquina continua existindo mesmo fora da linha
     *
     * @param maquina a máquina a ser removida
     * @return true se a máquina estava na linha e foi removida, false caso contrário
     */
    public boolean removerMaquina(Maquina maquina) {
        boolean removida = this.maquinas.remove(maquina);
        if (removida) {
            System.out.println("[OK] " + maquina.getNome() + " foi desinstalada da " + nome + ".");
        }
        return removida;
    }

    /**
     * Retorna a lista de máquinas instaladas nesta linha, e utiliza proteção de coleção para impedir que classes
     *  externas modifiquem a estrutura interna (adicionando ou removendo) diretamente
     *
     * @return Uma lista imutável contendo as máquinas da linha
     */
    public List<Maquina> getMaquinas() {
        // TAREFA 3.6: Proteção da Coleção
        return Collections.unmodifiableList(maquinas);
    }

    /**
     * Obtem o nome da linha de produção
     *
     * @return o nome da linha
     */
    public String getNome() {
        return nome;
    }

    /**
     * Verifica se alguma maquina precisa de manutenção
     *
     * @return true se pelo menos uma máquina (ou peça dela) precisar de atenção
     */
    @Override
    public boolean precisaManutencao() {
        for (Maquina maquina : maquinas) {
            if (maquina.precisaManutencao()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Faz um relatorio de diagnóstico para as maquinas
     *
     * @return uma string hierárquica detalhando toda a linha de produção
     */
    @Override
    public String gerarRelatorioDiagnostico() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(nome.toUpperCase()).append(" ===");

        if (maquinas.isEmpty()) {
            sb.append("\n  [Linha Vazia - Nenhuma maquina instalada]");
        }

        for (Maquina maquina : maquinas) {
            // O Agrupador chama o relatório do Todo (Máquina), 
            // que por sua vez chama o das Partes (Motor, Sensor, etc)
            sb.append("\n-> ").append(maquina.gerarRelatorioDiagnostico());
        }

        return sb.toString();
    }
}