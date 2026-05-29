package com.hanielcota.essentials.modules.environment.config;

import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record EnvironmentConfig(
    @Comment(
            "Master switch for the whole environment-protection module. Set to false to fully"
                + " disable it — no listeners are registered, so it cannot interfere with other"
                + " plugins. Takes effect on server start / module reload.")
        boolean enabled,
    @Comment(
            "How the worlds list is interpreted. WHITELIST = protections active ONLY in the listed"
                + " worlds. BLACKLIST = active everywhere EXCEPT the listed worlds. Default"
                + " (BLACKLIST + empty list) protects every world.")
        WorldMode worldMode,
    @Comment("Worlds the worldMode applies to. Use the exact world folder name.")
        List<String> worlds,
    @Comment("Permission that bypasses the player-triggered protections (buckets). Blank disables.")
        String bypassPermission,
    @Comment("Stop fire from spreading to neighbouring blocks.") boolean preventFireSpread,
    @Comment("Stop blocks from being destroyed by fire (burning).") boolean preventBlockBurn,
    @Comment("Stop new fire from being created (ignition).") boolean preventIgnite,
    @Comment(
            "IgniteCause names blocked when preventIgnite is true. Empty = block every cause."
                + " Examples: SPREAD, LIGHTNING, LAVA, FLINT_AND_STEEL, FIREBALL.")
        List<String> blockedIgniteCauses,
    @Comment("Stop explosions from destroying terrain (creeper, TNT, wither, end crystal, beds).")
        boolean preventExplosionBlockDamage,
    @Comment(
            "Entity-explosion source types blocked when preventExplosionBlockDamage is true. Empty"
                + " = block every source. Examples: CREEPER, PRIMED_TNT, WITHER, END_CRYSTAL.")
        List<String> blockedExplosionSources,
    @Comment("Stop flowing water from spreading.") boolean preventWaterFlow,
    @Comment("Stop flowing lava from spreading.") boolean preventLavaFlow,
    @Comment("Stop players from emptying lava buckets.") boolean preventLavaBucket,
    @Comment("Stop players from emptying water buckets.") boolean preventWaterBucket,
    @Comment("Stop ice from melting back into water.") boolean preventIceMelt,
    @Comment("Stop water from freezing into ice.") boolean preventIceForm,
    @Comment("Stop snow layers from melting.") boolean preventSnowMelt,
    @Comment("Stop snow layers from forming.") boolean preventSnowForm,
    @Comment("Stop mobs from changing blocks (enderman pickup, zombies breaking doors, etc.).")
        boolean preventMobGriefing,
    @Comment(
            "EntityType names blocked from griefing when preventMobGriefing is true. Empty = block"
                + " every mob. Examples: ENDERMAN, ZOMBIE, SILVERFISH, RAVAGER.")
        List<String> blockedGriefEntities,
    @Comment("Stop lightning transformations (pig→piglin, villager→witch, charged creeper).")
        boolean preventLightningTransform) {

  public enum WorldMode {
    WHITELIST,
    BLACKLIST
  }

  public static EnvironmentConfig defaults() {
    return new EnvironmentConfig(
        true,
        WorldMode.BLACKLIST,
        List.of(),
        "essentials.environment.bypass",
        true,
        true,
        false,
        List.of(),
        true,
        List.of(),
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        true,
        List.of(),
        false);
  }

  public boolean appliesTo(String worldName) {
    var listed = worlds.contains(worldName);
    return worldMode == WorldMode.WHITELIST ? listed : !listed;
  }

  public boolean hasBypassPermission() {
    return !bypassPermission.isBlank();
  }

  public boolean isIgniteCauseBlocked(String causeName) {
    return blockedIgniteCauses.isEmpty() || blockedIgniteCauses.contains(causeName);
  }

  public boolean isExplosionSourceBlocked(String entityTypeName) {
    return blockedExplosionSources.isEmpty() || blockedExplosionSources.contains(entityTypeName);
  }

  public boolean isGriefEntityBlocked(String entityTypeName) {
    return blockedGriefEntities.isEmpty() || blockedGriefEntities.contains(entityTypeName);
  }
}
