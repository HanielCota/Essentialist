package com.hanielcota.essentials.modules.teleport.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.teleport.config.TeleportConfig;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Routes a {@code /tp} invocation to the matching teleport flow based on the number and shape of
 * the raw arguments: one token to a player, two tokens to a player-to-player move, three numeric
 * tokens to coordinates. Owns the error paths the framework would normally provide via
 * {@code @OnlinePlayer} / {@code @PlayerOnly} / method-level {@code @Permission}.
 */
@RequiredArgsConstructor
public final class TeleportDispatcher {

  private static final String OTHERS_PERMISSION = "essentials.tp.others";

  private final ConfigHandle<TeleportConfig> config;
  private final PlayerProvider players;
  private final TeleportNotifier notifier;
  private final TeleportService teleport;
  private final MainThreadCallbacks callbacks;

  private static Double parseDouble(@NonNull String value) {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException _) {
      return null;
    }
  }

  public void dispatch(
      @NonNull CommandActor sender,
      @NonNull String arg1,
      @NonNull String arg2,
      @NonNull String arg3) {

    var hasArg3 = !arg3.isEmpty();
    if (hasArg3) {
      dispatchToPosition(sender, arg1, arg2, arg3);
      return;
    }

    var hasArg2 = !arg2.isEmpty();
    if (hasArg2) {
      dispatchMove(sender, arg1, arg2);
      return;
    }

    dispatchToPlayer(sender, arg1);
  }

  private void dispatchToPlayer(@NonNull CommandActor sender, @NonNull String targetName) {
    if (!sender.isPlayer()) {
      var snap = this.config.value();
      var msg = snap.mustBePlayer();
      sender.sendError(msg);
      return;
    }

    var resolved = this.players.online(targetName);
    if (resolved.isEmpty()) {
      sendPlayerNotFound(sender, targetName);
      return;
    }

    var senderPlayer = sender.unwrap(Player.class);
    var target = resolved.get();
    var senderName = senderPlayer.getName();
    var actualTargetName = target.getName();

    var future = this.teleport.toPlayer(senderPlayer, target);
    this.callbacks.hop(
        future,
        outcome ->
            this.notifier.notifyToPlayer(sender, target, senderName, actualTargetName, outcome),
        "tp to player");
  }

  private void dispatchMove(
      @NonNull CommandActor sender, @NonNull String fromName, @NonNull String toName) {
    if (!sender.hasPermission(OTHERS_PERMISSION)) {
      var snap = this.config.value();
      var msg = snap.noPermissionOthers();
      sender.sendError(msg);
      return;
    }

    var fromResolved = this.players.online(fromName);
    if (fromResolved.isEmpty()) {
      sendPlayerNotFound(sender, fromName);
      return;
    }

    var toResolved = this.players.online(toName);
    if (toResolved.isEmpty()) {
      sendPlayerNotFound(sender, toName);
      return;
    }

    var from = fromResolved.get();
    var to = toResolved.get();
    var actualFromName = from.getName();
    var actualToName = to.getName();
    var senderName = sender.name();
    var selfMove = Senders.isSelf(sender, from);

    var future = this.teleport.movePlayer(from, to);
    this.callbacks.hop(
        future,
        outcome ->
            this.notifier.notifyMove(
                sender, from, actualFromName, actualToName, senderName, selfMove, outcome),
        "tp move");
  }

  private void dispatchToPosition(
      @NonNull CommandActor sender,
      @NonNull String xRaw,
      @NonNull String yRaw,
      @NonNull String zRaw) {
    if (!sender.isPlayer()) {
      var snap = this.config.value();
      var msg = snap.mustBePlayer();
      sender.sendError(msg);
      return;
    }

    var x = parseDouble(xRaw);
    var y = parseDouble(yRaw);
    var z = parseDouble(zRaw);
    if (x == null || y == null || z == null) {
      var snap = this.config.value();
      var msg = snap.invalidPosition();
      sender.sendError(msg);
      return;
    }

    var senderPlayer = sender.unwrap(Player.class);
    var future = this.teleport.toPosition(senderPlayer, x, y, z);
    this.callbacks.hop(
        future, outcome -> this.notifier.notifyToPos(sender, x, y, z, outcome), "tp to position");
  }

  private void sendPlayerNotFound(@NonNull CommandActor sender, @NonNull String name) {
    var snap = this.config.value();
    var msg = snap.formatPlayerNotFound(name);
    sender.sendError(msg);
  }
}
