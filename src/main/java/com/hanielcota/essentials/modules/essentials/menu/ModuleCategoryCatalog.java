package com.hanielcota.essentials.modules.essentials.menu;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Central mapping of module id to its {@link ModuleCategory} for the admin menu. Kept in one place
 * (instead of per-module metadata) so categorisation is editable without touching ~50 modules. Any
 * module id not listed here falls back to {@link ModuleCategory#OTHER}, so a newly added module
 * shows up automatically until it is given a home.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModuleCategoryCatalog {

  private static final Map<String, ModuleCategory> BY_ID = build();

  public static @NonNull ModuleCategory categoryOf(@NonNull String moduleId) {
    return BY_ID.getOrDefault(moduleId, ModuleCategory.OTHER);
  }

  private static Map<String, ModuleCategory> build() {
    var map = new HashMap<String, ModuleCategory>();

    put(
        map,
        ModuleCategory.PROTECTION,
        "leaves",
        "environment",
        "combat",
        "entity",
        "crops",
        "weather");
    put(map, ModuleCategory.TELEPORT, "teleport", "tpa", "back", "spawn", "warps", "homes");
    put(
        map,
        ModuleCategory.CHAT,
        "chat",
        "msg",
        "broadcast",
        "clearchat",
        "socialspy",
        "mute",
        "silencer",
        "title",
        "actionbar");
    put(
        map,
        ModuleCategory.ITEMS,
        "give",
        "enchant",
        "repair",
        "hat",
        "smelt",
        "compact",
        "clear",
        "trash",
        "enderchest",
        "invsee",
        "rename",
        "itemlore",
        "more",
        "workstations");
    put(
        map,
        ModuleCategory.PLAYER,
        "fly",
        "gamemode",
        "speed",
        "feed",
        "heal",
        "god",
        "vanish",
        "nick",
        "afk",
        "seen",
        "near",
        "online",
        "ping",
        "list",
        "light");
    put(
        map,
        ModuleCategory.ADMIN,
        "essentials",
        "whitelist",
        "kick",
        "kill",
        "info",
        "sudo",
        "spawnmob");

    return Map.copyOf(map);
  }

  private static void put(
      @NonNull Map<String, ModuleCategory> map,
      @NonNull ModuleCategory category,
      @NonNull String... ids) {
    for (var id : ids) {
      map.put(id, category);
    }
  }
}
