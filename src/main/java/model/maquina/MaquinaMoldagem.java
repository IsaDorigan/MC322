package model.maquina;

import model.exceptions.ArgumentoIlegalException;
import java.util.ArrayList;
import java.util.List;

import model.Produto;
import model.enums.Cenario;
import model.exceptions.StatusIlegalException;
import model.maquina.componentes.Molde;

/**
 * Representa a máquina responsável por moldar a borracha e fazer virar patinhos
 */
public class MaquinaMoldagem extends Maquina {

    // Chance da moldagem aumentar o risco de falha do produto, e quanto aumenta
    private static final double CHANCE_AUMENTAR_RISCO = 0.10;
    private static final double AUMENTO_RISCO = 0.10;

    // Componente exclusivo da moldadora
    private final List<Molde> moldes;

    /**
     * Construtor da máquina de moldagem que define os parâmetros padrão e ajusta as probabilidades 
     * com base no cenário
     *
     * @param cenario O cenário atual da simulação que afeta os parâmetros da máquina
     */
    public MaquinaMoldagem(Cenario cenario) {
        super("Moldadora de Patinhos",
                50,
                0.04,
                15,
                cenario,
                2);
        
        // Instancia os 3 moldes específicos
        this.moldes = new ArrayList<>();
        this.moldes.add(new Molde("Pato Pequeno"));
        this.moldes.add(new Molde("Pato Medio"));
        this.moldes.add(new Molde("Pato Grande"));
    }

    /**
     * Executa o processo de moldagem do produto podendo aumentar seu risco de defeito dependendo 
     * da sorte e do cenário
     *
     * @param produto O produto que receberá a forma no molde
     * @return        O produto processado após passar pela etapa de moldagem
     */
    @Override
    protected Produto executarProcessamento(Produto produto) {
        System.out.println("[OK] Moldando " + produto.getNome() + "...");

        produto.processar();

        // Busca o molde correto para o tipo do produto
        Molde moldeCorreto = selecionarMolde(produto.getTipo());
        
        // O molde específico trabalha, sujando e desgastando apenas ele próprio
        moldeCorreto.injetarEMoldar();

        possivelmenteAumentarRisco(produto, CHANCE_AUMENTAR_RISCO, AUMENTO_RISCO,
                "A moldagem aumentou a probabilidade de falha.");

        produto.processado();

        return produto;
    }

    /**
     * Procura na lista de moldes internos qual é o molde compatível com o produto atual
     */
    private Molde selecionarMolde(String tipoProduto) {
        for (Molde m : moldes) {
            // Compara o tipo do formato do molde com o tipo do produto.
            // Para isso, no Molde.java, você precisa ter um getter: getTipoFormato()
            if (m.getTipoFormato().equals(tipoProduto)) {
                return m;
            }
        }
        throw new StatusIlegalException("Molde para " + tipoProduto + " não foi encontrado na máquina!");
    }

    /**
     * Quando a máquina for reparada, ela limpa todos os moldes
     */ 
    @Override
    public void reparar() {
        super.reparar();
        for (Molde m : moldes) {
            m.limpar();
        }
    }

    /** 
     * A máquina só está boa se ela mesma estiver boa e nenhum molde estiver sujo 
    */
    @Override
    public boolean precisaManutencao() {
        boolean algumMoldeSujo = false;
        for (Molde m : moldes) {
            if (m.precisaManutencao()) {
                algumMoldeSujo = true;
            }
        }
        return super.precisaManutencao() || algumMoldeSujo;
    }

    /**
     * Obtem a mensagem exibida quando ocorre uma falha de operação nesta máquina
     *
     * @return A mensagem alertando sobre a falha no molde
     */
    @Override
    protected String getMensagemFalha() {
        return "[ERRO] Quá! O molde entupiu de borracha e o patinho se perdeu!";
    }

    /**
     * Obtem o tipo de operação que esta máquina realiza dentro da linha de produção
     *
     * @return Uma string representando o tipo da máquina
     */
    @Override
    public String getTipo() {
        return "Moldagem";
    }

    @Override
    protected List<Molde.Estado> obterEstadosMoldes() {
        List<Molde.Estado> estados = new ArrayList<>();
        for (Molde molde : moldes) {
            estados.add(molde.obterEstado());
        }
        return estados;
    }

    @Override
    protected void restaurarEstadosMoldes(List<Molde.Estado> estadosMoldes) {
        if (estadosMoldes.size() != moldes.size()) {
            throw new ArgumentoIlegalException("A " + getNome() + " possui " + moldes.size()
                    + " moldes, mas os dados informam " + estadosMoldes.size() + ".");
        }
        for (int i = 0; i < moldes.size(); i++) {
            moldes.get(i).restaurarEstado(estadosMoldes.get(i));
        }
    }
}
