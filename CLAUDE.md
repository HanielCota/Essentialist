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
- **`.replace("{token}", value)`** over Placeholders / MiniMessage TagResolver
  when the substitution is a fixed-name token.

## Architectural rules

- **CommandFramework v3.2.1** (artifact
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
  - `@Subcommand` accepts only one name; use `@Alias` for additional names.
  - `CommandActor.uniqueId()` returns **`String`**, not `UUID`. Compare with
    `player.getUniqueId().toString()`.
- **Paper 26.x — deprecated workstation openers**:
  `Player#openWorkbench(Location, boolean)` and the others
  (`openAnvil`/`openGrindstone`/`openStonecutter`/`openLoom`/`openCartographyTable`/`openSmithingTable`)
  are deprecated. Use `MenuType.X.create(player)` + `player.openInventory(view)`.
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
