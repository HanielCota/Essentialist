# TPA Structure Cleanup Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reduce the largest accidental complexity in the TPA module while preserving existing behavior.

**Architecture:** Keep the public module package layout intact. Move TPA wiring into focused bootstrap classes, move favorite menu sorting/suggestion logic into a query helper, and move pending action click behavior into a handler so menu classes become mostly rendering/navigation.

**Tech Stack:** Java 25, Gradle, JUnit 6, Paper API, MenuFramework, Spotless.

---

### Task 1: Architecture Guardrails

**Files:**
- Modify: `src/test/java/com/hanielcota/essentials/ArchitecturePackageTest.java`

- [ ] Add tests that fail while `TpaModule`, `TpaFavoritesMenu`, and `TpaPendingActionMenu` remain oversized.
- [ ] Run the architecture tests and confirm failure.

### Task 2: Split TPA Bootstrap

**Files:**
- Modify: `src/main/java/com/hanielcota/essentials/modules/tpa/TpaModule.java`
- Create: `src/main/java/com/hanielcota/essentials/modules/tpa/TpaPersistenceBootstrap.java`
- Create: `src/main/java/com/hanielcota/essentials/modules/tpa/TpaRuntimeBootstrap.java`
- Create: `src/main/java/com/hanielcota/essentials/modules/tpa/TpaMenuBootstrap.java`
- Create: `src/main/java/com/hanielcota/essentials/modules/tpa/TpaCommandBootstrap.java`

- [ ] Move repository/table/writer construction into `TpaPersistenceBootstrap`.
- [ ] Move request runtime/shared helper/favorite runtime construction into `TpaRuntimeBootstrap`.
- [ ] Move menu registration into `TpaMenuBootstrap`.
- [ ] Move command registration into `TpaCommandBootstrap`.
- [ ] Keep `TpaModule.onEnable` as the high-level module flow.

### Task 3: Split Favorite Menu Logic

**Files:**
- Modify: `src/main/java/com/hanielcota/essentials/modules/tpa/menu/TpaFavoritesMenu.java`
- Create: `src/main/java/com/hanielcota/essentials/modules/tpa/menu/TpaFavoriteBrowser.java`
- Create: `src/main/java/com/hanielcota/essentials/modules/tpa/menu/TpaFavoriteMenuRenderer.java`

- [ ] Move sorting and suggestions into `TpaFavoriteBrowser`.
- [ ] Move item template rendering and placeholder replacement into `TpaFavoriteMenuRenderer`.
- [ ] Leave `TpaFavoritesMenu` responsible for registration, slot assembly, and navigation.

### Task 4: Split Pending Action Behavior

**Files:**
- Modify: `src/main/java/com/hanielcota/essentials/modules/tpa/menu/TpaPendingActionMenu.java`
- Create: `src/main/java/com/hanielcota/essentials/modules/tpa/menu/TpaPendingActionHandler.java`
- Create: `src/main/java/com/hanielcota/essentials/modules/tpa/menu/TpaPendingActionRenderer.java`

- [ ] Move accept/deny/block click effects into `TpaPendingActionHandler`.
- [ ] Move item template rendering into `TpaPendingActionRenderer`.
- [ ] Leave `TpaPendingActionMenu` responsible for menu registration and slot layout.

### Task 5: Verify

**Files:**
- All modified Java files.

- [ ] Run `./gradlew.bat test`.
- [ ] Run `./gradlew.bat spotlessApply build`.
- [ ] Inspect `git diff --stat` and summarize the resulting structure.
