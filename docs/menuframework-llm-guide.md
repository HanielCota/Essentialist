# MenuFramework LLM Guide

Canonical usage guide for AI assistants generating code with MenuFramework in the Essentialist
project. Follow the API below exactly. Do not invent classes, methods, or lifecycle abstractions
that are not listed here.

## What MenuFramework Is

MenuFramework is a Java 25 library for Paper inventory menus. It is not a standalone plugin.
Essentialist shades and relocates it.

Primary package:

```java
com.github.hanielcota.menuframework
```

## Installation (build.gradle)

```groovy
repositories {
    maven { url = 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.HanielCota:MenuFramework:v1.2.0'
}

// Shadow + relocate:
shadowJar {
    relocate 'com.github.hanielcota.menuframework', 'com.hanielcota.essentials.libs.menuframework'
}
```

## Plugin Lifecycle

One `MenuService` per plugin. Created at bootstrap, shut down in `EssentialsCore.onDisable`.

```java
// MenuBootstrap.java
var menuService = MenuFramework.create(plugin);
context.services().register(MenuService.class, menuService);

// EssentialsCore.java — shutdown order: modules → MenuService → database
menuHandle.ifPresent(MenuService::shutdown);
```

Never use `MenuFramework.initialize(plugin)` — the project does not use singleton mode.

## Project Conventions

### EssentialsMenu interface

Every menu implements `EssentialsMenu`:

```java
public interface EssentialsMenu {
    @NonNull String id();
    void register(@NonNull MenuService menus);
}
```

### Module registration

Use `ModuleMenus` to register a menu + cleanup closeable:

```java
// In module's onEnable:
var menus = env.service(MenuService.class);
var menu = new MyMenu(config, service, renderer, clickHandler);
registrar.menu(menu);   // internally calls ModuleMenus.register(menus, menu, closeables)
```

`ModuleMenus.register()` calls `menu.register(menus)` and registers a closeable that
calls `menus.unregisterDefinition(menuId)`.

### Menu opening from commands

Always use `MenuOpenings.open()` — never call `menus.open()` directly:

```java
// In command handler:
MenuOpenings.open(menus, player, MyMenu.ID, actor);
```

`MenuOpenings.open()` wraps the async `menus.open()` with error logging and a failure message.

## Basic Menu (non-paginated)

For static dialogs with a fixed number of slots (e.g. confirm/cancel):

```java
@Override
public void register(@NonNull MenuService menus) {
    var snap = this.config.value();

    var title = ComponentUtils.mini(snap.title());
    var rows = snap.rows();

    var promptTemplate = MenuTemplates.simple(material, name, lore);
    var yesTemplate = MenuTemplates.simple(material, name);
    var noTemplate = MenuTemplates.simple(material, name);

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(title);
    builder.slot(promptSlot, promptTemplate, click -> {});         // decorative
    builder.slot(yesSlot, yesTemplate, click -> this::confirm);
    builder.slot(noSlot, noTemplate, click -> this::cancel);

    var menu = builder.build();
    menu.register();
}
```

Do NOT chain `.build().register()` inline — always use two lines (scope bursting).

## Paginated Menu (list + dynamic content)

### Option A: PaginatedInfoMenus (preferred for simple lists)

When a menu is a list of items + a single info slot + page navigation, use `PaginatedInfoMenus`:

```java
@Override
public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var rows = snap.effectiveRows();
    var title = snap.menuTitle();
    var contentSlots = snap.effectiveContentSlots();
    var navigation = snap.navigation();       // NavigationButtonsConfig
    var infoSlot = snap.effectiveInfoSlot();
    var infoTemplate = buildInfoTemplate(snap);

    PaginatedInfoMenus.register(
        menus, ID, rows, title, contentSlots, navigation,
        infoSlot, infoTemplate, this::buildSlots);
}
```

### Option B: Manual pagination setup (when you need extra fixed slots, back buttons, etc.)

```java
@Override
public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var rows = snap.effectiveRows();
    var title = ComponentUtils.mini(snap.menuTitle());
    var contentSlots = snap.effectiveContentSlots();

    var paginationBuilder = PaginationConfig.builder().contentSlots(contentSlots);
    if (rows > 1) {
        PageNavigation.apply(menus, paginationBuilder, ID, rows, snap.navigation());
    }
    var pagination = paginationBuilder.build();

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(title);
    builder.pagination(pagination);
    builder.slot(infoSlot, infoTemplate, null);   // fixed decorative slot
    builder.dynamicContent(this::buildSlots);

    var menu = builder.build();
    menu.register();
}
```

### Dynamic content provider signature

```java
private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    // collect data, build SlotDefinition list
    // use slot = -1 for "next available content slot"
    // use explicit slot >= 0 to place at a specific position (e.g. back button, filter button)
}
```

Dynamic content items normally use slot `-1`; the pagination engine projects them into configured
content slots. Explicit `slot >= 0` places the item at that exact slot regardless of page.

## MenuBuilder API (verified subset)

```java
MenuFramework.builder("id", menus)   // always pass MenuService

.rows(int)                           // 1..6
.title(Component)                    // via ComponentUtils.mini(miniMessage)
.pagination(PaginationConfig)        // required for dynamicContent()
.dynamicContent((Player, MenuSession) -> List<SlotDefinition>)
.slot(int slot, ItemTemplate template, ClickHandler handler)  // handler can be null (decorative)
.allowShiftClick(boolean)            // only if player needs to shift-click items in the menu
.build()                             // returns MenuDefinition
```

`.register()` is called on the returned `MenuDefinition`, NOT on the builder:

```java
var menu = builder.build();
menu.register();
```

## PaginationConfig (verified subset)

```java
PaginationConfig.builder()
    .contentSlots(List<Integer>)          // required
    .navigationSlots(List<Integer>)       // set by PageNavigation.apply()
    .previousTemplate(String templateId)  // pre-registered template ID
    .nextTemplate(String templateId)      // pre-registered template ID
    .hideDisabledNavigation(true)         // set by PageNavigation.apply()
    .build()
```

Content slots and navigation slots must be disjoint.

## PageNavigation

Centralizes prev/next button setup for paginated menus:

```java
PageNavigation.apply(menus, paginationBuilder, menuId, rows, navigationConfig);
```

This internally:
1. Creates prev/next `ItemTemplate` from `NavigationButtonsConfig`
2. Registers them via `menus.registerTemplate(menuId + ".previous", template)`
3. Computes effective slots via `MenuLayouts.sanitizeSlot()`
4. Sets `navigationSlots()`, `previousTemplate()`, `nextTemplate()`, `hideDisabledNavigation(true)`

## NavigationButtonsConfig

Config record for prev/next button appearance:

```java
public record NavigationButtonsConfig(
    Material material,      // default ARROW
    int previousSlot,       // raw configured slot
    int nextSlot,           // raw configured slot
    String previousName,    // MiniMessage string
    String nextName)        // MiniMessage string

// Effective slots (fallback to last row if invalid):
config.effectivePreviousSlot(rows);
config.effectiveNextSlot(rows);
```

## ItemTemplate (verified subset)

```java
// Via MenuTemplates utility:
MenuTemplates.simple(material, name)                       // no lore
MenuTemplates.simple(material, name, List<String> lore)    // with lore, italic=false
MenuTemplates.info(material, name, List<String> lore)      // + HIDE_ATTRIBUTES flag, italic=false

// Via builder (when you need head, glow, custom flags):
var builder = ItemTemplate.builder(material);
builder.name(miniMessageString);
builder.lore(loreStringsArray);
builder.italic(false);
builder.head(playerUUID);          // player head with live skin
builder.head(base64Texture);       // or custom texture
builder.flags(ItemFlag.HIDE_ATTRIBUTES);
var template = builder.build();

// Conditional head helper:
MenuTemplates.applyHead(builder, icon, useSkin, headTexture, playerId);
```

Factory shortcuts used in the codebase:

```java
ItemTemplate.builder(Material)                     // start building
ItemTemplate.builder(Material).name(String).build()
```

Not used: `ItemTemplate.of()`, `ItemTemplate.glowing()`, `ItemTemplate.head()`, `ItemTemplate.filler()`.

## SlotDefinition

```java
SlotDefinition.of(slot, template, handler)

// slot = -1  → auto-assign to next content slot (paginated)
// slot >= 0  → fixed position (decorative, back button, filter, etc.)
// handler = null → decorative slot, no click action
```

## ClickContext (verified subset)

```java
click.player()              // Player who clicked
click.switchTo("menuId")    // close current + open target (preferred over open())
click.refresh()             // re-render current menu
click.reply("miniMessage")  // send message to player
click.session()             // current MenuSession
```

Always use `click.switchTo()` for navigation, never `click.open()` — `switchTo` closes + opens
atomically, avoiding client-server desync.

## MenuService (verified subset)

```java
MenuFramework.create(plugin)             // returns MenuService
menus.open(player, "menuId")             // returns CompletableFuture<MenuSession>
menus.registerTemplate("id", template)   // pre-register template for pagination nav
menus.unregisterDefinition("menuId")     // cleanup on module disable
menus.shutdown()                         // on plugin disable
```

Not used: `getDefinition()`, `getTemplate()`, `getSession()`, `closeSession()`, `closeAllSessions()`,
`getMetrics()`, `preloader()`.

## MenuSession (verified subset)

```java
session.totalPages()   // 0 for non-paginated menus
```

Not used: `viewerId()`, `menuId()`, `view()`, `currentPage()`, `setPage()`, `refresh()`, `close()`,
`isSameView()`, `isDisposed()`, `dispose()`, `updateSlot()`, `updateSlots()`, `updateTitle()`,
`setAttribute()`, `getAttribute()`, `newSelection()`.

## Utility Classes (project-internal)

### MenuLayouts

```java
MenuLayouts.clampRows(rows)                      // clamp to 1..6
MenuLayouts.slotCount(rows)                      // rows * 9
MenuLayouts.allSlots(rows)                       // List 0..slotCount-1
MenuLayouts.fallbackContentSlots(rows, count)    // first `count` slots, clamped
MenuLayouts.sanitizeSlots(configured, rows)      // filter invalid + deduplicate
MenuLayouts.sanitizeSlots(configured, rows, fallback)   // with explicit fallback
MenuLayouts.sanitizeSlot(configuredSlot, rows, fallbackSlot)  // single slot
```

### MenuTemplates

```java
MenuTemplates.simple(material, name)
MenuTemplates.simple(material, name, lore)
MenuTemplates.info(material, name, lore)
MenuTemplates.applyHead(builder, icon, useSkin, headTexture, playerId)
```

### ComponentUtils

```java
ComponentUtils.mini("miniMessageString")   // MiniMessage → Component
```

## Common Mistakes

1. Do NOT use `MenuFramework.builder("id")` without `menus`. Always pass `MenuService`.
2. Do NOT chain `.build().register()` inline — use two lines (scope bursting).
3. Do NOT put fixed-position items (back buttons, info slots) inside `dynamicContent()` builder.
   Use `builder.slot(slot, template, handler)` in `register()` instead.
4. Do NOT configure `dynamicContent()` without `pagination()` first.
5. Do NOT skip `PageNavigation.apply()` for paginated menus with more than 1 row — players
   will be stuck on page 1.
6. Do NOT use `PaginationConfig` for menus with a fixed set of slots that will never paginate
   (e.g. hub menus with 5-7 navigation items) — use `builder.slot()` for each item.
7. Do NOT use `ctx.open()` — use `ctx.switchTo()` for navigation.
8. Do NOT call `player.openInventory()` or `player.closeInventory()` directly from click handlers.
9. Do NOT store raw `ItemStack` in `slot()` — use `ItemTemplate`.
10. Do NOT call `menus.open()` directly from commands — use `MenuOpenings.open()`.
11. Do NOT forget to call `MenuService.shutdown()` on plugin disable.
