# Folia smoke test

The plugin's async surfaces (PR #56 onwards) were designed Folia-safe but have only been validated on Paper. This checklist exercises the paths that are most likely to misbehave under Folia's threaded-regions model, so a single run on a Folia server confirms the design holds.

## Setup

1. Install a current Folia build (https://papermc.io/downloads/folia) at the same Minecraft version as `gradle.properties`.
2. Drop the shadowed `essentials-*.jar` into `plugins/`.
3. Start the server, join with two accounts (`alice`, `bob`).
4. Watch the server console for `WARN`/`SEVERE` from `com.hanielcota.essentials.*` while running through the scenarios below.

## Scenarios

### Teleport callbacks across regions (`MainThreadCallbacks.hop`)

- [ ] `alice` runs `/tp bob` while standing in a different region than `bob` (e.g. opposite ends of a 10 000-block flat world). Confirm `alice` sees the "teleported" message and `bob` sees the "X teleported to you" message. **Watch for**: any `IllegalStateException: Cannot read property of player from wrong region` in console.
- [ ] `alice` runs `/tphere bob`. Same expectation in reverse.
- [ ] With `alice` in nether and `bob` in overworld, `alice` runs `/tp bob`. Cross-dimension teleports go through `teleportAsync` then re-hop to main; the success message must still land.

### Menu click → teleport (`BackClickHandler`, `VanishClickHandler`)

- [ ] `alice` dies, respawns, runs `/back` and clicks an entry. Confirm the menu closes, the teleport happens, and `alice` sees the "voltou para" message. **Watch for**: menu staying open, or message arriving on the wrong tick.
- [ ] `alice` (with vanish permission) runs `/vanish menu` (or whatever opens the vanish target menu) and clicks `bob`'s head. Confirm `alice` arrives at `bob` and sees the teleported message.

### TPA accept (`TpAcceptCommand` + `TeleportRequestExecutor`)

- [ ] `alice` runs `/tpa bob`. `bob` runs `/tpaccept alice`. Confirm `alice` is teleported to `bob` and both see the matching pair of messages.
- [ ] Repeat the scenario but with `alice` and `bob` in regions far apart (likely on different worker threads under Folia).

### AsyncDatabaseWriter under load

- [ ] Run a small script (or paste-spam the command) to perform 100+ `/sethome` calls in quick succession from `alice`. Confirm none of them fail visibly and that the SQLite file has all rows after a restart.
- [ ] If you have access to a console-side load test, write 20 000 entries against the writer to trigger queue saturation. Confirm a single saturation warn per second appears (not per submission) and that the bounded queue rejects extra submissions via the returned `CompletableFuture` rather than blocking.

### Module enable/disable on `/essentials reload`

- [ ] `/essentials reload` while `alice` has an open menu (homes / vanish / list). Confirm the menu is force-closed and reopens cleanly; no zombie listeners.
- [ ] Confirm `ConfigHandle.onReload(...)` listeners fire (look for any module-specific reload-applied behaviour — e.g. AFK timer rebuilds with a new threshold).

### Vanish + region scheduling (`InvseeSynchronizer`)

- [ ] `alice` (staff) runs `/invsee bob`, drags an item between slots, then `bob` disconnects mid-edit. Confirm the queued write-back is dropped silently (the `Bukkit.getPlayer(targetId)` re-resolve inside the entity scheduler returns null) — **no NPE** in console.

## Pass criteria

All checkboxes ticked, zero unexpected `WARN`/`SEVERE` entries in the console from the plugin's packages. If anything fails, capture:

1. Folia version + JVM version
2. The exact command sequence
3. The full stack trace from console
4. Whether the failure reproduces on Paper (run the same scenario on a Paper build of the same MC version)
