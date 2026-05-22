package com.hanielcota.essentials.modules.trash.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record TrashConfig(
    @Comment("Number of rows in the trash menu (1-6).") int rows,
    @Comment("Trash menu title.") String title) {

  public static TrashConfig defaults() {
    return new TrashConfig(4, "Lixeira");
  }

  /** Inventory slot count: rows (clamped to 1-6) times nine. */
  public int size() {
    return Math.clamp(rows, 1, 6) * 9;
  }
}
