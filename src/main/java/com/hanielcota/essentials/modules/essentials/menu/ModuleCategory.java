package com.hanielcota.essentials.modules.essentials.menu;

import lombok.NonNull;
import org.bukkit.Material;

/** Tabs that group the modules in the admin menu. Each carries its tab label and icon. */
public enum ModuleCategory {
  PROTECTION("<red>Proteção", Material.SHIELD),
  TELEPORT("<aqua>Teleporte", Material.ENDER_PEARL),
  CHAT("<yellow>Chat", Material.PAPER),
  ITEMS("<gold>Itens", Material.CHEST),
  PLAYER("<green>Jogador", Material.PLAYER_HEAD),
  ADMIN("<light_purple>Admin", Material.COMMAND_BLOCK),
  OTHER("<gray>Outros", Material.BARREL);

  private final String label;
  private final Material icon;

  ModuleCategory(@NonNull String label, @NonNull Material icon) {
    this.label = label;
    this.icon = icon;
  }

  public @NonNull String label() {
    return this.label;
  }

  public @NonNull Material icon() {
    return this.icon;
  }
}
