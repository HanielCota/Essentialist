package com.hanielcota.essentials.modules.spawn.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Every chat line {@code /spawn} and {@code /setspawn} can send. */
@ConfigSerializable
public record SpawnMessages(
    @Comment("/setspawn confirmation.") String spawnSet,
    @Comment("Shown when /spawn runs and no spawn has been configured yet.") String noSpawn,
    @Comment("Shown when the configured spawn world is no longer loaded.") String worldGone,
    @Comment("Shown on /spawn start when a delay is configured. Placeholders: {seconds}.")
        String teleporting,
    @Comment("Shown after /spawn completes successfully.") String teleported,
    @Comment("Shown when the delayed teleport is cancelled by damage or disconnect.")
        String cancelled,
    @Comment("Shown when the teleport itself fails.") String failed) {

  public static SpawnMessages defaults() {
    return new SpawnMessages(
        "<green>Spawn set to your current location.",
        "<red>The spawn has not been configured yet.",
        "<red>The spawn world is not loaded.",
        "<yellow>Teleporting in <gold>{seconds}s</gold>. Do not take damage.",
        "<green>You were teleported to spawn.",
        "<red>Teleport cancelled.",
        "<red>The teleport could not be completed.");
  }
}
