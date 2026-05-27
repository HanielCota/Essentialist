# Essentialist — project conventions for Claude

## Code style: Scope Bursting

Vertical flow over horizontal. Every line one cognitive intention. Code must be
visually scannable top-to-bottom; never compressed into a single dense line.

### 1. Scope bursting — always explode chained calls

Never resolve multiple responsibilities inline. Extract each step into a local
`var` with a semantic name. Hard limit: **two levels of chaining max** in any
single expression; beyond that, explode.

Bad:

```java
actor.sendSuccess(this.config.value().messages().spawnSet());
```

Good:

```java
var snap = this.config.value();
var messages = snap.messages();
var spawnSetMsg = messages.spawnSet();

actor.sendSuccess(spawnSetMsg);
```

Permitted as a single chain: short, semantically obvious calls like
`player.getUniqueId()` or `text.toLowerCase()`.

### 2. No inline logic in parameters

No `.replace()`, concatenation, ternaries, streams, builders, lambdas, or
formatting expressions directly inside a method call argument list. Extract
first, then pass the named variable.

Bad:

```java
sender.sendMessage(messages.prefix().replace("{player}", target.getName()));
```

Good:

```java
var prefix = messages.prefix();
var targetName = target.getName();
var formatted = prefix.replace("{player}", targetName);

sender.sendMessage(formatted);
```

### 3. Guard clauses (fail-fast)

Invalid state exits the method immediately. Never nest the happy path inside
conditionals; never use `else` after `return`. Validate in this order: null →
empty optionals → permissions → invalid state → preconditions → main flow.

Bad:

```java
if (player != null) {
  if (player.isOnline()) {
    process(player);
  }
}
```

Good:

```java
if (player == null || !player.isOnline()) {
  return;
}

process(player);
```

### 4. Zone segmentation

Every non-trivial method has these zones separated by a blank line:

1. **Extraction** — collect data (configs, services, current state).
2. **Validation** — guard clauses only.
3. **Processing** — main logic, object construction.
4. **Side effect** — messages, dispatch, logging, return.

### 5. Two-level indentation max

If a method needs deeper nesting, extract a helper, add a guard clause, or
linearize the flow. Pyramids of doom are forbidden.

### 6. Construction of complex objects

Builders and constructors with multiple non-trivial arguments build into a
named local first, then pass that local to the consumer.

Bad:

```java
scheduler.schedule(
    sender,
    location,
    delay,
    new DelayedTeleportPrompt(messages.teleporting(), messages.teleported()));
```

Good:

```java
var teleporting = messages.teleporting();
var teleported = messages.teleported();

var prompt = new DelayedTeleportPrompt(teleporting, teleported);

scheduler.schedule(sender, location, delay, prompt);
```

### 7. Streams

Long stream chains are exploded — collect intermediate `toList()` results into
named variables. Short pipelines (`stream().filter(...).toList()`) are fine
when each operation is semantically obvious.

### 8. Variable naming

Intermediate `var` names are encouraged and must carry intent. Use `snap` for
config snapshots, `messages` for the message bundle, `<noun>Msg` for the
rendered string. No single-letter or generic names except in tight loops.

### 9. Vertical spacing

Blank lines separate cognitive shifts — between extraction and validation,
between validation and processing, between processing and side effect, between
unrelated mutations.

### 10. Horizontal width

Target ~100 columns. If a line starts to grow, that is the signal to extract,
not to wrap. The formatter (Spotless / google-java-format) will not produce
good wrapping for chains; defend against it by exploding before it has to.

---

## Project conventions

- **Lombok**: `@RequiredArgsConstructor`, `@NonNull` on parameters, `var` for
  locals. Lombok 1.18.46 with `lombok.config` at the repo root.
- **No internal null checks**: don't use `Objects.requireNonNull` in
  constructors or internal methods. `@NonNull` from Lombok covers it. Validate
  only at system boundaries.
- **No comments unless the why is non-obvious**: skip "what" comments — names
  carry that. Document hidden constraints, workarounds, invariants, surprising
  decisions. Never reference the current task, PR, or caller in a comment;
  those belong in the PR description and rot with the codebase.
- **Records over classes** for value carriers, command handlers, configs.
- **SRP strict**: one responsibility per class. Decompose god-classes
  (state / orchestration / timer / notifier / persistence each in its own
  class). Command classes stay thin — service does the work.
- **Switch with `default -> throw`** for exhaustive enums when the missing
  branch is a bug.
- **Text blocks for SQL**.
- **Template substitution**: a single `{token}` swap is fine as
  `template.replace("{token}", value)`. For two or more tokens in the same
  template, use `Placeholders.format(template, "k1", v1, "k2", v2, ...)` (or
  the `Map` overload for 4+) — one single-pass walk instead of N chained
  `String.replace` allocations, and the keys stay co-located so a missed token
  is obvious. Prefer this over MiniMessage `TagResolver` for fixed-name tokens.

## Module package layout

Each feature module under `modules/<name>/` follows a flat set of conventional
sub-packages. Pick the right one when adding a class — the
`ArchitecturePackageTest` enforces a few of these rules at build time:

- **`command/`** — `@Command`-annotated handlers, their `*Notifier`,
  `*Dispatcher`, `*Orchestrator`, `*ResultHandler`, `*Presenter`. UI/user-facing
  glue lives here, including notifiers that format messages.
- **`service/`** — application services (orchestration, in-memory state).
  Pure-behaviour services (`HealService`) and stateful services (`AfkService`)
  both live here. Persistence types are NOT allowed here — use `repository/`.
- **`repository/`** — relational persistence, with `*Repository` for CRUD,
  `*Table` for DDL/SQL constants, `*Cache` for in-memory mirrors. Do not add
  `*Store` classes. Repositories must not import Bukkit beyond `Material` /
  `Location`.
- **`domain/`** — domain records and enums (`Home`, `Mute`, `TeleportRequest`).
  Do not add `model/` packages. Cross-module imports are only allowed against
  `domain/`, `service/`, `history/`.
- **`listener/`** — Bukkit `Listener` implementations. Thin — delegate to
  services.
- **`menu/`** — `EssentialsMenu` subclasses and their `*ClickHandler`,
  `*EntryRenderer`, `*MenuState`. `menu/presentation/` for menu-specific
  rendering helpers when the count justifies it.
- **`config/`** — `*Config` records, `*Messages`, `MessagePair` factories. One
  config per module is the norm; nest when the config gets large.
- **`history/`** — append-only persistence specific to histories (teleport,
  tpa). Distinct from `repository/` because the access pattern is different
  (push-only + tail-read).

### Nominal sub-packages

A module may carry sub-packages outside this canonical set when a sub-domain
grows past a handful of cohesive classes that share one responsibility.
Examples already in tree: `chat/channel/`, `chat/format/`, `chat/guard/`,
`chat/placeholder/` (each is its own pipeline stage);
`homes/rename/` (orchestrator + notifier + sessions + timer + messages for
one flow); `tpa/bootstrap/` (per-area `*Bootstrap` factories, enforced by the
arch test).

Rules:
- Two-class rule: a nominal sub-package needs at least two cohesive classes.
  One `*Resolver` or one `*Helper` belongs in `service/`, not its own folder.
- The cross-module import rule still applies — only `service/`, `domain/`,
  `history/` are importable from other modules. Don't import into a nominal
  sub-package from outside the module.
- When in doubt, flatten. A renamed file is cheaper than a stale folder.

### Naming conventions for orchestration

- **`*Dispatcher`** — routes one entry point onto one of several handlers
  (`TeleportDispatcher` picks tp-to-player vs tp-to-pos by arg shape).
- **`*Orchestrator`** — sequences multiple steps (validate → service →
  notifier) for one logical operation (`GiveOrchestrator`,
  `HomeRenameOrchestrator`).
- **`*Executor`** — runs one focused async/heavy step
  (`TeleportRequestExecutor` calls `teleportAsync`).
- **`*Resolver`** — input → canonical value mapping (`HomeNameResolver`,
  `RealNameResolver`).
- **`*Notifier`** — formats and sends user-facing messages.

When in doubt, prefer the simplest one that fits — a class can be just
`<Module>Service` if it has only one job. Renaming an existing class is
cheaper than adding an unjustified second one.

## Core package layout

- **`database/`** has no Java files at the root. Keep shared database
  infrastructure under `async/`, `connection/`, `executor/`, `schema/`, or the
  concrete engine package such as `sqlite/`.
- **`module/`** root is limited to the public module API:
  `Module`, `AbstractModule`, `ModuleMetadata`. Internals live under
  `environment/`, `lifecycle/`, `registry/`, `registration/`, or `discovery/`.

## Architectural rules

- **CommandFramework v3.3.1** (artifact
  `com.github.HanielCota.CommandFramework:command-paper:${commandFrameworkVersion}`).
  Annotations live in `io.github.hanielcota.commandframework.annotation.*`,
  runtime in `…commandframework.core.*`, entry point
  `io.github.hanielcota.commandframework.paper.PaperCommandFramework`.
  - `@PermissionForOther` on the class requires **every** route in the class
    to have a `@TargetOrSelf` / `@OnlinePlayer` parameter — runtime crash on
    enable otherwise. Apply at method level when only some routes have a
    target.
  - Method-level `@Permission` overrides class-level (precedence:
    method → class → `@AutoPermission` → empty).
  - Prefer `@Alias` for additional subcommand names for clarity.
    `@Subcommand({"a", "b"})` with an array also works in v3.3.1.
  - `CommandActor.uniqueId()` returns **`String`**, not `UUID`. Compare with
    `player.getUniqueId().toString()`.
- **Paper 26.x workstation menus**:
  use `MenuType.X.create(player)` + `player.openInventory(view)` for virtual
  workstations.
- **`@PlayerOnly`** on any command whose body calls
  `actor.unwrap(Player.class)`. Without it the console path throws an unfriendly
  `IllegalArgumentException` stack trace instead of the framework's clean
  player-only message.
- **Async teleport everywhere**: `player.teleportAsync(loc).thenAccept(success
  -> ...)` instead of `player.teleport(loc)`. The future completes on the
  entity's owning thread so callbacks fire safely.
- **Async DB writes via `AsyncDatabaseWriter`**: hot writes go through a
  per-module `DefaultAsyncDatabaseWriter` registered as a module closeable.
  Reads cache in-memory where possible (see `WarpCache`, `CachedHomeRepository`).
- **Menu framework** (`com.github.HanielCota:MenuFramework`): list-style menus
  with info + pagination use the `PaginatedInfoMenus.register(...)` helper —
  do not duplicate the register shape.

## Workflow

- **Spotless before push**: `./gradlew spotlessApply build` (without
  `-x test`). CI runs both `spotlessCheck` and tests; both block merge.
- **Branch protection on main**: PR + CI green + all review threads resolved
  (including the Copilot bot's). Auto-merge stays BLOCKED until resolved.
- **Commits, PR titles, PR bodies, GitHub comments**: always in English.
  Conversation in this chat can be pt-BR; artifacts in version control stay
  English.
- **Direct commits to main**: preferred when feasible, but branch protection
  still requires the PR + CI flow.
