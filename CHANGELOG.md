# Changelog

All notable changes to Essentialist are tracked here. Entries follow the structure of [Keep a Changelog](https://keepachangelog.com/en/1.1.0/); versioning is [Semantic](https://semver.org/) but pre-1.0 patch and minor numbers track Gradle's `version` property in lockstep with each release.

## [Unreleased]

### Added

- Configurable bootstrap as `BootstrapStage` chain. Subclass `EssentialsBootstrap` and override `stages(EssentialsPlugin)` to insert, reorder, or replace stages from an addon. `EssentialsBootstrap.defaultStages(plugin)` exposes the shipped sequence.
- Architecture tests (`ArchitecturePackageTest`):
  - cross-module imports must go through `service`/`domain`/`history` packages,
  - repositories must not import Bukkit beyond `Material`/`Location`,
  - only `core.api` may implement the public `*Api` facades.
- Test coverage for the previously-untested core: `ModuleLifecycle`, `ModuleDependencyResolver`, `DefaultAsyncDatabaseWriter` (saturation + rejection + exception propagation), `SqliteDialect`, `MainThreadCallbacks`, and six `*ApiAdapter`s.
- `docs/FOLIA_SMOKE_TEST.md` — checklist for validating Folia compatibility on a live server.

### Changed

- `NearService.findNearby` pre-sizes the result list to `candidates.size()` to avoid the default ArrayList grow-by-50% on dense worlds.

## Releases shipped via merged PRs

These were the ABI-breaking changes published before this changelog existed. They are kept here so an addon author can match the changes against the released artifact.

### refactor: bounded writer queue, SqlDialect seam, typed EssentialsApi (PR #58)

#### Added

- `SqlDialect` interface + `SqliteDialect` implementation. Registered as a service. Every `*Table` class is now instance-based and takes a `SqlDialect` constructor argument.
- Six domain facades on `EssentialsApi`: `homes()`, `warps()`, `mutes()`, `nicks()`, `vanish()`, `teleports()` — each returns `Optional<XxxApi>` so addons degrade gracefully when a module is disabled.
#### Changed

- **BREAKING:** Removed `EssentialsApi.plugin()` and `EssentialsApi.services()`. Addons must use the typed accessors instead.
- **BREAKING:** `DefaultAsyncDatabaseWriter` now uses a bounded `LinkedBlockingQueue` (default capacity 10 000). Overflow surfaces via `RejectedExecutionException` on the returned `CompletableFuture`. The constructor accepts an optional capacity.
- **BREAKING:** `*Table` classes (`SqlHomeTable`, `WarpTable`, `MuteTable`, `NickTable`, `SpawnTable`, `TpaHistoryTable`, `TeleportHistoryTable`) are now instance-based. Constructor takes a `SqlDialect`. The static `install(SqlExecutor)` became an instance method.
- **BREAKING:** Repository classes that referenced `Table.UPSERT` constants (e.g. `SqlHomeRepository`, `MuteStore`, `NickStore`, `SpawnStore`, `WarpStore`) now take a table instance and use `table.upsert()` instead of a static constant.
### refactor: split AbstractModule into ModuleEnvironment + ModuleRegistrar (PR #57)

#### Changed

- **BREAKING:** `AbstractModule.onEnable()` signature changed from no-arg to `onEnable(ModuleEnvironment env, ModuleRegistrar registrar)`. Every module subclass must be updated.
- `AbstractModule` no longer exposes `plugin()`, `service()`, `config()`, `configure()`, `registerCommand()`, `registerMenu()`, `registerListener()`, `registerService()`, `registerCloseable()`, or `context()`. Those now live on `ModuleEnvironment` (read) and `ModuleRegistrar` (write).

#### Added

- `ModuleEnvironment` interface: `plugin()`, `service(Class)`, `findService(Class)`, `config(name, type, defaults)`.
- `ModuleRegistrar` interface: `listener`, `command`, `menu`, `closeable`, `provide`, `configure` (with implicit-type and explicit-type overloads).

### refactor: harden async callbacks, concurrency and DI semantics (PR #56)

#### Added

- `Scheduler#mainExecutor()` — `Executor` that hops back to the main (global region) thread.
- `MainThreadCallbacks#hop(future, consumer, operation)` — applies `orTimeout(30s)` + `thenAcceptAsync(mainExecutor)` + `exceptionally(log)` in one call.
- `ConfigHandle#onReload(Consumer<T>)` — per-handle reload listener delivering the freshly reloaded value.
- `DualReply` helper for the sender/target dual-message dispatch pattern used by heal/feed/kill/repair.

#### Changed

- **BREAKING:** `AsyncDatabaseWriter#submit(String, Runnable)` returns `CompletableFuture<Void>` instead of `boolean`. Fire-and-forget callers can ignore the return; callers that need to chain post-persist work attach to it via `.thenRun` / `.whenComplete`.
- **BREAKING:** `TeleportService` is no longer a utility class with static methods; it is now an instance class registered as a service. `TeleportService.toPlayer(a, b)` → `service.toPlayer(a, b)`.
- `ModuleServices.register` no longer silently unregisters before registering; duplicate service-type registration now throws `IllegalStateException` from the underlying `ServiceRegistry`.
- `HomeBucket` switched from `HashMap` + `synchronized` to `ConcurrentHashMap` with atomic operations.
- `InvseeSynchronizer` re-resolves `Bukkit.getPlayer(targetId)` inside the entity-scheduler callback to eliminate a TOCTOU on the captured reference.
- `AfkActivityListener` throttles `PlayerMoveEvent` per-player to 250 ms.

## Migration cheat sheet for addon authors

If your addon imports any of the breaking changes above, here is the minimal patch:

```diff
-public class MyAddonModule extends AbstractModule {
-  @Override protected void onEnable() {
-    var svc = service(MyService.class);
-    var cfg = config("addon", AddonConfig.class, AddonConfig::defaults);
-    registerCommand(new MyCommand(svc, cfg));
-  }
-}
+public class MyAddonModule extends AbstractModule {
+  @Override protected void onEnable(ModuleEnvironment env, ModuleRegistrar registrar) {
+    var svc = env.service(MyService.class);
+    var cfg = env.config("addon", AddonConfig.class, AddonConfig::defaults);
+    registrar.command(new MyCommand(svc, cfg));
+  }
+}
```

```diff
-boolean ok = writer.submit("save", () -> store.save(entry));
+writer.submit("save", () -> store.save(entry));  // ignore the future for fire-and-forget
+// or
+writer.submit("save", () -> store.save(entry))
+    .whenComplete((v, err) -> { /* react to failure */ });
```

```diff
-var future = TeleportService.toPlayer(sender, target);
-future.thenAccept(outcome -> /* runs off main thread on Paper */);
+var service = essentialsApi.teleports().orElseThrow();
+var future = service.toPlayer(sender, target);
+mainThreadCallbacks.hop(future, outcome -> /* runs on main thread */, "addon-tp");
```

```diff
-var registry = essentialsApi.services();
-var nick = registry.find(NickService.class);
+var nicks = essentialsApi.nicks();         // typed facade
+nicks.flatMap(api -> api.nickOf(playerId)).ifPresent(...);
```
