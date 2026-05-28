package com.hanielcota.essentials.modules.homes.create;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameSessions;
import com.hanielcota.essentials.modules.homes.command.HomeLimitReachedMessageResolver;
import com.hanielcota.essentials.modules.homes.service.HomeNameValidator;
import com.hanielcota.essentials.modules.homes.service.HomePromptCancellation;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.scheduler.Task;
import java.time.Duration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Drives the chat-driven create-home flow: opens a prompt, captures the player's next chat message
 * and creates the home via {@link HomeService#createNew} at the location captured the moment the "+
 * Nova home" button was clicked (so a player moving while typing still gets the home where they
 * intended).
 *
 * <p>Mutually exclusive with rename: starting a create prompt cancels any pending rename session
 * for the same player so the chat listeners don't double-process the next message.
 */
@RequiredArgsConstructor
public final class HomeCreateOrchestrator {

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final HomeCreateSessions sessions;
  private final HomeRenameSessions renameSessions;
  private final HomeNameValidator validator;
  private final Scheduler scheduler;
  private final HomeCreateNotifier notifier;
  private final HomeLimitReachedMessageResolver limitReachedResolver;

  public void prompt(@NonNull Player player) {
    var snap = this.config.value();
    var timeout = snap.renameTimeout();
    var seconds = timeout.toSeconds();
    var uuid = player.getUniqueId();

    this.renameSessions.cancel(uuid);

    var location = player.getLocation();
    Runnable onTimeout = () -> handleTimeout(player, seconds);
    var task = scheduleTimeout(player, timeout, onTimeout);

    this.sessions.start(uuid, location, task);
    this.notifier.sendPrompt(player, seconds);
  }

  public void handleInput(
      @NonNull Player player, @NonNull Location location, @NonNull String input) {
    if (HomePromptCancellation.isCancel(input)) {
      this.notifier.sendCancelled(player);
      return;
    }

    if (!this.validator.isValid(input)) {
      this.notifier.sendInvalid(player);
      return;
    }

    var snap = this.config.value();
    var defaultMaterial = snap.defaultMaterial();
    var outcome = this.service.createNew(player, input, location, defaultMaterial);

    switch (outcome) {
      case CREATED -> this.notifier.sendCreated(player, input);
      case ALREADY_EXISTS -> this.notifier.sendAlreadyExists(player, input);
      case LIMIT_REACHED -> {
        var msg = this.limitReachedResolver.resolve(input, player);
        this.notifier.sendLimitReached(player, msg);
      }
    }
  }

  public void handleTimeout(@NonNull Player player, long seconds) {
    var uuid = player.getUniqueId();
    var session = this.sessions.consume(uuid);

    if (session == null || !player.isOnline()) {
      return;
    }

    this.notifier.sendTimeout(player, seconds);
  }

  private Task scheduleTimeout(
      @NonNull Player player, @NonNull Duration timeout, @NonNull Runnable onTimeout) {
    var disabled = timeout.isZero() || timeout.isNegative();
    if (disabled) {
      return Task.noop();
    }

    return this.scheduler.runOnEntityLater(player, onTimeout, timeout);
  }
}
