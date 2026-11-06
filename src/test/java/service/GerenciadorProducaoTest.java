package service;

import model.Demanda;
import model.Esteira;
import model.LinhaDeProducao;
import model.MateriaPrima;
import model.enums.Cenario;
import model.maquina.MaquinaEmbalagem;
import model.maquina.MaquinaInspecao;
import model.maquina.MaquinaMoldagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import strategy.EstrategiaFilaIndiana;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes unitários para GerenciadorProducao.
 */
public class GerenciadorProducaoTest {

    private GerenciadorProducao gerenciador;

    /**
     * Configuração inicial executada antes de cada teste
     */
    @BeforeEach
    void setUp() {
        MateriaPrima mp = new MateriaPrima(1, "Borracha", 200.0, "kg", 5.0, 5.0);
        LinhaDeProducao linha = new LinhaDeProducao("Bancada Teste");
        linha.adicionarMaquina(new MaquinaMoldagem(Cenario.IDEAL));
        linha.adicionarMaquina(new MaquinaEmbalagem(Cenario.IDEAL));
        linha.adicionarMaquina(new MaquinaInspecao(Cenario.IDEAL));

        gerenciador = new GerenciadorProducao(mp, Cenario.IDEAL,
                new Esteira(100, 1), new Esteira(100, 2), new Esteira(100, 3),
                new EstrategiaFilaIndiana(), linha);
    }

    /**
     * Testa se o registro de uma nova demanda reflete corretamente 
     * no tamanho da fila de demandas da fábrica.
     */
    @Test
    void testRegistrarDemandaAumentaFila() {
        gerenciador.registrarDemanda(new Demanda("Pato Pequeno", 10));
        assertEquals(1, gerenciador.getDemandas().size(), "A fila de demandas deve ter 1 item.");
    }

    /**
     * Testa o controle orçamentário verificando se a compra de novos insumos 
     * desconta o valor exato e proporcional do budget disponível.
     */
    @Test
    void testCompraDeMateriaPrimaDescontaBudget() {
        double budgetInicial = gerenciador.getBudget();
        double quantidadeComprada = 10.0;
        
        gerenciador.comprarMateriaPrima(quantidadeComprada);
        
        double custoEsperado = quantidadeComprada * gerenciador.getMateriaPrima().getCustoPorUnidade();
        assertEquals(budgetInicial - custoEsperado, gerenciador.getBudget(), 0.001, 
            "O valor da compra deve ser descontado do budget corretamente.");
    }
}