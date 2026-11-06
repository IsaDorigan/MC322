# Fábrica de Patinhos de Borracha (MC322)

Simulação de uma planta industrial que fabrica patinhos de borracha (pequenos, médios e grandes) em Java.
A linha de produção tem esteiras, máquinas de moldagem, embalagem e inspeção, armazém, controle de
matéria-prima e de orçamento (budget), estratégias de produção (padrão Strategy), cenários de operação e auditoria.

**Autores:** Isadora Kluge Dorigan e Guilherme Forte Silva

## Requisitos

- JDK 17 ou superior instalado (o Gradle não precisa estar instalado: o projeto usa o Gradle Wrapper).

## Como construir e executar

No Linux/macOS use `./gradlew`; no Windows use `gradlew.bat`.

| Comando | O que faz |
|---|---|
| `./gradlew build` | Compila o projeto e roda as verificações |
| `./gradlew run` | Executa a aplicação (menu interativo no terminal) |
| `./gradlew javadoc` | Gera a documentação Javadoc em `build/docs/javadoc/index.html` |

Se o Linux/macOS responder "Permission denied" ao rodar o wrapper, execute uma vez `chmod +x gradlew`.

## Estrutura

```
build.gradle / settings.gradle / gradle.properties   configuração do Gradle
gradlew, gradlew.bat, gradle/wrapper/                Gradle Wrapper
justificativa.txt                                    justificativa do tema e das atualizações
src/main/java/
  Main.java                  configura semente, carrega o estado salvo (ou cria uma planta nova) e inicia o menu
  MenuFabrica.java           menu e submenus; captura os erros de regra de negócio e os exibe ao usuário
  service/                   GerenciadorProducao: regras da planta (demandas, produção, compras, reparos, auditoria)
  persistence/               PersistenciaPlanta (salvar/carregar XML) e NoXml (leitura validada de elementos)
  model/                     Demanda, MateriaPrima, Esteira, Produto e patos, Auditavel
  model/maquina/             Maquina e suas subclasses (moldagem, embalagem, inspeção)
  model/enums/               status e cenários
  model/exceptions/          exceções de domínio
  strategy/                  estratégias de produção
  util/                      entrada validada do console, estilo do terminal e sorteio
```

## Tratamento de erros

As falhas de regra de negócio são representadas por exceções de domínio, todas derivadas de `PlantaException`:

| Exceção | Quando é lançada |
|---|---|
| `ArgumentoIlegalException` | Argumento inválido (quantidade negativa ou zero, tipo de produto inexistente, máquina inexistente) |
| `StatusIlegalException` | Operação incompatível com o estado atual (ex.: alterar demanda em produção) |
| `DemandaInexistenteException` | Não há demanda para o produto pedido ou nenhuma demanda elegível para a estratégia |
| `RecursoInsuficienteException` | Falta de matéria-prima ou carga acima da capacidade de máquinas/esteiras |
| `OrcamentoInsuficienteException` | Budget insuficiente para produzir, comprar ou reparar |
| `MaquinaIndisponivelException` | Linha mal configurada ou com máquina quebrada |
| `PersistenciaException` | Falha de leitura ou escrita do arquivo de persistência |
| `ArquivoInexistenteException` | (subclasse de `PersistenciaException`) O arquivo de persistência não existe |
| `DadosInvalidosException` | (subclasse de `PersistenciaException`) Arquivo vazio, XML malformado, elemento ausente, valor de tipo errado ou dado que viola as regras do domínio |

O `GerenciadorProducao` valida tudo **antes** de alterar o estado (estoque, budget e status das demandas), então uma
operação que falha não deixa a planta parcialmente modificada. O `MenuFabrica` captura `PlantaException`, mostra uma
mensagem de erro clara e devolve o usuário ao menu. Entradas inválidas digitadas no menu são tratadas por
`util.EntradaConsole`, que repete a pergunta até receber um valor válido.

## Persistência (salvar e carregar em XML)

O estado da planta é gravado em `dados/planta.xml` (relativo à pasta de execução; a pasta é ignorada pelo git).
No menu principal, **7 - Salvar estado da planta** grava o estado atual (pode ser repetido para atualizar o arquivo) e
**8 - Carregar estado da planta** substitui o estado atual pelo salvo. Ao escolher **0 - Sair**, o programa pergunta se
você quer salvar antes.

**Carregamento inicial:** ao iniciar, a aplicação tenta recuperar `dados/planta.xml`.
- Arquivo inexistente (primeira execução) é uma situação normal: inicia uma planta nova, com a escolha de cenário.
- Arquivo existente mas inválido: o motivo é mostrado e também se inicia uma planta nova; o arquivo defeituoso só é
  sobrescrito se o usuário salvar depois.

**Garantias:**
- *Carregar valida antes de substituir.* O arquivo é lido e validado (estrutura XML, tipos e regras do domínio) e uma planta
  totalmente nova é construída; só então ela substitui a atual. Se houver qualquer erro, a planta atual permanece intacta.
- *Salvar não corrompe o salvamento anterior.* A gravação é feita em arquivo temporário e só então substitui `planta.xml`.
- *Domínio separado de arquivo.* As classes de `model` não conhecem XML: elas oferecem instantâneos imutáveis do estado
  (`obterEstado()`) e métodos `restaurarEstado(...)` que aplicam as mesmas invariantes do uso normal. As Partes de uma
  máquina continuam encapsuladas. Toda a leitura e escrita de XML fica em `persistence/`.
- Leitura de XML com DOCTYPE desativado (proteção contra XXE).

**Estrutura do arquivo** (resumo; ver um arquivo salvo para o exemplo completo):

```
planta versao="1"
  cenario, orcamento, proximoLote, proximoIdProduto, estrategia
  materiasPrimas/materiaPrima  (id, nome, quantidade, unidade, custoPorUnidade, quantidadeMinima)
  esteiras/esteira             (numero, capacidadeMaxima)
  linhaDeProducao/nome
  linhaDeProducao/maquinas/maquina tipo="Moldagem|Embalagem|Inspecao"
      nome, status, saude, limiarManutencao, usos, falhas
      motor, sensorTemperatura, atuadores/atuador, moldes/molde (só na moldagem)
  demandas/demanda             (tipoProduto, quantidade, status)
  armazem/produto              (id, tipo, lote, status, probabilidadeFalhaAcumulada)
```

## Escopo e Limitações

### Dados Persistidos vs. Não Persistidos
**Persistidos (Salvos no XML):** 
Salvamos o estoque de matéria-prima, a configuração da infraestrutura (esteiras), as máquinas com o estado de suas peças internas (motor, sensor, atuadores e moldes), as demandas cadastradas e os produtos no armazém. Isso é o essencial para restaurar o estado útil da fábrica.

**Não Persistidos:**
- **Itens na esteira:** Representam um transporte em andamento (estado instável). Salvr eles viola a uma premissa da produção.
- **Estimativas de consumo/custo e configurações fixas das máquinas:** São dados calculados dinamicamente ou inerentes ao construtor da máquina e ao cenário. Não salvamos para evitar redundância e inconsistência.
- **Semente (Seed) do Sorteio:** É uma configuração da execução atual, e não uma propriedade física da planta.

### Explicação da Suíte de Testes
Os testes automatizados utilizam JUnit 5 de forma isolada, garantindo a integridade do sistema em 4 frentes:

- **`MateriaPrimaTest`:** Valida regras de negócio, como o bloqueio de consumo abaixo da reserva mínima 
- **`DemandaTest`:** Verifica o ciclo de vida dos pedidos, garantindo transições corretas de status .
- **`GerenciadorProducaoTest`:** Testa o fluxo de produção, como o registro de demandas na fila e o desconto correto do orçamento (budget) ao comprar insumos.
- **`PersistenciaPlantaTest`:** Focado na leitura e escrita em arquivos isolados , garante que os valores exatos são preservados no recarregamento da fábrica e verifica se o sistema lança `DadosInvalidosException` e `ArquivoInexistenteException` ao enfrentar XMLs corrompidos ou caminhos falsos.