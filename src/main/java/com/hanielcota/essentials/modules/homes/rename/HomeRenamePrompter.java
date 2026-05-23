package com.hanielcota.essentials.modules.homes.rename;

import org.bukkit.entity.Player;

/**
 * Opens the chat-driven rename flow for a home. Implemented in the rename package to keep the menu
 * click handler free of chat-listener wiring.
 */
@FunctionalInterface
public interface HomeRenamePrompter {

  void prompt(Player player, String homeName);
}
