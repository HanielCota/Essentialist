package com.hanielcota.essentials.modules.homes.rename;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.menu.HomeRenamePrompter;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.scheduler.Task;
import com.hanielcota.essentials.util.ComponentUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Drives the chat-driven rename flow: holds the pending rename per player, sends the prompt, and
 * consumes their next chat message (or the timeout) to finish the rename.
 *
 * <p>Implements {@link HomeRenamePrompter} so the /homes menu click handler can request a rename
 * without depending on chat-listener internals.
 */
@RequiredArgsConstructor
public final class HomeRenameOrchestrator implements HomeRenamePrompter, Listener {

  private static final int MAX_NAME_LENGTH = 32;

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final Scheduler scheduler;

  private final ConcurrentHashMap<UUID, Pending> pending = new ConcurrentHashMap<>();

  @Override
  public void prompt(Player player, String homeName) {
    var snap = config.value();
    var timeout = snap.renameTimeout();
    var seconds = timeout.toSeconds();

    cancelPrior(player.getUniqueId());

    var timeoutTask =
        scheduler.runLater(
            () -> {
              var expired = pending.remove(player.getUniqueId());
              if (expired == null || !player.isOnline()) return;
              player.sendMessage(
                  ComponentUtils.mini(
                      snap.messages()
                          .renameTimeout()
                          .replace("{seconds}", Long.toString(seconds))));
            },
            timeout);

    pending.put(player.getUniqueId(), new Pending(homeName, timeoutTask));
    player.sendMessage(
        ComponentUtils.mini(
            snap.messages()
                .renamePrompt()
                .replace("{name}", homeName)
                .replace("{seconds}", Long.toString(seconds))));
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onChat(AsyncChatEvent event) {
    var player = event.getPlayer();
    var session = pending.remove(player.getUniqueId());
    if (session == null) return;

    session.timeoutTask().cancel();
    event.setCancelled(true);

    var input = PlainTextComponentSerializer.plainText().serialize(event.message()).strip();
    var messages = config.value().messages();

    if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("cancelar")) {
      player.sendMessage(ComponentUtils.mini(messages.renameCancelled()));
      return;
    }
    if (!isValidName(input)) {
      player.sendMessage(ComponentUtils.mini(messages.renameInvalid()));
      return;
    }

    var result = service.rename(player.getUniqueId(), session.homeName(), input);
    switch (result) {
      case RENAMED ->
          player.sendMessage(
              ComponentUtils.mini(
                  messages.renamed().replace("{old}", session.homeName()).replace("{new}", input)));
      case NOT_FOUND ->
          player.sendMessage(
              ComponentUtils.mini(messages.renameLost().replace("{name}", session.homeName())));
      case NAME_TAKEN ->
          player.sendMessage(ComponentUtils.mini(messages.renameTaken().replace("{name}", input)));
      default -> throw new IllegalStateException("Unexpected rename result: " + result);
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    cancelPrior(event.getPlayer().getUniqueId());
  }

  private void cancelPrior(UUID player) {
    var prior = pending.remove(player);
    if (prior != null) prior.timeoutTask().cancel();
  }

  private static boolean isValidName(String name) {
    return !name.isEmpty() && name.length() <= MAX_NAME_LENGTH && !name.contains(" ");
  }

  private record Pending(String homeName, Task timeoutTask) {}
}
