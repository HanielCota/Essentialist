package com.hanielcota.essentials.modules.list.config;

import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Group every player falls into when no other {@link GroupDefinition} matches. */
@ConfigSerializable
public record DefaultGroup(
    @Comment("Display name for the fallback group.") String name,
    @Comment("Material rendered in the player slot when no group matches.") Material material) {

  public static DefaultGroup of(String name, Material material) {
    return new DefaultGroup(name, material);
  }
}
