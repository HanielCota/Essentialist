package com.hanielcota.essentials.modules.chat.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

/**
 * UX side of the local channel: warns the sender when no other player is in earshot. Extracted from
 * {@code LocalChannel} so the channel keeps a pure audience-policy surface — symmetric with global
 * and staff channels.
 */
@RequiredArgsConstructor
public final class LocalChannelNotifier {

  private static final MiniMessage MINI = MiniMessage.miniMessage();

  private final @NonNull ConfigHandle<ChatConfig> config;

  public void warnNoListeners(@NonNull Player sender) {
    var snap = this.config.value();
    var warning = snap.local().noListenerWarning();
    if (warning.isEmpty()) {
      return;
    }
    var component = MINI.deserialize(warning);
    sender.sendMessage(component);
  }
}
