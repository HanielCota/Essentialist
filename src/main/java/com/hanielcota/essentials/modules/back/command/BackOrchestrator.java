package com.hanielcota.essentials.modules.back.command;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.back.menu.BackMenu;
import com.hanielcota.essentials.modules.back.service.BackCooldownService;
import com.hanielcota.essentials.modules.back.service.BackPrefetch;
import com.hanielcota.essentials.modules.back.service.BackStaffViewState;
import com.hanielcota.essentials.modules.back.service.BackWarmupService;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.time.Duration;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class BackOrchestrator {

  private final @NonNull ConfigHandle<BackConfig> config;
  private final @NonNull TeleportHistory history;
  private final @NonNull MenuService menus;
  private final @NonNull BackPrefetch prefetch;
  private final @NonNull BackCooldownService cooldown;
  private final @NonNull BackWarmupService warmup;
  private final @NonNull MainThreadCallbacks callbacks;
  private final @NonNull BackStaffViewState staffViewState;

  public CommandResult openBack(@NonNull CommandActor actor) {
    var player = actor.unwrap(Player.class);
    var playerId = player.getUniqueId();

    var entries = this.history.list(playerId);
    var snap = this.config.value();

    var shouldDirectTeleport = snap.directTeleport() && entries.size() == 1;
    if (shouldDirectTeleport) {
      return directTeleport(player, entries.getFirst(), playerId, snap);
    }

    this.prefetch.prefetch(playerId, entries);
    MenuOpenings.open(this.menus, player, BackMenu.ID, actor);

    return CommandResult.success();
  }

  public CommandResult viewBack(@NonNull CommandActor actor, @NonNull Player target) {
    var player = actor.unwrap(Player.class);
    var playerId = player.getUniqueId();
    var targetId = target.getUniqueId();

    var entries = this.history.list(targetId);
    if (entries.isEmpty()) {
      var snap = this.config.value();
      var noBackMsg = snap.noBack();
      return CommandResult.invalidUsage(noBackMsg);
    }

    var targetName = target.getName();
    this.staffViewState.startView(playerId, targetId, targetName);
    this.prefetch.prefetch(playerId, entries);
    MenuOpenings.open(this.menus, player, BackMenu.ID, actor);

    return CommandResult.success();
  }

  public CommandResult forceBack(@NonNull CommandActor actor) {
    var player = actor.unwrap(Player.class);
    var playerId = player.getUniqueId();

    var entries = this.history.list(playerId);
    if (entries.isEmpty()) {
      var snap = this.config.value();
      var noBackMsg = snap.noBack();
      return CommandResult.invalidUsage(noBackMsg);
    }

    var snap = this.config.value();
    var entry = entries.getFirst();

    return directTeleport(player, entry, playerId, snap);
  }

  public CommandResult clearBack(@NonNull CommandActor actor, @NonNull Player subject) {
    var subjectId = subject.getUniqueId();

    var entries = this.history.list(subjectId);
    for (var entry : entries) {
      this.history.remove(subjectId, entry.id());
    }

    return CommandResult.success();
  }

  public void processTeleport(
      @NonNull ClickContext click, @NonNull HistoryEntry entry, @NonNull UUID historyOwner) {
    var player = click.player();
    var snap = this.config.value();
    var playerId = player.getUniqueId();

    var remaining = this.cooldown.remainingSeconds(playerId, snap.cooldownSeconds());
    if (remaining > 0) {
      var msg = snap.formatCooldownMessage((int) remaining);
      click.reply(msg);
      return;
    }

    var target = entry.location();
    var world = target.getWorld();

    if (world == null) {
      this.history.remove(historyOwner, entry.id());
      var noBackMsg = snap.noBack();
      click.reply(noBackMsg);
      return;
    }

    var worldName = world.getName();
    var x = target.getX();
    var y = target.getY();
    var z = target.getZ();

    Consumer<Boolean> onArrival =
        success -> {
          if (!Boolean.TRUE.equals(success)) {
            var noBackMsg = snap.noBack();
            click.reply(noBackMsg);
            return;
          }

          this.history.remove(historyOwner, entry.id());
          this.cooldown.touch(playerId);

          var successMessage = snap.formatBack(worldName, x, y, z);
          click.reply(successMessage);
          playTeleportSound(player, snap);
        };

    var warmupSecs = snap.warmupSeconds();
    if (warmupSecs > 0) {
      scheduleWarmup(player, warmupSecs, target, snap, onArrival, click::reply);
      return;
    }

    executeAsyncTeleport(player, target, onArrival);
  }

  private CommandResult directTeleport(
      @NonNull Player player,
      @NonNull HistoryEntry entry,
      @NonNull UUID historyOwner,
      @NonNull BackConfig snap) {
    var playerId = player.getUniqueId();

    var remaining = this.cooldown.remainingSeconds(playerId, snap.cooldownSeconds());
    if (remaining > 0) {
      var msg = snap.formatCooldownMessage((int) remaining);
      return CommandResult.invalidUsage(msg);
    }

    var target = entry.location();
    var world = target.getWorld();

    if (world == null) {
      this.history.remove(historyOwner, entry.id());
      var noBackMsg = snap.noBack();
      return CommandResult.invalidUsage(noBackMsg);
    }

    var worldName = world.getName();
    var x = target.getX();
    var y = target.getY();
    var z = target.getZ();

    Consumer<Boolean> onArrival =
        success -> {
          if (!Boolean.TRUE.equals(success)) {
            var noBackMsg = snap.noBack();
            player.sendRichMessage(noBackMsg);
            return;
          }

          this.history.remove(historyOwner, entry.id());
          this.cooldown.touch(playerId);
          var successMessage = snap.formatBack(worldName, x, y, z);
          player.sendRichMessage(successMessage);
          playTeleportSound(player, snap);
        };

    var warmupSecs = snap.warmupSeconds();
    if (warmupSecs > 0) {
      scheduleWarmup(player, warmupSecs, target, snap, onArrival, player::sendRichMessage);
      return CommandResult.success();
    }

    executeAsyncTeleport(player, target, onArrival);

    return CommandResult.success();
  }

  private void scheduleWarmup(
      @NonNull Player player,
      int seconds,
      @NonNull Location target,
      @NonNull BackConfig snap,
      @NonNull Consumer<Boolean> onArrival,
      @NonNull Consumer<String> reply) {
    var secondsText = Integer.toString(seconds);
    var warmupMsg = snap.warmupMessage().replace("{seconds}", secondsText);
    reply.accept(warmupMsg);

    Runnable onComplete = () -> executeAsyncTeleport(player, target, onArrival);

    Runnable onCancel =
        () -> {
          var cancelMsg = snap.warmupCancelled();
          reply.accept(cancelMsg);
        };

    var delay = Duration.ofSeconds(seconds);
    this.warmup.schedule(player, delay, onComplete, onCancel);
  }

  private void executeAsyncTeleport(
      @NonNull Player player, @NonNull Location target, @NonNull Consumer<Boolean> onArrival) {
    var teleportFuture = player.teleportAsync(target);
    this.callbacks.hop(teleportFuture, onArrival, "back teleport");
  }

  private void playTeleportSound(@NonNull Player player, @NonNull BackConfig snap) {
    var soundKey = snap.teleportSound();
    if (soundKey.isEmpty()) {
      return;
    }

    var location = player.getLocation();
    var volume = snap.teleportVolume();
    var pitch = snap.teleportPitch();

    player.playSound(location, soundKey, volume, pitch);
  }
}
