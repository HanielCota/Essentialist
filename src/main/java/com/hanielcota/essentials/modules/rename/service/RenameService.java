package com.hanielcota.essentials.modules.rename.service;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public final class RenameService {

  public Result rename(Player player, @Nullable Component name) {

    var held = player.getInventory().getItemInMainHand();
    if (held.getType().isAir()) {
      return Result.EMPTY_HAND;
    }

    var meta = held.getItemMeta();
    meta.displayName(name);
    held.setItemMeta(meta);

    return name != null ? Result.RENAMED : Result.CLEARED;
  }

  public enum Result {
    RENAMED,
    CLEARED,
    EMPTY_HAND
  }
}
