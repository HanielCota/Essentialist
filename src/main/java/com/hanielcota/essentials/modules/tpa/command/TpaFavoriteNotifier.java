package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.util.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Owns every chat line the favorite-add prompt flow can send. Keeps {@code
 * TpaFavoritePromptOrchestrator} free of {@code player.sendMessage(...)} calls so the orchestrator
 * only sequences validator → service → notifier.
 */
@RequiredArgsConstructor
public final class TpaFavoriteNotifier {

  private final ConfigHandle<TpaConfig> config;

  public void sendPrompt(@NonNull Player player, long seconds) {
    var messages = this.config.value().messages();
    var template = messages.favoritePrompt();
    var secondsText = Long.toString(seconds);
    var promptText = template.replace("{seconds}", secondsText);

    sendMini(player, promptText);
  }

  public void sendAdded(@NonNull Player player, @NonNull String favoriteName) {
    var messages = this.config.value().messages();
    var template = messages.favoriteAdded();
    var addedText = template.replace("{player}", favoriteName);

    sendMini(player, addedText);
  }

  public void sendAlready(@NonNull Player player, @NonNull String favoriteName) {
    var messages = this.config.value().messages();
    var template = messages.favoriteAlready();
    var alreadyText = template.replace("{player}", favoriteName);

    sendMini(player, alreadyText);
  }

  public void sendRemoved(@NonNull Player player, @NonNull String favoriteName) {
    var messages = this.config.value().messages();
    var template = messages.favoriteRemoved();
    var removedText = template.replace("{player}", favoriteName);

    sendMini(player, removedText);
  }

  public void sendInvalidName(@NonNull Player player) {
    var messages = this.config.value().messages();
    var invalidText = messages.favoriteInvalidName();

    sendMini(player, invalidText);
  }

  public void sendUnknownPlayer(@NonNull Player player, @NonNull String typedName) {
    var messages = this.config.value().messages();
    var template = messages.favoriteUnknownPlayer();
    var unknownText = template.replace("{player}", typedName);

    sendMini(player, unknownText);
  }

  public void sendSelf(@NonNull Player player) {
    var messages = this.config.value().messages();
    var selfText = messages.favoriteSelf();

    sendMini(player, selfText);
  }

  public void sendCancelled(@NonNull Player player) {
    var messages = this.config.value().messages();
    var cancelledText = messages.favoritePromptCancelled();

    sendMini(player, cancelledText);
  }

  public void sendTimeout(@NonNull Player player) {
    var messages = this.config.value().messages();
    var timeoutText = messages.favoritePromptTimeout();

    sendMini(player, timeoutText);
  }

  private static void sendMini(@NonNull Player player, @NonNull String raw) {
    var component = ComponentUtils.mini(raw);

    player.sendMessage(component);
  }
}
