package com.hanielcota.essentials.modules.homes.create;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.shared.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Owns every chat line emitted by the create-home flow: prompt, cancelled, invalid name, created,
 * already-exists, limit-reached and timeout. Keeps {@link HomeCreateOrchestrator} free of {@code
 * player.sendMessage(...)} calls.
 */
@RequiredArgsConstructor
public final class HomeCreateNotifier {

  private final ConfigHandle<HomesConfig> config;

  private static void sendMini(@NonNull Player player, @NonNull String raw) {
    var component = ComponentUtils.mini(raw);

    player.sendMessage(component);
  }

  public void sendPrompt(@NonNull Player player, long seconds) {
    var messages = this.config.value().messages();
    var template = messages.createPrompt();
    var secondsStr = Long.toString(seconds);
    var promptMsg = template.replace("{seconds}", secondsStr);

    sendMini(player, promptMsg);
  }

  public void sendCancelled(@NonNull Player player) {
    var messages = this.config.value().messages();
    var cancelledText = messages.createCancelled();

    sendMini(player, cancelledText);
  }

  public void sendInvalid(@NonNull Player player) {
    var messages = this.config.value().messages();
    var invalidText = messages.invalidName();

    sendMini(player, invalidText);
  }

  public void sendCreated(@NonNull Player player, @NonNull String name) {
    var messages = this.config.value().messages();
    var template = messages.homeSet();
    var msg = template.replace("{name}", name);

    sendMini(player, msg);
  }

  public void sendAlreadyExists(@NonNull Player player, @NonNull String name) {
    var messages = this.config.value().messages();
    var template = messages.createAlreadyExists();
    var msg = template.replace("{name}", name);

    sendMini(player, msg);
  }

  public void sendLimitReached(@NonNull Player player, @NonNull String message) {
    sendMini(player, message);
  }

  public void sendTimeout(@NonNull Player player, long seconds) {
    var messages = this.config.value().messages();
    var template = messages.createTimeout();
    var secondsStr = Long.toString(seconds);
    var msg = template.replace("{seconds}", secondsStr);

    sendMini(player, msg);
  }
}
