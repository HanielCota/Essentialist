package com.hanielcota.essentials.modules.speed.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record SpeedConfig(
    @Comment("Shown when walk speed is set. Placeholder: {valor}.") String walkSet,
    @Comment("Placeholders: {player}, {valor}.") String walkSetOther,
    @Comment("Shown when fly speed is set. Placeholder: {valor}.") String flySet,
    @Comment("Placeholders: {player}, {valor}.") String flySetOther,
    @Comment("Shown when the speed value is outside the 1-10 range.") String invalid) {

  public static SpeedConfig defaults() {
    return new SpeedConfig(
        "<green>Velocidade de caminhada definida para <gold>{valor}</gold>.",
        "<green>Velocidade de caminhada de <gold>{player}</gold> definida para"
            + " <gold>{valor}</gold>.",
        "<green>Velocidade de voo definida para <gold>{valor}</gold>.",
        "<green>Velocidade de voo de <gold>{player}</gold> definida para <gold>{valor}</gold>.",
        "<red>A velocidade precisa estar entre 1 e 10.");
  }

  public MessagePair whenWalkSet() {
    return new MessagePair(walkSet, walkSetOther);
  }

  public MessagePair whenFlySet() {
    return new MessagePair(flySet, flySetOther);
  }
}
