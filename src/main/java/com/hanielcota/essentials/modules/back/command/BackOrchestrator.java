package com.hanielcota.essentials.modules.back.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.back.menu.BackMenu;
import com.hanielcota.essentials.modules.back.service.BackPrefetch;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Sequences the {@code /back} flow: read history, refuse when empty, prefetch and open the menu.
 * Extracted from {@link BackCommand} so the command record stays a one-liner delegate.
 */
@RequiredArgsConstructor
public final class BackOrchestrator {

  private final @NonNull ConfigHandle<BackConfig> config;
  private final @NonNull TeleportHistory history;
  private final @NonNull MenuService menus;
  private final @NonNull BackPrefetch prefetch;

  public CommandResult openBack(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var senderId = sender.getUniqueId();

    var entries = this.history.list(senderId);
    if (entries.isEmpty()) {
      var snap = this.config.value();
      var noBackMsg = snap.noBack();

      return CommandResult.invalidUsage(noBackMsg);
    }

    this.prefetch.prefetch(senderId, entries);
    MenuOpenings.open(this.menus, sender, BackMenu.ID, actor);

    return CommandResult.success();
  }
}
