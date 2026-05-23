package com.hanielcota.essentials.modules.clearchat.service;

import com.hanielcota.essentials.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public final class ClearChatService {

  // A single space, not Component.empty(): a truly empty message is not reliably
  // rendered as a chat line by the client, which would leave the chat un-flushed.
  private static final Component BLANK_LINE = Component.space();

  public void clearChat(int lines, String announcement) {
    var message = ComponentUtils.mini(announcement);

    for (var player : Bukkit.getOnlinePlayers()) {
      for (var i = 0; i < lines; i++) {
        player.sendMessage(BLANK_LINE);
      }
      player.sendMessage(message);
    }
  }
}
