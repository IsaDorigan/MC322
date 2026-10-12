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
  Main.java                  configura semente, cenário, linha de produção e inicia o menu
  MenuFabrica.java           menu e submenus; captura os erros de regra de negócio e os exibe ao usuário
  GerenciadorProducao.java   regras da planta (demandas, produção, compras, reparos, auditoria)
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

O `GerenciadorProducao` valida tudo **antes** de alterar o estado (estoque, budget e status das demandas), então uma
operação que falha não deixa a planta parcialmente modificada. O `MenuFabrica` captura `PlantaException`, mostra uma
mensagem de erro clara e devolve o usuário ao menu. Entradas inválidas digitadas no menu são tratadas por
`util.EntradaConsole`, que repete a pergunta até receber um valor válido.
