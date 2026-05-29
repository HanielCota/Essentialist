package com.hanielcota.essentials.modules.combat.config;

import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record CombatConfig(
    @Comment(
            "Master switch for the whole combat/damage module. Set to false to fully disable it —"
                + " no listeners are registered. Takes effect on server start / module reload.")
        boolean enabled,
    @Comment(
            "How the worlds list is interpreted. WHITELIST = rules active ONLY in the listed"
                + " worlds. BLACKLIST = active everywhere EXCEPT the listed worlds.")
        WorldMode worldMode,
    @Comment("Worlds the worldMode applies to. Use the exact world folder name.")
        List<String> worlds,
    @Comment("Permission that bypasses PvP blocking (attacker still deals damage). Blank disables.")
        String bypassPermission,
    @Comment("Allow player-versus-player damage. Set to false to block all PvP in scope.")
        boolean pvp,
    @Comment(
            "DamageCause names players are immune to. Empty = no immunity. Examples: FALL, FIRE,"
                + " FIRE_TICK, LAVA, DROWNING, FALLING_BLOCK, CONTACT, HOT_FLOOR.")
        List<String> immuneDamageCauses,
    @Comment("Stop players from losing hunger (food level never decreases).") boolean preventHunger,
    @Comment("Keep the player's inventory on death (drops nothing).") boolean keepInventory,
    @Comment("Keep the player's experience on death (drops no XP).") boolean keepExperience) {

  public enum WorldMode {
    WHITELIST,
    BLACKLIST
  }

  public static CombatConfig defaults() {
    return new CombatConfig(
        true,
        WorldMode.BLACKLIST,
        List.of(),
        "essentials.combat.bypass",
        true,
        List.of(),
        false,
        false,
        false);
  }

  public boolean appliesTo(String worldName) {
    var listed = worlds.contains(worldName);
    return worldMode == WorldMode.WHITELIST ? listed : !listed;
  }

  public boolean hasBypassPermission() {
    return !bypassPermission.isBlank();
  }

  public boolean isDamageCauseImmune(String causeName) {
    return immuneDamageCauses.contains(causeName);
  }
}
