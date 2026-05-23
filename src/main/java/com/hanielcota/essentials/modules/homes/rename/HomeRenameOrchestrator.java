package com.hanielcota.essentials.modules.homes.rename;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.name.HomeNameValidator;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.scheduler.Task;
import com.hanielcota.essentials.util.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Drives the chat-driven rename flow: opens a prompt with a timeout, captures the player's next
 * chat message, and applies the rename via {@link HomeService}. Delegates session storage to {@link
 * HomeRenameSessions} and name validation to {@link HomeNameValidator}; the orchestrator itself
 * only sequences these collaborators and emits the user-visible chat lines.
 */
@RequiredArgsConstructor
public final class HomeRenameOrchestrator implements HomeRenamePrompter {

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final Scheduler scheduler;
  private final HomeRenameSessions sessions;
  private final HomeNameValidator validator;

  private static boolean isCancel(@NonNull String input) {
    return input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("cancelar");
  }

  @Override
  public void prompt(@NonNull Player player, @NonNull String homeName) {
    var snap = config.value();
    var timeout = snap.renameTimeout();
    var seconds = timeout.toSeconds();
    var uuid = player.getUniqueId();

    var timeoutTask =
        timeout.isZero() || timeout.isNegative()
            ? Task.noop()
            : scheduler.runOnEntityLater(player, () -> handleTimeout(player, seconds), timeout);

    sessions.start(uuid, homeName, timeoutTask);

    var promptMsg = HomeRenameMessages.prompt(snap.messages(), homeName, seconds);
    player.sendMessage(ComponentUtils.mini(promptMsg));
  }

  public void handleInput(@NonNull Player player, @NonNull String oldName, @NonNull String input) {
    var messages = config.value().messages();
    var uuid = player.getUniqueId();

    if (isCancel(input)) {
      player.sendMessage(ComponentUtils.mini(messages.renameCancelled()));
      return;
    }

    if (!validator.isValid(input)) {
      player.sendMessage(ComponentUtils.mini(messages.invalidName()));
      return;
    }

    var result = service.rename(uuid, oldName, input);
    var resultMsg = HomeRenameMessages.result(messages, oldName, input, result);

    player.sendMessage(ComponentUtils.mini(resultMsg));
  }

  public void handleTimeout(@NonNull Player player, long seconds) {
    var uuid = player.getUniqueId();
    var session = sessions.consume(uuid);

    if (session == null || !player.isOnline()) {
      return;
    }

    var line = HomeRenameMessages.timeout(config.value().messages(), seconds);
    player.sendMessage(ComponentUtils.mini(line));
  }
}
