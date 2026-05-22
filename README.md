<div align="center">

# Essentialist

### Plugin de comandos essenciais para servidores Paper — modular, moderno e pronto para Folia.

![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)
![Paper](https://img.shields.io/badge/Paper-1.21.11-005C99)
![Gradle](https://img.shields.io/badge/Gradle-Kotlin%20DSL-02303A?logo=gradle&logoColor=white)
![Folia](https://img.shields.io/badge/Folia-supported-2EA043)
![License](https://img.shields.io/badge/license-MIT-3DA639)
![Version](https://img.shields.io/badge/version-0.1.0--SNAPSHOT-DBAB09)

</div>

---

## Sobre

**Essentialist** é um plugin de comandos essenciais para servidores Minecraft baseados em
[Paper](https://papermc.io/). Em vez de um monólito, cada funcionalidade é um **módulo
independente**: o que você não usa, não carrega. A arquitetura é centrada em injeção de
dependências, ciclo de vida explícito e resolução de dependências entre módulos por ordenação
topológica.

Todos os comandos são **recarregáveis em tempo real**, suportam **alvo em outro jogador** com
permissões dedicadas e usam mensagens formatadas com MiniMessage.

## Características

- **Arquitetura modular** — cada feature é um `Module` descoberto via `ServiceLoader`; falha de
  um módulo não derruba os demais (com rollback automático).
- **Pronto para Folia** — `folia-supported: true`, agendamento por região.
- **Comandos self/other** — aplique em si mesmo ou em outro jogador com `essentials.<comando>.others`.
- **Configuração viva** — YAML por módulo, recarregável via `/essentials reload` sem reiniciar.
- **Histórico de teleporte** — `/back` registra mortes e teleportes (ender pearl, portais,
  comandos) persistidos em SQLite.
- **Cooldowns e confirmação** — proteção contra spam; comandos destrutivos pedem confirmação.
- **Internacionalizável** — todas as mensagens vivem no config, com placeholders.

## Requisitos

| Componente | Versão |
|------------|--------|
| Servidor   | Paper 1.21.11+ (ou Folia) |
| Java       | 25+ |

## Instalação

1. Baixe o `Essentialist-<versão>.jar` mais recente.
2. Coloque o arquivo na pasta `plugins/` do seu servidor.
3. Reinicie o servidor — os arquivos de configuração são gerados em `plugins/Essentialist/`.

## Comandos

| Comando | Aliases | Descrição | Permissão |
|---------|---------|-----------|-----------|
| `/tp <jogador>` | — | Teleporta você até outro jogador | `essentials.tp` |
| `/tp move <de> <para>` | — | Teleporta um jogador até outro | `essentials.tp.others` |
| `/tp pos <x> <y> <z>` | — | Teleporta para coordenadas específicas | `essentials.tp` |
| `/tphere <jogador>` | — | Traz um jogador até você | `essentials.tphere` |
| `/back` | — | Volta ao local anterior pelo menu de histórico | `essentials.back` |
| `/fly [jogador]` | — | Ativa ou desativa o modo voo | `essentials.fly` |
| `/gamemode <modo> [jogador]` | `/gm` | Altera o modo de jogo | `essentials.gamemode` |
| `/reparar [jogador]` | `/repair` | Repara o item na mão | `essentials.repair` |
| `/reparar tudo [jogador]` | `all` | Repara o inventário inteiro | `essentials.repair` |
| `/limpar [jogador]` | `/clear` | Limpa o inventário (pede confirmação) | `essentials.clear` |
| `/alimentar [jogador]` | `/feed` | Restaura fome e saturação | `essentials.feed` |
| `/chapeu` | `/hat` | Equipa o item da mão como capacete | `essentials.hat` |
| `/compactar` | `/compact` | Compacta minérios e barras em blocos | `essentials.compact` |
| `/derreter` | `/smelt` | Derrete minérios no inventário | `essentials.smelt` |
| `/essentials reload` | — | Recarrega todas as configurações | `essentials.admin.reload` |

> Os comandos têm cooldown de 3 a 5 segundos. O `/limpar` exige confirmação antes de executar.

## Permissões

O modelo de permissões é declarativo e segue um padrão consistente:

| Permissão | Concede |
|-----------|---------|
| `essentials.<comando>` | Usar o comando em si mesmo |
| `essentials.<comando>.others` | Usar o comando em outro jogador (`fly`, `gamemode`, `clear`, `feed`, `repair`, `tp move`) |
| `essentials.gamemode.<modo>` | Acesso a um modo de jogo específico (`survival`, `creative`, ...) |
| `essentials.admin.reload` | Recarregar configurações |

## Configuração

Cada módulo gera o próprio arquivo YAML em `plugins/Essentialist/` (`clear.yml`, `repair.yml`,
`teleport.yml`, ...). Todas as mensagens são editáveis e aceitam tags do MiniMessage e
placeholders como `{player}` e `{count}`.

```yaml
# repair.yml
repaired-hand: "<green>Item reparado."
repaired-hand-other: "<green>Reparou o item de <gold>{player}</gold>."
blacklist: []          # materiais que nunca podem ser reparados
repair-all-limit: 41   # teto de itens por /reparar tudo
```

Após editar qualquer arquivo, aplique as mudanças com `/essentials reload` — não é preciso
reiniciar o servidor.

## Arquitetura

```
com.hanielcota.essentials
├── EssentialsPlugin       Entry point (JavaPlugin)
├── bootstrap/             Montagem do grafo de serviços
├── core/                  Ciclo de vida (BOOTING → ENABLED → DISABLING)
├── module/                Sistema de módulos e ordenação por dependências
├── modules/               Módulos de funcionalidade (clear, feed, repair, ...)
├── command/               Infraestrutura de comandos e interceptors
├── config/                Serviço de configuração YAML recarregável
├── database/              SQLite + HikariCP
├── event/                 Event bus interno
├── message/               Mensagens e internacionalização
├── service/               Service registry (injeção de dependências)
├── user/                  Usuários e sessões
├── paper/                 Adapters da API do Paper
├── scheduler/             Agendamento de tarefas
├── serialization/         Serializers
└── util/                  Utilitários
```

**Como funciona:**

- Cada módulo implementa `Module` (via `AbstractModule`) e é descoberto pelo `ServiceLoader`.
- O `ModuleManager` resolve a ordem de habilitação por **ordenação topológica** das dependências
  declaradas (ex.: `back` depende de `teleport`); se um módulo falha, faz rollback dos já ativos.
- O `EssentialsBootstrap` monta o grafo de serviços e o `ServiceRegistry` atua como container de DI.
- Comandos são declarados por anotações ([CommandFramework](https://github.com/HanielCota)) e
  menus pelo [MenuFramework](https://github.com/HanielCota).
- A persistência (histórico do `/back`) usa SQLite acessado via HikariCP.

## Compilando do código-fonte

Requer **JDK 25**. O projeto usa Gradle (Kotlin DSL) com o plugin Shadow.

```bash
# Clone o repositório
git clone <url-do-repositorio>
cd Essentialist

# Gere o artefato final (fat jar com dependências relocadas)
./gradlew build
```

O `.jar` final é escrito em `build/libs/`. A formatação é garantida pelo
[Spotless](https://github.com/diffplug/spotless) (`google-java-format`):

```bash
./gradlew spotlessApply   # formata o código
./gradlew spotlessCheck   # verifica a formatação
```

## Tecnologias

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 25 |
| Build | Gradle (Kotlin DSL) + Shadow |
| Servidor | Paper API |
| Comandos | CommandFramework |
| Menus | MenuFramework |
| Configuração | Configurate (YAML) |
| Banco de dados | SQLite + HikariCP |
| Formatação | Spotless / google-java-format |

## Contribuindo

Contribuições são bem-vindas. Antes de abrir um pull request:

1. Garanta que o código compila — `./gradlew build`.
2. Aplique a formatação — `./gradlew spotlessApply`.
3. Mantenha o padrão modular: novas features entram como módulos próprios em `modules/`.

## Licença

Distribuído sob a licença MIT. Veja o arquivo [`LICENSE`](LICENSE) para mais detalhes.

## Autor

Desenvolvido por **HanielCota** — [github.com/HanielCota](https://github.com/HanielCota).
