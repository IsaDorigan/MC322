package model;

import model.exceptions.ArgumentoIlegalException;
import model.exceptions.RecursoInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Classe de testes unitários para MateriaPrima.
 */
public class MateriaPrimaTest {

    private MateriaPrima borracha;

    /**
     * Configuração inicial executada antes de cada teste
     */
    @BeforeEach
    void setUp() {
        // Inicializa: id, nome, quantidade, unidade, custo, reserva minima
        borracha = new MateriaPrima(1, "Borracha", 100.0, "kg", 5.0, 10.0);
    }

    /**
     * Testa se o consumo de uma quantidade válida de matéria-prima 
     * deduz corretamente o valor do estoque total.
     */
    @Test
    void testConsumoValidoDiminuiEstoque() {
        borracha.consumir(50.0);
        assertEquals(50.0, borracha.getQuantidade(), 0.001, "O estoque deve diminuir corretamente.");
    }

    /**
     * Testa se o sistema bloqueia o consumo e lança a exceção adequada 
     * quando a operação tenta ultrapassar o limite da reserva mínima de segurança.
     */
    @Test
    void testConsumoAbaixoDaReservaLancaExcecao() {
        // Tenta consumir 95kg (sobrariam 5kg, mas a reserva é 10kg)
        assertThrows(RecursoInsuficienteException.class, () -> {
            borracha.consumir(95.0);
        }, "Deve lançar exceção ao tentar ultrapassar a reserva mínima.");
    }

    /**
     * Testa se o sistema impede o consumo de valores negativos, 
     * barrando a operação com a exceção coreta.
     */
    @Test
    void testConsumoNegativoLancaExcecao() {
        assertThrows(ArgumentoIlegalException.class, () -> {
            borracha.consumir(-10.0);
        }, "Não deve aceitar consumir valores negativos.");
    }
}