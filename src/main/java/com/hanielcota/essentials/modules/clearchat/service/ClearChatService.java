package com.hanielcota.essentials.modules.clearchat.service;

import com.hanielcota.essentials.util.ComponentUtils;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public final class ClearChatService {

  // A single space, not Component.empty(): a truly empty message is not reliably
  // rendered as a chat line by the client, which would leave the chat un-flushed.
  private static final Component BLANK_LINE = Component.space();

  public void clearChat(int lines, @NonNull String announcement) {
    var message = ComponentUtils.mini(announcement);
    // Build the flush block as a single Component so each player receives one chat
    // packet instead of `lines + 1`. With the configured cap of 300 lines, one
    // packet per player avoids brushing Paper's packet-spam threshold.
    var flush = Component.text();
    for (var i = 0; i < lines; i++) {
      flush.append(BLANK_LINE).append(Component.newline());
    }
    var bundle = flush.append(message).build();

    for (var player : Bukkit.getOnlinePlayers()) {
      player.sendMessage(bundle);
    }
  }
}
