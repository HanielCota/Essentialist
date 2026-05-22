package com.hanielcota.essentials.modules.enderchest.config;

import java.util.Objects;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record EnderChestConfig(
    @Comment("Shown when you open your own ender chest.") String opened,
    @Comment("Shown when you open another player's ender chest. Placeholder: {player}.")
        String openedOther,
    @Comment("Shown when you lack permission to open another player's ender chest.")
        String noPermissionOther) {

  public static EnderChestConfig defaults() {
    return new EnderChestConfig(
        "<green>Abrindo o seu Ender Chest.",
        "<green>Abrindo o Ender Chest de <gold>{player}</gold>.",
        "<red>Você não pode abrir o Ender Chest de outros jogadores.");
  }

  public String formatOpenedOther(String player) {
    Objects.requireNonNull(player, "player");
    return openedOther.replace("{player}", player);
  }
}
