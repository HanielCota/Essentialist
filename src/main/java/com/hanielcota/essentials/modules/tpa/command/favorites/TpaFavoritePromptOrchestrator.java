package com.hanielcota.essentials.modules.tpa.command.favorites;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.service.favorites.TpaFavoriteService;
import com.hanielcota.essentials.modules.tpa.service.favorites.TpaFavoriteSessions;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.scheduler.Task;
import java.time.Duration;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Drives the chat-driven favorite-add flow: opens a prompt, captures the player's next chat message
 * and adds the favorite via {@link TpaFavoriteService}.
 *
 * <p>Session storage is delegated to {@link TpaFavoriteSessions}, the chat-message validation lives
 * here as a regex and chat messaging is delegated to {@link TpaFavoriteNotifier}. Same shape as the
 * homes rename orchestrator.
 */
@RequiredArgsConstructor
public final class TpaFavoritePromptOrchestrator {

  private static final Pattern VALID_NICK = Pattern.compile("[A-Za-z0-9_]{3,16}");

  private final ConfigHandle<TpaConfig> config;
  private final TpaFavoriteService favorites;
  private final TpaFavoriteSessions sessions;
  private final TpaFavoriteNotifier notifier;
  private final PlayerProvider players;
  private final Scheduler scheduler;
  private final TpaFavoriteAddNotifier addNotifier;

  private static boolean isCancel(@NonNull String input) {
    return input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("cancelar");
  }

  private static boolean isSameAs(@NonNull Player player, @NonNull OfflinePlayer target) {
    var playerId = player.getUniqueId();
    var targetId = target.getUniqueId();

    return playerId.equals(targetId);
  }

  private static String nameOf(@NonNull OfflinePlayer offline, @NonNull String fallback) {
    var name = offline.getName();
    if (name == null || name.isBlank()) {
      return fallback;
    }
    return name;
  }

  public void prompt(@NonNull Player player) {
    var snap = this.config.value();
    var timeout = snap.favoritePromptTimeout();
    var seconds = timeout.toSeconds();
    var uuid = player.getUniqueId();

    Runnable onTimeout = () -> handleTimeout(player);
    var timeoutTask = scheduleTimeout(player, timeout, onTimeout);

    this.sessions.start(uuid, timeoutTask);
    this.notifier.sendPrompt(player, seconds);
  }

  public void handleInput(@NonNull Player player, @NonNull String input) {
    if (isCancel(input)) {
      this.notifier.sendCancelled(player);
      return;
    }

    if (!VALID_NICK.matcher(input).matches()) {
      this.notifier.sendInvalidName(player);
      return;
    }

    var resolved = this.players.offlineByName(input);
    if (resolved.isEmpty()) {
      this.notifier.sendUnknownPlayer(player, input);
      return;
    }

    var target = resolved.get();
    if (isSameAs(player, target)) {
      this.notifier.sendSelf(player);
      return;
    }

    var targetId = target.getUniqueId();
    var targetName = nameOf(target, input);
    var ownerId = player.getUniqueId();
    var added = this.favorites.add(ownerId, targetId, targetName);

    if (!added) {
      this.notifier.sendAlready(player, targetName);
      return;
    }
    this.notifier.sendAdded(player, targetName);
    this.addNotifier.notify(player.getName(), targetId);
  }

  public void handleTimeout(@NonNull Player player) {
    var uuid = player.getUniqueId();
    var session = this.sessions.consume(uuid);

    if (session == null || !player.isOnline()) {
      return;
    }

    this.notifier.sendTimeout(player);
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
