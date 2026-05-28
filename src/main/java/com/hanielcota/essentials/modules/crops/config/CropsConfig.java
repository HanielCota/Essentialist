package com.hanielcota.essentials.modules.crops.config;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record CropsConfig(
    @Comment(
            "Master switch for the whole crops module. Set to false to fully disable it — no"
                + " listeners are registered, so it cannot interfere with other crop/farming"
                + " plugins. Takes effect on server start / module reload.")
        boolean enabled,
    @Comment(
            "How the worlds list is interpreted. WHITELIST = protection is active ONLY in the"
                + " listed worlds. BLACKLIST = active everywhere EXCEPT the listed worlds.")
        WorldMode worldMode,
    @Comment("Worlds the worldMode applies to. Use the exact world folder name.")
        List<String> worlds,
    @Comment(
            "Permission that lets a player bypass break/trample protection (e.g. staff)."
                + " Leave blank to disable bypassing entirely.")
        String bypassPermission,
    @Comment(
            "Crop Material names this module manages (protection, auto-replant, explosions)."
                + " Leave empty to manage ALL crops. Example: WHEAT, CARROTS, POTATOES.")
        List<String> managedCrops,
    @Comment(
            "EntityType names blocked from trampling/eating crops. Leave empty to block ALL mobs."
                + " Example: RABBIT, VILLAGER.")
        List<String> blockedMobs,
    @Comment("Prevent players from breaking unripe (not fully grown) crops.") boolean preventBreak,
    @Comment("Prevent players from trampling farmland.") boolean preventTrampling,
    @Comment("Prevent mobs from trampling farmland.") boolean preventMobTrampling,
    @Comment("Prevent mobs (e.g. rabbits) from eating or damaging crops.") boolean preventMobDamage,
    @Comment("Remove crop blocks from explosion block lists (creepers, TNT).")
        boolean preventExplosion,
    @Comment("Also keep farmland intact during explosions (otherwise it reverts to dirt).")
        boolean preventExplosionFarmland,
    @Comment("Prevent farmland from drying out due to lack of water.") boolean permanentHydration,
    @Comment("Automatically replant fully grown crops after harvest.") boolean autoReplant,
    @Comment(
            "When auto-replanting, remove one seed from the harvest drops so the replant is not"
                + " free. No effect if the harvest dropped no matching seed.")
        boolean replantConsumesSeed,
    @Comment("Player feedback messages.") CropsMessages messages) {

  public enum WorldMode {
    WHITELIST,
    BLACKLIST
  }

  public static CropsConfig defaults() {
    return new CropsConfig(
        true,
        WorldMode.WHITELIST,
        List.of("world"),
        "essentials.crops.bypass",
        List.of(),
        List.of(),
        true,
        true,
        true,
        true,
        true,
        false,
        true,
        true,
        false,
        CropsMessages.defaults());
  }

  public boolean appliesTo(String worldName) {
    var listed = worlds.contains(worldName);
    return worldMode == WorldMode.WHITELIST ? listed : !listed;
  }

  public boolean isCropAllowed(Material material) {
    return managedCrops.isEmpty() || managedCrops.contains(material.name());
  }

  public boolean isMobBlocked(EntityType type) {
    return blockedMobs.isEmpty() || blockedMobs.contains(type.name());
  }

  public boolean hasBypassPermission() {
    return !bypassPermission.isBlank();
  }
}
