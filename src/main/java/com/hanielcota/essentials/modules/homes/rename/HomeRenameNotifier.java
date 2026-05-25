package com.hanielcota.essentials.modules.homes.rename;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.service.HomeService.RenameResult;
import com.hanielcota.essentials.util.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Owns every chat line emitted by the rename flow: prompt, cancelled, invalid name, result and
 * timeout. Keeps {@link HomeRenameOrchestrator} free of {@code player.sendMessage(...)} calls so
 * the orchestrator only sequences the validator → service → notifier chain.
 */
@RequiredArgsConstructor
public final class HomeRenameNotifier {

  private final ConfigHandle<HomesConfig> config;

  public void sendPrompt(@NonNull Player player, @NonNull String homeName, long seconds) {
    var messages = this.config.value().messages();
    var promptMsg = HomeRenameMessages.prompt(messages, homeName, seconds);
    var component = ComponentUtils.mini(promptMsg);

    player.sendMessage(component);
  }

  public void sendCancelled(@NonNull Player player) {
    var messages = this.config.value().messages();
    var cancelledText = messages.renameCancelled();
    var component = ComponentUtils.mini(cancelledText);

    player.sendMessage(component);
  }

  public void sendInvalid(@NonNull Player player) {
    var messages = this.config.value().messages();
    var invalidText = messages.invalidName();
    var component = ComponentUtils.mini(invalidText);

    player.sendMessage(component);
  }

  public void sendResult(
      @NonNull Player player,
      @NonNull String oldName,
      @NonNull String newName,
      @NonNull RenameResult result) {
    var messages = this.config.value().messages();
    var resultMsg = HomeRenameMessages.result(messages, oldName, newName, result);
    var component = ComponentUtils.mini(resultMsg);

    player.sendMessage(component);
  }

  public void sendTimeout(@NonNull Player player, long seconds) {
    var messages = this.config.value().messages();
    var timeoutMsg = HomeRenameMessages.timeout(messages, seconds);
    var component = ComponentUtils.mini(timeoutMsg);

    player.sendMessage(component);
  }
}
