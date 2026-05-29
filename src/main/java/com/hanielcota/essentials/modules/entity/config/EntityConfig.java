package com.hanielcota.essentials.modules.entity.config;

import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record EntityConfig(
    @Comment(
            "Master switch for the whole entity/item-protection module. Set to false to fully"
                + " disable it — no listeners are registered. Takes effect on server start / module"
                + " reload.")
        boolean enabled,
    @Comment(
            "How the worlds list is interpreted. WHITELIST = protections active ONLY in the listed"
                + " worlds. BLACKLIST = active everywhere EXCEPT the listed worlds.")
        WorldMode worldMode,
    @Comment("Worlds the worldMode applies to. Use the exact world folder name.")
        List<String> worlds,
    @Comment("Permission that bypasses the player-triggered protections. Blank disables bypassing.")
        String bypassPermission,
    @Comment("Protect item frames from being broken or having their item removed.")
        boolean protectItemFrames,
    @Comment("Protect paintings from being broken.") boolean protectPaintings,
    @Comment("Protect armor stands from being broken or manipulated.") boolean protectArmorStands,
    @Comment("Stop dropped items from despawning (they stay on the ground forever).")
        boolean preventItemDespawn,
    @Comment("Cancel creature spawns matching the reason/type filters below.")
        boolean preventMobSpawns,
    @Comment(
            "SpawnReason names cancelled when preventMobSpawns is true. Empty = every reason."
                + " Examples: NATURAL, SPAWNER, BREEDING, JOCKEY, REINFORCEMENTS.")
        List<String> blockedSpawnReasons,
    @Comment(
            "EntityType names cancelled when preventMobSpawns is true. Empty = every type."
                + " Examples: ZOMBIE, CREEPER, PHANTOM.")
        List<String> blockedSpawnTypes) {

  public enum WorldMode {
    WHITELIST,
    BLACKLIST
  }

  public static EntityConfig defaults() {
    return new EntityConfig(
        true,
        WorldMode.BLACKLIST,
        List.of(),
        "essentials.entity.bypass",
        false,
        false,
        false,
        false,
        false,
        List.of(),
        List.of());
  }

  public boolean appliesTo(String worldName) {
    var listed = worlds.contains(worldName);
    return worldMode == WorldMode.WHITELIST ? listed : !listed;
  }

  public boolean hasBypassPermission() {
    return !bypassPermission.isBlank();
  }

  public boolean isSpawnReasonBlocked(String reasonName) {
    return blockedSpawnReasons.isEmpty() || blockedSpawnReasons.contains(reasonName);
  }

  public boolean isSpawnTypeBlocked(String entityTypeName) {
    return blockedSpawnTypes.isEmpty() || blockedSpawnTypes.contains(entityTypeName);
  }
}
