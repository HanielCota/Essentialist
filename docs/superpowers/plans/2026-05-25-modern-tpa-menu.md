# Modern TPA Menu Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Turn `/tpa` without arguments into a modern hub with player stats, history access, configurable icons, and a settings menu for `/tpa` and `/tpahere` receive toggles.

**Architecture:** Add a persistent `TpaProfile` cache backed by SQLite. `TeleportRequestService` consults that cache before creating a request and increments the sender's total sent counter after a request is accepted into the pending store. Menus read the same profile service to render per-player stats and toggle settings.

**Tech Stack:** Java 25, Paper API, MenuFramework `ItemTemplate`/`SlotDefinition`, Configurate YAML, SQLite via `SqlExecutor`.

---

### Task 1: TPA Profile Model And Cache

**Files:**
- Create: `src/main/java/com/hanielcota/essentials/modules/tpa/domain/TpaProfile.java`
- Create: `src/main/java/com/hanielcota/essentials/modules/tpa/repository/TpaProfileTable.java`
- Create: `src/main/java/com/hanielcota/essentials/modules/tpa/repository/TpaProfileRepository.java`
- Create: `src/main/java/com/hanielcota/essentials/modules/tpa/service/TpaProfileService.java`
- Test: `src/test/java/com/hanielcota/essentials/modules/tpa/service/TpaProfileServiceTest.java`

- [ ] Write tests for default profile, independent `/tpa` and `/tpahere` toggles, and sent-count increment.
- [ ] Run `.\gradlew.bat test --tests "*TpaProfileServiceTest"` and confirm the tests fail because classes do not exist.
- [ ] Implement `TpaProfile`, repository/table, and service cache with async saves.
- [ ] Re-run `.\gradlew.bat test --tests "*TpaProfileServiceTest"` and confirm the tests pass.

### Task 2: Request Blocking And Counter Integration

**Files:**
- Modify: `src/main/java/com/hanielcota/essentials/modules/tpa/service/TeleportRequestService.java`
- Modify: `src/main/java/com/hanielcota/essentials/modules/tpa/command/TpaRequests.java`
- Modify: `src/main/java/com/hanielcota/essentials/modules/tpa/config/TpaMessages.java`
- Modify: `src/main/java/com/hanielcota/essentials/modules/tpa/TpaModule.java`
- Test: `src/test/java/com/hanielcota/essentials/modules/tpa/service/TeleportRequestServiceTest.java`

- [ ] Write tests proving blocked `/tpa` and blocked `/tpahere` do not create pending requests.
- [ ] Run the targeted test and confirm RED.
- [ ] Inject `TpaProfileService` into `TeleportRequestService`.
- [ ] Change creation to return `Optional<TeleportRequest>` and add configured blocked messages.
- [ ] Wire profile table install, repository load, and writer in `TpaModule`.
- [ ] Re-run targeted tests and confirm GREEN.

### Task 3: Hub And Settings Menus

**Files:**
- Modify: `src/main/java/com/hanielcota/essentials/modules/tpa/config/TpaHelpMenuConfig.java`
- Create: `src/main/java/com/hanielcota/essentials/modules/tpa/config/TpaSettingsMenuConfig.java`
- Modify: `src/main/java/com/hanielcota/essentials/modules/tpa/config/TpaConfig.java`
- Modify: `src/main/java/com/hanielcota/essentials/modules/tpa/menu/TpaHelpMenu.java`
- Create: `src/main/java/com/hanielcota/essentials/modules/tpa/menu/TpaSettingsMenu.java`
- Modify: `src/main/java/com/hanielcota/essentials/modules/tpa/TpaModule.java`

- [ ] Add config fields for profile head, TPA icon, history icon, settings icon, toggle items, and back item.
- [ ] Render `/tpa` hub dynamically for the viewer, including `{player}`, `{sent}`, `{receive_tpa}`, and `{receive_tpahere}` placeholders.
- [ ] Use the viewer skin when `usePlayerHead` is true; otherwise use configured material or configured custom head texture.
- [ ] Add settings menu toggles that flip the matching `TpaProfileService` setting and refresh the menu.
- [ ] Register both menus in `TpaModule`.

### Task 4: Verification

**Files:**
- All changed files.

- [ ] Run the stale API search command used in this session against source and docs.
- [ ] Run `.\gradlew.bat build --warning-mode all --rerun-tasks`.
- [ ] Inspect `git diff --stat` and confirm only TPA feature files plus existing API cleanup files are changed.
