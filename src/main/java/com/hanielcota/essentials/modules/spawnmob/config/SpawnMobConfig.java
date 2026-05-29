package com.hanielcota.essentials.modules.spawnmob.config;

import com.hanielcota.essentials.shared.Placeholders;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record SpawnMobConfig(
    @Comment("Maximum mobs spawned by a single command (the requested amount is capped to this).")
        int maxPerCommand,
    @Comment("Confirmation shown to the sender. Placeholders: {count}, {mob}.") String spawned,
    @Comment("Shown when the given type is not a spawnable mob. Placeholders: {mob}.")
        String invalidMob) {

  public static SpawnMobConfig defaults() {
    return new SpawnMobConfig(
        20,
        "<green>Spawned <gold>{count}</gold> <gold>{mob}</gold>.",
        "<red><gold>{mob}</gold> is not a spawnable mob.");
  }

  public String formatSpawned(int count, @NonNull String mob) {
    return Placeholders.format(spawned, "count", count, "mob", mob);
  }

  public String formatInvalidMob(@NonNull String mob) {
    return invalidMob.replace("{mob}", mob);
  }
}
