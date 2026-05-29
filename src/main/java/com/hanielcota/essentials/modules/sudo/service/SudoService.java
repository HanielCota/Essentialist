package com.hanielcota.essentials.modules.sudo.service;

import com.hanielcota.essentials.scheduler.Scheduler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/** Runs a command as another player, on that player's region thread. */
@RequiredArgsConstructor
public final class SudoService {

  private final Scheduler scheduler;

  public void run(@NonNull Player target, @NonNull String command) {
    var normalized = stripLeadingSlash(command);

    this.scheduler.runOnEntity(target, () -> target.performCommand(normalized));
  }

  private static String stripLeadingSlash(@NonNull String command) {
    if (command.startsWith("/")) {
      return command.substring(1);
    }

    return command;
  }
}
