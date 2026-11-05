package persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import model.Demanda;
import model.Esteira;
import model.LinhaDeProducao;
import model.MateriaPrima;
import model.Produto;
import model.enums.Cenario;
import model.enums.StatusDemanda;
import model.enums.StatusMaquina;
import model.enums.StatusProduto;
import model.exceptions.ArgumentoIlegalException;
import model.exceptions.ArquivoInexistenteException;
import model.exceptions.DadosInvalidosException;
import model.exceptions.PersistenciaException;
import model.exceptions.PlantaException;
import model.maquina.Maquina;
import model.maquina.MaquinaEmbalagem;
import model.maquina.MaquinaInspecao;
import model.maquina.MaquinaMoldagem;
import model.maquina.componentes.AtuadorMecanico;
import model.maquina.componentes.Molde;
import model.maquina.componentes.Motor;
import model.maquina.componentes.SensorTemperatura;
import service.GerenciadorProducao;
import strategy.EstrategiaProducao;

/**
 * Responsável por salvar o estado da planta em um arquivo XML e por reconstruí-lo a partir dele.
 * Concentra toda a lógica de leitura e escrita de arquivos: as classes de domínio não conhecem XML,
 * apenas oferecem instantâneos imutáveis do próprio estado e métodos que restauram esse estado
 * validando as regras do domínio.
 *
 * <p>Garantias de segurança:</p>
 * <ul>
 *   <li><b>Salvar</b> grava primeiro em um arquivo temporário e só então substitui o arquivo definitivo,
 *       de modo que uma falha no meio da escrita nunca destrói o salvamento anterior.</li>
 *   <li><b>Carregar</b> constrói uma planta totalmente nova e só a devolve se todos os dados forem válidos;
 *       quem chama continua com a planta anterior se qualquer exceção for lançada.</li>
 * </ul>
 *
 * <p>O que é salvo: cenário, orçamento, contadores de lote e de produto, estratégia ativa, matéria-prima,
 * esteiras, linha de produção (máquinas, motor, sensor, atuadores e moldes), demandas e produtos do armazém.
 * Não é salvo: a semente do sorteio, o item que está sobre uma esteira (sempre vazia entre operações) e as
 * estimativas de consumo e custo das demandas, que são recalculadas ao carregar.</p>
 *
 * @author Isadora Kluge Dorigan e Guilherme Forte Silva
 */
public class PersistenciaPlanta {

    /** Versão do formato do arquivo, gravada na raiz do XML e verificada ao carregar. */
    public static final int VERSAO_FORMATO = 1;

    private final Path arquivo;

    /**
     * Cria a persistência associada a um arquivo.
     *
     * @param arquivo caminho do arquivo XML onde o estado será salvo e de onde será carregado
     * @throws ArgumentoIlegalException se o caminho for nulo
     */
    public PersistenciaPlanta(Path arquivo) {
        if (arquivo == null) {
            throw new ArgumentoIlegalException("O caminho do arquivo de persistencia nao foi informado.");
        }
        this.arquivo = arquivo;
    }

    /**
     * Retorna o arquivo usado por esta persistência.
     *
     * @return o caminho do arquivo
     */
    public Path getArquivo() {
        return arquivo;
    }

    /**
     * Informa se o arquivo de persistência existe.
     *
     * @return {@code true} se o caminho existir
     */
    public boolean arquivoExiste() {
        return Files.exists(arquivo);
    }

    // =====================================================
    // SALVAR
    // =====================================================

    /**
     * Salva o estado atual da planta no arquivo, substituindo o conteúdo anterior.
     * Pode ser chamado repetidamente para atualizar o salvamento após alterações.
     *
     * @param gerenciador a planta a ser salva
     * @throws ArgumentoIlegalException se o gerenciador for nulo
     * @throws PersistenciaException    se não for possível gravar o arquivo (o arquivo anterior é preservado)
     */
    public void salvar(GerenciadorProducao gerenciador) {
        if (gerenciador == null) {
            throw new ArgumentoIlegalException("Nao ha planta para salvar.");
        }
        Document documento = construirDocumento(gerenciador);
        gravar(documento);
    }

    private Document construirDocumento(GerenciadorProducao g) {
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new PersistenciaException("Nao foi possivel preparar o XML: " + e.getMessage(), e);
        }

        Element planta = doc.createElement("planta");
        planta.setAttribute("versao", String.valueOf(VERSAO_FORMATO));
        doc.appendChild(planta);

        adicionar(doc, planta, "cenario", g.getCenario().name());
        adicionar(doc, planta, "orcamento", g.getBudget());
        adicionar(doc, planta, "proximoLote", g.getProximoLote());
        adicionar(doc, planta, "proximoIdProduto", g.getProximoIdProduto());
        adicionar(doc, planta, "estrategia", g.getEstrategiaAtual().getClass().getSimpleName());

        // Matéria-prima
        MateriaPrima mp = g.getMateriaPrima();
        Element materias = adicionar(doc, planta, "materiasPrimas", null);
        Element materia = adicionar(doc, materias, "materiaPrima", null);
        materia.setAttribute("id", String.valueOf(mp.getId()));
        adicionar(doc, materia, "nome", mp.getNome());
        adicionar(doc, materia, "quantidade", mp.getQuantidade());
        adicionar(doc, materia, "unidade", mp.getUnidade());
        adicionar(doc, materia, "custoPorUnidade", mp.getCustoPorUnidade());
        adicionar(doc, materia, "quantidadeMinima", mp.getQuantidadeMinima());

        // Esteiras
        Element esteiras = adicionar(doc, planta, "esteiras", null);
        for (Esteira esteira : g.getEsteiras()) {
            Element e = adicionar(doc, esteiras, "esteira", null);
            e.setAttribute("numero", String.valueOf(esteira.getNumero()));
            adicionar(doc, e, "capacidadeMaxima", esteira.getCapacidadeMaxima());
        }

        // Linha de produção (composição: máquina -> motor, sensor, atuadores, moldes)
        LinhaDeProducao linha = g.getLinhaDeProducao();
        Element elLinha = adicionar(doc, planta, "linhaDeProducao", null);
        adicionar(doc, elLinha, "nome", linha.getNome());
        Element maquinas = adicionar(doc, elLinha, "maquinas", null);
        for (Maquina maquina : linha.getMaquinas()) {
            escreverMaquina(doc, maquinas, maquina);
        }

        // Demandas
        Element demandas = adicionar(doc, planta, "demandas", null);
        for (Demanda demanda : g.getDemandas()) {
            Element d = adicionar(doc, demandas, "demanda", null);
            adicionar(doc, d, "tipoProduto", demanda.getTipoProduto());
            adicionar(doc, d, "quantidade", demanda.getQuantidadeProdutos());
            adicionar(doc, d, "status", demanda.getStatus().name());
        }

        // Armazém
        Element armazem = adicionar(doc, planta, "armazem", null);
        for (Produto produto : g.getProdutosFabricados()) {
            Element p = adicionar(doc, armazem, "produto", null);
            p.setAttribute("id", String.valueOf(produto.getId()));
            adicionar(doc, p, "tipo", produto.getTipo());
            adicionar(doc, p, "lote", produto.getLote());
            adicionar(doc, p, "status", produto.getStatus().name());
            adicionar(doc, p, "probabilidadeFalhaAcumulada", produto.getProbabilidadeFalhaAcumulada());
        }
        return doc;
    }

    private void escreverMaquina(Document doc, Element pai, Maquina maquina) {
        Maquina.Estado estado = maquina.obterEstado();
        Element m = adicionar(doc, pai, "maquina", null);
        m.setAttribute("tipo", maquina.getTipo());
        adicionar(doc, m, "nome", estado.nome());
        adicionar(doc, m, "status", estado.status().name());
        adicionar(doc, m, "saude", estado.saude());
        adicionar(doc, m, "limiarManutencao", estado.limiarManutencao());
        adicionar(doc, m, "usos", estado.usos());
        adicionar(doc, m, "falhas", estado.falhas());

        Element motor = adicionar(doc, m, "motor", null);
        adicionar(doc, motor, "modelo", estado.motor().modelo());
        adicionar(doc, motor, "ligado", estado.motor().ligado());
        adicionar(doc, motor, "desgaste", estado.motor().desgaste());
        adicionar(doc, motor, "temperaturaAtual", estado.motor().temperaturaAtual());

        Element sensor = adicionar(doc, m, "sensorTemperatura", null);
        adicionar(doc, sensor, "codigoIdentificacao", estado.sensor().codigoIdentificacao());
        adicionar(doc, sensor, "calibrado", estado.sensor().calibrado());
        adicionar(doc, sensor, "leiturasRealizadas", estado.sensor().leiturasRealizadas());

        Element atuadores = adicionar(doc, m, "atuadores", null);
        for (AtuadorMecanico.Estado a : estado.atuadores()) {
            Element at = adicionar(doc, atuadores, "atuador", null);
            adicionar(doc, at, "funcao", a.funcao());
            adicionar(doc, at, "ciclosUso", a.ciclosUso());
            adicionar(doc, at, "forcaAplicada", a.forcaAplicada());
        }

        if (!estado.moldes().isEmpty()) {
            Element moldes = adicionar(doc, m, "moldes", null);
            for (Molde.Estado mo : estado.moldes()) {
                Element molde = adicionar(doc, moldes, "molde", null);
                adicionar(doc, molde, "tipoFormato", mo.tipoFormato());
                adicionar(doc, molde, "nivelSujeira", mo.nivelSujeira());
                adicionar(doc, molde, "desgasteFisico", mo.desgasteFisico());
                adicionar(doc, molde, "pecasMoldadasDesdeLimpeza", mo.pecasMoldadasDesdeLimpeza());
            }
        }
    }

    private static Element adicionar(Document doc, Element pai, String nome, Object valor) {
        Element filho = doc.createElement(nome);
        if (valor != null) {
            filho.setTextContent(String.valueOf(valor));
        }
        pai.appendChild(filho);
        return filho;
    }

    /** Grava em arquivo temporário na mesma pasta e então substitui o definitivo. */
    private void gravar(Document documento) {
        Path temporario = null;
        try {
            Path pasta = arquivo.toAbsolutePath().getParent();
            Files.createDirectories(pasta);
            temporario = Files.createTempFile(pasta, "planta-", ".tmp");

            Transformer transformador = TransformerFactory.newInstance().newTransformer();
            transformador.setOutputProperty(OutputKeys.INDENT, "yes");
            transformador.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformador.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformador.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            try (OutputStream saida = Files.newOutputStream(temporario)) {
                // A declaração é escrita à mão porque o Transformer a cola na mesma linha da raiz
                saida.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes(StandardCharsets.UTF_8));
                transformador.transform(new DOMSource(documento), new StreamResult(saida));
            }

            try {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | TransformerException e) {
            throw new PersistenciaException("Nao foi possivel gravar o arquivo " + arquivo
                    + ": " + e.getMessage() + ". O salvamento anterior (se existia) foi preservado.", e);
        } finally {
            if (temporario != null) {
                try {
                    Files.deleteIfExists(temporario);
                } catch (IOException ignorada) {
                    // o arquivo temporário restante não afeta os dados salvos
                }
            }
        }
    }

    // =====================================================
    // CARREGAR
    // =====================================================

    /**
     * Lê o arquivo, valida todos os dados e reconstrói a planta.
     * A planta devolvida é um objeto novo e independente: quem chamou deve apenas substituir a referência
     * à planta anterior. Se qualquer problema for encontrado, uma exceção é lançada e nada é alterado.
     *
     * @param estrategias estratégias de produção disponíveis; a estratégia salva é escolhida entre elas
     * @return a planta reconstruída
     * @throws ArquivoInexistenteException se o arquivo não existir
     * @throws DadosInvalidosException     se o arquivo estiver vazio, não for um XML válido, tiver elementos
     *                                     ausentes ou valores incompatíveis com as regras do domínio
     * @throws PersistenciaException       se ocorrer uma falha de leitura do arquivo
     */
    public GerenciadorProducao carregar(List<EstrategiaProducao> estrategias) {
        Element raiz = lerRaiz();
        try {
            return reconstruir(new NoXml(raiz, raiz.getNodeName()), estrategias);
        } catch (PersistenciaException e) {
            throw e;
        } catch (PlantaException e) {
            throw new DadosInvalidosException("Dados incompativeis com as regras da fabrica: " + e.getMessage(), e);
        }
    }

    private Element lerRaiz() {
        if (!Files.exists(arquivo)) {
            throw new ArquivoInexistenteException("O arquivo de persistencia nao existe: " + arquivo + ".");
        }
        if (!Files.isRegularFile(arquivo)) {
            throw new PersistenciaException("O caminho de persistencia nao e um arquivo: " + arquivo + ".");
        }
        try {
            if (Files.size(arquivo) == 0) {
                throw new DadosInvalidosException("O arquivo de persistencia esta vazio: " + arquivo + ".");
            }
            DocumentBuilder construtor = criarConstrutorSeguro();
            try (InputStream entrada = Files.newInputStream(arquivo)) {
                return construtor.parse(entrada).getDocumentElement();
            }
        } catch (SAXParseException e) {
            throw new DadosInvalidosException("Arquivo com formato XML invalido (linha " + e.getLineNumber()
                    + ", coluna " + e.getColumnNumber() + "): " + e.getMessage(), e);
        } catch (SAXException e) {
            throw new DadosInvalidosException("Arquivo com formato XML invalido: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PersistenciaException("Nao foi possivel ler o arquivo " + arquivo + ": " + e.getMessage(), e);
        }
    }

    /** Construtor de DOM sem DOCTYPE (protege contra XXE) e sem mensagens soltas no console. */
    private DocumentBuilder criarConstrutorSeguro() {
        try {
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            fabrica.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            fabrica.setXIncludeAware(false);
            fabrica.setExpandEntityReferences(false);
            DocumentBuilder construtor = fabrica.newDocumentBuilder();
            construtor.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) { }

                @Override
                public void error(SAXParseException e) throws SAXException {
                    throw e;
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    throw e;
                }
            });
            return construtor;
        } catch (ParserConfigurationException e) {
            throw new PersistenciaException("Nao foi possivel preparar a leitura do XML: " + e.getMessage(), e);
        }
    }

    private GerenciadorProducao reconstruir(NoXml planta, List<EstrategiaProducao> estrategias) {
        if (!planta.caminho().equals("planta")) {
            throw new DadosInvalidosException("O elemento raiz deve ser <planta>, mas o arquivo contem <"
                    + planta.caminho() + ">.");
        }
        int versao = planta.atributoInteiro("versao");
        if (versao != VERSAO_FORMATO) {
            throw new DadosInvalidosException("Versao do formato nao suportada: " + versao
                    + " (esperada: " + VERSAO_FORMATO + ").");
        }

        Cenario cenario = planta.enumeracao(Cenario.class, "cenario");
        double orcamento = planta.decimal("orcamento");
        int proximoLote = planta.inteiro("proximoLote");
        int proximoIdProduto = planta.inteiro("proximoIdProduto");
        EstrategiaProducao estrategia = escolherEstrategia(planta.texto("estrategia"), estrategias);

        MateriaPrima materiaPrima = lerMateriaPrima(planta.filho("materiasPrimas"));
        List<Esteira> esteiras = lerEsteiras(planta.filho("esteiras"));
        LinhaDeProducao linha = lerLinha(planta.filho("linhaDeProducao"), cenario);
        List<Demanda> demandas = lerDemandas(planta.filho("demandas"));
        List<Produto> produtos = lerArmazem(planta.filho("armazem"));

        GerenciadorProducao gerenciador = new GerenciadorProducao(materiaPrima, cenario,
                esteiras.get(0), esteiras.get(1), esteiras.get(2), estrategia, linha);
        gerenciador.restaurarEstado(orcamento, proximoLote, proximoIdProduto, demandas, produtos);
        return gerenciador;
    }

    private EstrategiaProducao escolherEstrategia(String nome, List<EstrategiaProducao> disponiveis) {
        List<String> nomes = new ArrayList<>();
        for (EstrategiaProducao estrategia : disponiveis) {
            if (estrategia.getClass().getSimpleName().equals(nome)) {
                return estrategia;
            }
            nomes.add(estrategia.getClass().getSimpleName());
        }
        throw new DadosInvalidosException("Estrategia desconhecida: '" + nome + "' (disponiveis: "
                + String.join(", ", nomes) + ").");
    }

    private MateriaPrima lerMateriaPrima(NoXml no) {
        List<NoXml> materias = no.filhos("materiaPrima");
        if (materias.size() != 1) {
            throw new DadosInvalidosException("A fabrica opera com exatamente uma materia-prima, mas "
                    + no.caminho() + " contem " + materias.size() + ".");
        }
        NoXml m = materias.get(0);
        return new MateriaPrima(m.atributoInteiro("id"), m.texto("nome"), m.decimal("quantidade"),
                m.texto("unidade"), m.decimal("custoPorUnidade"), m.decimal("quantidadeMinima"));
    }

    private List<Esteira> lerEsteiras(NoXml no) {
        List<NoXml> nos = no.filhos("esteira");
        if (nos.size() != 3) {
            throw new DadosInvalidosException("A fabrica possui 3 esteiras, mas " + no.caminho()
                    + " contem " + nos.size() + ".");
        }
        List<Esteira> esteiras = new ArrayList<>();
        for (int i = 0; i < nos.size(); i++) {
            int numero = nos.get(i).atributoInteiro("numero");
            if (numero != i + 1) {
                throw new DadosInvalidosException("As esteiras devem estar numeradas de 1 a 3 em ordem, mas "
                        + nos.get(i).caminho() + " tem numero " + numero + ".");
            }
            esteiras.add(new Esteira(nos.get(i).decimal("capacidadeMaxima"), numero));
        }
        return esteiras;
    }

    private LinhaDeProducao lerLinha(NoXml no, Cenario cenario) {
        LinhaDeProducao linha = new LinhaDeProducao(no.texto("nome"));
        for (NoXml m : no.filho("maquinas").filhos("maquina")) {
            Maquina maquina = criarMaquina(m.atributo("tipo"), cenario, m.caminho());
            maquina.restaurarEstado(lerEstadoMaquina(m));
            linha.adicionarMaquina(maquina);
        }
        return linha;
    }

    private Maquina criarMaquina(String tipo, Cenario cenario, String caminho) {
        switch (tipo) {
            case "Moldagem":
                return new MaquinaMoldagem(cenario);
            case "Embalagem":
                return new MaquinaEmbalagem(cenario);
            case "Inspecao":
                return new MaquinaInspecao(cenario);
            default:
                throw new DadosInvalidosException("Tipo de maquina desconhecido '" + tipo + "' em " + caminho
                        + "@tipo (aceitos: Moldagem, Embalagem, Inspecao).");
        }
    }

    private Maquina.Estado lerEstadoMaquina(NoXml m) {
        NoXml motor = m.filho("motor");
        Motor.Estado estadoMotor = new Motor.Estado(motor.texto("modelo"), motor.booleano("ligado"),
                motor.decimal("desgaste"), motor.decimal("temperaturaAtual"));

        NoXml sensor = m.filho("sensorTemperatura");
        SensorTemperatura.Estado estadoSensor = new SensorTemperatura.Estado(
                sensor.texto("codigoIdentificacao"), sensor.booleano("calibrado"), sensor.inteiro("leiturasRealizadas"));

        List<AtuadorMecanico.Estado> atuadores = new ArrayList<>();
        for (NoXml a : m.filho("atuadores").filhos("atuador")) {
            atuadores.add(new AtuadorMecanico.Estado(a.texto("funcao"), a.inteiro("ciclosUso"), a.decimal("forcaAplicada")));
        }

        List<Molde.Estado> moldes = new ArrayList<>();
        NoXml noMoldes = m.filhoOpcional("moldes");
        if (noMoldes != null) {
            for (NoXml mo : noMoldes.filhos("molde")) {
                moldes.add(new Molde.Estado(mo.texto("tipoFormato"), mo.decimal("nivelSujeira"),
                        mo.decimal("desgasteFisico"), mo.inteiro("pecasMoldadasDesdeLimpeza")));
            }
        }

        return new Maquina.Estado(m.texto("nome"), m.enumeracao(StatusMaquina.class, "status"),
                m.decimal("saude"), m.decimal("limiarManutencao"), m.inteiro("usos"), m.inteiro("falhas"),
                estadoMotor, estadoSensor, atuadores, moldes);
    }

    private List<Demanda> lerDemandas(NoXml no) {
        List<Demanda> demandas = new ArrayList<>();
        for (NoXml d : no.filhos("demanda")) {
            String tipo = d.texto("tipoProduto");
            if (GerenciadorProducao.criarProduto(tipo, 0) == null) {
                throw new DadosInvalidosException("Tipo de produto desconhecido '" + tipo + "' em "
                        + d.caminho() + "/tipoProduto.");
            }
            Demanda demanda = new Demanda(tipo, 0);
            demanda.restaurarEstado(d.inteiro("quantidade"), d.enumeracao(StatusDemanda.class, "status"));
            demandas.add(demanda);
        }
        return demandas;
    }

    private List<Produto> lerArmazem(NoXml no) {
        List<Produto> produtos = new ArrayList<>();
        for (NoXml p : no.filhos("produto")) {
            String tipo = p.texto("tipo");
            Produto produto = GerenciadorProducao.criarProduto(tipo, p.atributoInteiro("id"));
            if (produto == null) {
                throw new DadosInvalidosException("Tipo de produto desconhecido '" + tipo + "' em "
                        + p.caminho() + "/tipo.");
            }
            produto.restaurarEstado(p.enumeracao(StatusProduto.class, "status"),
                    p.decimal("probabilidadeFalhaAcumulada"), p.inteiro("lote"));
            produtos.add(produto);
        }
        return produtos;
    }
}
