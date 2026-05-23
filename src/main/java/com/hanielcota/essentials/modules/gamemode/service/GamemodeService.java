package com.hanielcota.essentials.modules.gamemode.service;

import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public final class GamemodeService {

  public Result apply(@NonNull Player player, @NonNull GameMode mode) {
    if (player.getGameMode() == mode) {
      return Result.ALREADY_IN_MODE;
    }

    player.setGameMode(mode);
    return Result.CHANGED;
  }

  public enum Result {
    CHANGED,
    ALREADY_IN_MODE
  }
}
