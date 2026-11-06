package model;

import model.enums.StatusDemanda;
import model.exceptions.ArgumentoIlegalException;
import model.exceptions.StatusIlegalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes unitários para Demanda
 */
public class DemandaTest {

    private Demanda demanda;

    /**
     * Configuração inicial executada antes de cada teste.
     * Instancia uma nova demanda padrão para garantir um estado limpo.
     */
    @BeforeEach
    void setUp() {
        demanda = new Demanda("Pato Medio", 50);
    }

    /**
     * Testa se a atualização da quantidade de produtos para zero altera o status da demanda para CANCELADA.
     */
    @Test
    void testAtualizarQuantidadeZeroCancelaDemanda() {
        demanda.atualizarQuantidade(0);
        assertEquals(StatusDemanda.CANCELADA, demanda.getStatus(), "Quantidade 0 deve cancelar a demanda.");
    }

    /**
     * Testa se o sistema bloqueia a atribuição de uma quantidade negativa lançando a exceção adequada.
     */
    @Test
    void testAtualizarQuantidadeNegativaLancaExcecao() {
        assertThrows(ArgumentoIlegalException.class, () -> {
            demanda.atualizarQuantidade(-5);
        }, "Quantidade negativa deve lançar ArgumentoIlegalException.");
    }

    /**
     * Testa se a transição de estado ocorre corretamente ao iniciar o processo de produção da demanda.
     */
    @Test
    void testIniciaProducaoMudaStatus() {
        demanda.iniciarProducao();
        assertEquals(StatusDemanda.EM_PRODUCAO, demanda.getStatus(), "Status deve mudar para EM_PRODUCAO.");
    }

    /**
     * Testa se a demanda possui trava de segurança contra cancelamentos indevidos após já ter sido concluída.
     */
    @Test
    void testNaoPodeCancelarDemandaConcluida() {
        // Força a demanda a ficar zerada em produção (simulando que produziu tudo)
        demanda.iniciarProducao();
        demanda.registrarProduzidos(50);
        demanda.concluir();

        assertThrows(StatusIlegalException.class, () -> {
            demanda.cancelar();
        }, "Não pode cancelar uma demanda que já foi concluída.");
    }
}