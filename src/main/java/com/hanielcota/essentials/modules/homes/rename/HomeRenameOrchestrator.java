package com.hanielcota.essentials.modules.homes.rename;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.create.HomeCreateSessions;
import com.hanielcota.essentials.modules.homes.service.HomeNameValidator;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Drives the chat-driven rename flow: opens a prompt, captures the player's next chat message and
 * applies the rename via {@link HomeService}.
 *
 * <p>Session storage is delegated to {@link HomeRenameSessions}, name validation to {@link
 * HomeNameValidator}, timeout scheduling to {@link HomeRenameTimer} and chat messaging to {@link
 * HomeRenameNotifier}. The orchestrator itself only sequences these collaborators.
 */
@RequiredArgsConstructor
public final class HomeRenameOrchestrator {

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final HomeRenameSessions sessions;
  private final HomeCreateSessions createSessions;
  private final HomeNameValidator validator;
  private final HomeRenameTimer timer;
  private final HomeRenameNotifier notifier;

  private static boolean isCancel(@NonNull String input) {
    return input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("cancelar");
  }

  public void prompt(@NonNull Player player, @NonNull String homeName) {
    var snap = this.config.value();
    var timeout = snap.renameTimeout();
    var seconds = timeout.toSeconds();
    var uuid = player.getUniqueId();

    this.createSessions.cancel(uuid);

    Runnable onTimeout = () -> handleTimeout(player, seconds);
    var timeoutTask = this.timer.schedule(player, timeout, onTimeout);

    this.sessions.start(uuid, homeName, timeoutTask);
    this.notifier.sendPrompt(player, homeName, seconds);
  }

  public void handleInput(@NonNull Player player, @NonNull String oldName, @NonNull String input) {
    if (isCancel(input)) {
      this.notifier.sendCancelled(player);
      return;
    }

    if (!this.validator.isValid(input)) {
      this.notifier.sendInvalid(player);
      return;
    }

    var uuid = player.getUniqueId();
    var result = this.service.rename(uuid, oldName, input);

    this.notifier.sendResult(player, oldName, input, result);
  }

  public void handleTimeout(@NonNull Player player, long seconds) {
    var uuid = player.getUniqueId();
    var session = this.sessions.consume(uuid);

    if (session == null || !player.isOnline()) {
      return;
    }

    this.notifier.sendTimeout(player, seconds);
  }
}
