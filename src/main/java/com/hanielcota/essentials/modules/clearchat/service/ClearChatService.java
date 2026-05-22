package com.hanielcota.essentials.modules.clearchat.service;

import com.hanielcota.essentials.util.ComponentUtils;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClearChatService {

  // A single space, not Component.empty(): a truly empty message is not reliably
  // rendered as a chat line by the client, which would leave the chat un-flushed.
  private static final Component BLANK_LINE = Component.space();

  /** Flushes every online player's chat with blank lines, then shows the announcement. */
  public void clearChat(int lines, String announcement) {
    Objects.requireNonNull(announcement, "announcement");

    Component message = ComponentUtils.mini(announcement);
    for (Player player : Bukkit.getOnlinePlayers()) {
      for (int i = 0; i < lines; i++) {
        player.sendMessage(BLANK_LINE);
      }
      player.sendMessage(message);
    }
  }
}
