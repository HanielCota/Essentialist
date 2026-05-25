package com.hanielcota.essentials.modules.list.config;

import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record GroupDefinition(
    @Comment("Internal id used in summary placeholders ({count_<id>}).") String id,
    @Comment("Display name for the group. MiniMessage allowed.") String name,
    @Comment("Permission node a player must hold to belong to this group.") String permission,
    @Comment("Material rendered in the player slot for members of this group.") Material material,
    @Comment("Higher priority wins when a player matches multiple groups.") int priority) {

  public static GroupDefinition of(
      String id, String name, String permission, Material material, int priority) {
    return new GroupDefinition(id, name, permission, material, priority);
  }
}
