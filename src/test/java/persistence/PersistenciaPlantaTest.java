package persistence;

import model.Demanda;
import model.Esteira;
import model.LinhaDeProducao;
import model.MateriaPrima;
import model.enums.Cenario;
import model.exceptions.ArquivoInexistenteException;
import model.exceptions.DadosInvalidosException;
import model.maquina.MaquinaEmbalagem;
import model.maquina.MaquinaInspecao;
import model.maquina.MaquinaMoldagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import service.GerenciadorProducao;
import strategy.EstrategiaFilaIndiana;
import strategy.EstrategiaProducao;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes unitários para PersistenciaPlanta
 */
public class PersistenciaPlantaTest {

    /**
     * Diretório temporário gerenciado automaticamente pelo JUnit.
     * É criado antes da execução dos testes e deletado logo após
     */
    @TempDir
    Path diretorioTemporario; // O JUnit cria e deleta essa pasta automaticamente

    private Path arquivoXmlTemp;
    private PersistenciaPlanta persistencia;
    private GerenciadorProducao gerenciadorOriginal;
    private List<EstrategiaProducao> estrategiasDisponiveis;

    /**
     * Configuração inicial executada antes de cada teste.
     * Cria um arquivo XML temporario e monta um estado genérico da fábrica 
     * com insumos, linha de produção, máquinas e demandas 
     */
    @BeforeEach
    void setUp() {
        arquivoXmlTemp = diretorioTemporario.resolve("planta_teste.xml");
        persistencia = new PersistenciaPlanta(arquivoXmlTemp);
        estrategiasDisponiveis = List.of(new EstrategiaFilaIndiana());

        // Monta uma fábrica com dados genéricos
        MateriaPrima mp = new MateriaPrima(1, "Borracha", 300.0, "kg", 5.0, 10.0);
        LinhaDeProducao linha = new LinhaDeProducao("Linha 1");
        linha.adicionarMaquina(new MaquinaMoldagem(Cenario.IDEAL));
        linha.adicionarMaquina(new MaquinaEmbalagem(Cenario.IDEAL));
        linha.adicionarMaquina(new MaquinaInspecao(Cenario.IDEAL));

        gerenciadorOriginal = new GerenciadorProducao(mp, Cenario.IDEAL,
                new Esteira(50, 1), new Esteira(50, 2), new Esteira(50, 3),
                estrategiasDisponiveis.get(0), linha);
        
        // Adiciona uma demanda para salvar junto
        gerenciadorOriginal.registrarDemanda(new Demanda("Pato Grande", 15));
    }

    /**
     * Testa o fluxo completo de persistência (salvar e carregar)
     */
    @Test
    void testSalvarECarregarPreservaDados() {
        // 1. Salva o estado
        persistencia.salvar(gerenciadorOriginal);
        assertTrue(Files.exists(arquivoXmlTemp), "O arquivo XML deve ter sido criado fisicamente.");

        // 2. Carrega o estado em uma NOVA instância (simulando reabrir o programa)
        GerenciadorProducao gerenciadorRecuperado = persistencia.carregar(estrategiasDisponiveis);

        // 3. Valida se a recuperação preservou as invariantes
        assertNotNull(gerenciadorRecuperado);
        assertEquals(gerenciadorOriginal.getBudget(), gerenciadorRecuperado.getBudget(), 0.001);
        assertEquals(300.0, gerenciadorRecuperado.getMateriaPrima().getQuantidade(), 0.001, "O estoque de borracha deve ser preservado.");
        
        assertEquals(1, gerenciadorRecuperado.getDemandas().size(), "Deve recuperar a lista de demandas intacta.");
        assertEquals("Pato Grande", gerenciadorRecuperado.getDemandas().get(0).getTipoProduto());
        assertEquals(15, gerenciadorRecuperado.getDemandas().get(0).getQuantidadeProdutos());
    }

    /**
     * Testa o mecanismo de segurança contra arquivos ausentes.
     * Verifica se o sistema lança a exceção ArquivoInexistenteException 
     * ao tentar carregar um arquivo XML que não está presente no diretório.
     */
    @Test
    void testCarregarArquivoInexistenteLancaExcecao() {
        // Aponta para um caminho que não existe
        PersistenciaPlanta persInexistente = new PersistenciaPlanta(diretorioTemporario.resolve("nao_existe.xml"));
        
        assertThrows(ArquivoInexistenteException.class, () -> {
            persInexistente.carregar(estrategiasDisponiveis);
        }, "Deve lançar ArquivoInexistenteException ao tentar ler arquivo que não existe.");
    }

    /**
     * Testa a validação estrutural do XML durante o carregamento.
     * Garante que arquivos corrompidos ou mal formatados sejam bloqueados
     * lançando a exceção DadosInvalidosException em vez de contaminar a memória do sistema
     */
    @Test
    void testCarregarArquivoCorrompidoLancaExcecao() throws Exception {
        // Cria um arquivo com XML quebrado
        Files.writeString(arquivoXmlTemp, "<planta><materiasPrimas>ARQUIVO CORROMPIDO");

        assertThrows(DadosInvalidosException.class, () -> {
            persistencia.carregar(estrategiasDisponiveis);
        }, "Deve lançar DadosInvalidosException ao ler um XML mal formatado.");
    }
}