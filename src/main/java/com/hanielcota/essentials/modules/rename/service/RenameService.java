package com.hanielcota.essentials.modules.rename.service;

import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

public final class RenameService {

  public enum Result {
    RENAMED,
    CLEARED,
    EMPTY_HAND
  }

  public Result rename(Player player, @Nullable Component name) {
    Objects.requireNonNull(player, "player");

    var held = player.getInventory().getItemInMainHand();
    if (held.getType().isAir()) {
      return Result.EMPTY_HAND;
    }

    var meta = held.getItemMeta();
    meta.displayName(name);
    held.setItemMeta(meta);

    return name != null ? Result.RENAMED : Result.CLEARED;
  }
}
