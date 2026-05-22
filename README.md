<div align="center">

# ⚡ Essentialist

**A modular essentials plugin for Paper and Folia servers.**

Teleportation, item repair, flight, gamemode control and inventory utilities —
every feature shipped as an independent, hot-reloadable module.

[![CI](https://github.com/HanielCota/Essentialist/actions/workflows/ci.yml/badge.svg)](https://github.com/HanielCota/Essentialist/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)
![Paper](https://img.shields.io/badge/Paper-1.21.11-005C99)
![Folia](https://img.shields.io/badge/Folia-supported-2EA043)
![License](https://img.shields.io/badge/License-MIT-3DA639)

</div>

---

## 📖 Overview

**Essentialist** is a command plugin for Minecraft servers running [Paper](https://papermc.io/).
Rather than a monolith, every feature is an **independent module** — what you don't use is never
loaded. The codebase is built around dependency injection, an explicit lifecycle, and topological
resolution of inter-module dependencies.

Every command is **hot-reloadable**, supports **targeting another player** through dedicated
permissions, and renders messages with MiniMessage formatting.

## ✨ Features

- 🧩 **Modular architecture** — each feature is a `Module` discovered via `ServiceLoader`; a failing
  module never takes the others down, thanks to automatic rollback.
- 🧵 **Folia-ready** — `folia-supported: true`, with region-based scheduling.
- 🎯 **Self / other commands** — apply an action to yourself or to another player with the
  `essentials.<command>.others` permission.
- 🔄 **Live configuration** — per-module YAML, reloadable via `/essentials reload` with no restart.
- 🧭 **Teleport history** — `/back` records deaths and teleports (ender pearls, portals, commands),
  persisted in SQLite.
- 🛡️ **Cooldowns and confirmation** — built-in spam protection; destructive commands ask for
  confirmation before running.
- 💬 **Fully configurable messages** — every string lives in the config, with placeholders.

## 📋 Requirements

| Component | Version |
|-----------|---------|
| Server    | Paper 1.21.11 or newer (Folia supported) |
| Java      | 25 or newer |

## 📦 Installation

1. Download the latest `Essentialist-<version>.jar`.
2. Drop it into your server's `plugins/` folder.
3. Restart the server — configuration files are generated under `plugins/Essentialist/`.

## ⌨️ Commands

| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/tp <player>` | — | Teleport yourself to another player | `essentials.tp` |
| `/tp move <from> <to>` | — | Teleport one player to another | `essentials.tp.others` |
| `/tp pos <x> <y> <z>` | — | Teleport to specific coordinates | `essentials.tp` |
| `/tphere <player>` | — | Bring a player to you | `essentials.tphere` |
| `/back` | — | Return to a previous location via the history menu | `essentials.back` |
| `/fly [player]` | — | Toggle flight mode | `essentials.fly` |
| `/gamemode <mode> [player]` | `/gm` | Change game mode | `essentials.gamemode` |
| `/reparar [player]` | `/repair` | Repair the item in hand | `essentials.repair` |
| `/reparar tudo [player]` | `all` | Repair the entire inventory | `essentials.repair` |
| `/limpar [player]` | `/clear` | Clear the inventory (asks for confirmation) | `essentials.clear` |
| `/alimentar [player]` | `/feed` | Restore hunger and saturation | `essentials.feed` |
| `/chapeu` | `/hat` | Wear the held item as a helmet | `essentials.hat` |
| `/compactar` | `/compact` | Compact ores and ingots into blocks | `essentials.compact` |
| `/derreter` | `/smelt` | Smelt ores in the inventory | `essentials.smelt` |
| `/essentials reload` | — | Reload every configuration file | `essentials.admin.reload` |

> [!NOTE]
> Commands have a 3–5 second cooldown. `/limpar` requires confirmation before it runs.

## 🔐 Permissions

The permission model is declarative and follows a consistent pattern:

| Permission | Grants |
|------------|--------|
| `essentials.<command>` | Use the command on yourself |
| `essentials.<command>.others` | Use the command on another player (`fly`, `gamemode`, `clear`, `feed`, `repair`, `tp move`) |
| `essentials.gamemode.<mode>` | Access to a specific game mode (`survival`, `creative`, ...) |
| `essentials.admin.reload` | Reload configurations |

## ⚙️ Configuration

Each module generates its own YAML file under `plugins/Essentialist/` (`clear.yml`, `repair.yml`,
`teleport.yml`, ...). Every message is editable and accepts MiniMessage tags and placeholders such
as `{player}` and `{count}`.

```yaml
# repair.yml
repaired-hand: "<green>Item repaired."
repaired-hand-other: "<green>Repaired <gold>{player}</gold>'s item."
blacklist: []          # materials that can never be repaired
repair-all-limit: 41   # cap of items processed per /reparar tudo
```

> [!TIP]
> After editing any file, apply the changes with `/essentials reload` — no server restart needed.

## 🏗️ Architecture

```
com.hanielcota.essentials
├── EssentialsPlugin       Entry point (JavaPlugin)
├── bootstrap/             Service graph assembly
├── core/                  Lifecycle (BOOTING → ENABLED → DISABLING)
├── module/                Module system and dependency ordering
├── modules/               Feature modules (clear, feed, repair, ...)
├── command/               Command infrastructure and interceptors
├── config/                Reloadable YAML configuration service
├── database/              SQLite + HikariCP
├── event/                 Internal event bus
├── message/               Messages and internationalization
├── service/               Service registry (dependency injection)
├── user/                  Users and sessions
├── paper/                 Paper API adapters
├── scheduler/             Task scheduling
├── serialization/         Serializers
└── util/                  Utilities
```

**How it works:**

- Each module implements `Module` (through `AbstractModule`) and is discovered by `ServiceLoader`.
- `ModuleManager` resolves the enable order via **topological sort** of the declared dependencies
  (e.g. `back` depends on `teleport`); if a module fails, the already-enabled ones are rolled back.
- `EssentialsBootstrap` assembles the service graph; `ServiceRegistry` acts as the DI container.
- Commands are declared with annotations
  ([CommandFramework](https://github.com/HanielCota)) and menus with
  [MenuFramework](https://github.com/HanielCota).
- Persistence (the `/back` history) uses SQLite accessed through HikariCP.

## 🔨 Building from Source

Requires **JDK 25**. The project uses Gradle (Kotlin DSL) with the Shadow plugin.

```bash
# Clone the repository
git clone https://github.com/HanielCota/Essentialist.git
cd Essentialist

# Produce the final artifact (fat jar with relocated dependencies)
./gradlew build
```

The final `.jar` is written to `build/libs/`. Formatting is enforced by
[Spotless](https://github.com/diffplug/spotless) (`google-java-format`):

```bash
./gradlew spotlessApply   # format the code
./gradlew spotlessCheck   # verify formatting
```

## 🧰 Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 25 |
| Build | Gradle (Kotlin DSL) + Shadow |
| Server | Paper API |
| Commands | CommandFramework |
| Menus | MenuFramework |
| Configuration | Configurate (YAML) |
| Database | SQLite + HikariCP |
| Formatting | Spotless / google-java-format |
| CI | GitHub Actions |

## 🤝 Contributing

Contributions are welcome. Before opening a pull request:

1. Make sure the project builds — `./gradlew build`.
2. Apply formatting — `./gradlew spotlessApply`.
3. Keep the modular pattern: new features go in as their own module under `modules/`.

Every push and pull request is automatically validated by the CI pipeline (build, tests, lint).

## 📄 License

Distributed under the MIT License. See [`LICENSE`](LICENSE) for details.

## 👤 Author

Built by **HanielCota** — [github.com/HanielCota](https://github.com/HanielCota).
