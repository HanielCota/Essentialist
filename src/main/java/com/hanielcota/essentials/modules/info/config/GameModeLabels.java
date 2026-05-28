package com.hanielcota.essentials.modules.info.config;

import lombok.NonNull;
import org.bukkit.GameMode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record GameModeLabels(
    @Comment("Label shown for GameMode SURVIVAL on the player info card.") String survival,
    @Comment("Label shown for GameMode CREATIVE on the player info card.") String creative,
    @Comment("Label shown for GameMode ADVENTURE on the player info card.") String adventure,
    @Comment("Label shown for GameMode SPECTATOR on the player info card.") String spectator) {

  public static GameModeLabels defaults() {
    return new GameModeLabels("Sobrevivência", "Criativo", "Aventura", "Espectador");
  }

  public String label(@NonNull GameMode mode) {
    return switch (mode) {
      case SURVIVAL -> survival;
      case CREATIVE -> creative;
      case ADVENTURE -> adventure;
      case SPECTATOR -> spectator;
    };
  }
}
