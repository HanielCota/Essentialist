package com.hanielcota.essentials.modules.homes.rename;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.HomesMessages;
import com.hanielcota.essentials.modules.homes.menu.HomeRenamePrompter;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.homes.service.HomeService.RenameResult;
import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.util.ComponentUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Drives the chat-driven rename flow: opens a prompt with a timeout, captures the player's next
 * chat message, and applies the rename via {@link HomeService}. Delegates session storage to {@link
 * HomeRenameSessions} and name validation to {@link HomeNameValidator}; the orchestrator itself
 * only sequences these collaborators and emits the user-visible chat lines.
 */
@RequiredArgsConstructor
public final class HomeRenameOrchestrator implements HomeRenamePrompter, Listener {

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final Scheduler scheduler;
  private final HomeRenameSessions sessions;
  private final HomeNameValidator validator;

  @Override
  public void prompt(Player player, String homeName) {
    var snap = config.value();
    var timeout = snap.renameTimeout();
    var seconds = timeout.toSeconds();
    var timeoutTask = scheduler.runLater(() -> onTimeout(player, seconds), timeout);

    sessions.start(player.getUniqueId(), homeName, timeoutTask);
    player.sendMessage(ComponentUtils.mini(formatPrompt(snap.messages(), homeName, seconds)));
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onChat(AsyncChatEvent event) {
    var player = event.getPlayer();
    var session = sessions.consume(player.getUniqueId()).orElse(null);
    if (session == null) return;

    session.timeoutTask().cancel();
    event.setCancelled(true);

    var input = PlainTextComponentSerializer.plainText().serialize(event.message()).strip();
    handleInput(player, session.homeName(), input);
  }

  private void handleInput(Player player, String oldName, String input) {
    var messages = config.value().messages();

    if (isCancel(input)) {
      player.sendMessage(ComponentUtils.mini(messages.renameCancelled()));
      return;
    }
    if (!validator.isValid(input)) {
      player.sendMessage(ComponentUtils.mini(messages.renameInvalid()));
      return;
    }

    var result = service.rename(player.getUniqueId(), oldName, input);
    player.sendMessage(ComponentUtils.mini(formatResult(messages, oldName, input, result)));
  }

  private void onTimeout(Player player, long seconds) {
    if (sessions.consume(player.getUniqueId()).isEmpty() || !player.isOnline()) return;

    var line =
        config.value().messages().renameTimeout().replace("{seconds}", Long.toString(seconds));
    player.sendMessage(ComponentUtils.mini(line));
  }

  private static boolean isCancel(String input) {
    return input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("cancelar");
  }

  private static String formatPrompt(HomesMessages messages, String homeName, long seconds) {
    return messages
        .renamePrompt()
        .replace("{name}", homeName)
        .replace("{seconds}", Long.toString(seconds));
  }

  private static String formatResult(
      HomesMessages messages, String oldName, String newName, RenameResult result) {
    return switch (result) {
      case RENAMED -> messages.renamed().replace("{old}", oldName).replace("{new}", newName);
      case NOT_FOUND -> messages.renameLost().replace("{name}", oldName);
      case NAME_TAKEN -> messages.renameTaken().replace("{name}", newName);
      default -> throw new IllegalStateException("Unexpected rename result: " + result);
    };
  }
}
