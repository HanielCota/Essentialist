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
    @Comment("Shown when walk and fly speed are restored to the defaults.") String reset,
    @Comment("Placeholders: {player}.") String resetOther,
    @Comment("Shown when the speed value is outside the 1-10 range.") String invalid,
    @Comment("Shown when /speed is used without a subcommand.") String usage) {

  public static SpeedConfig defaults() {
    return new SpeedConfig(
        "<green>Velocidade de caminhada definida para <gold>{valor}</gold>.",
        "<green>Velocidade de caminhada de <gold>{player}</gold> definida para"
            + " <gold>{valor}</gold>.",
        "<green>Velocidade de voo definida para <gold>{valor}</gold>.",
        "<green>Velocidade de voo de <gold>{player}</gold> definida para <gold>{valor}</gold>.",
        "<green>Velocidades de caminhada e voo restauradas para o padrão.",
        "<green>Velocidades de <gold>{player}</gold> restauradas para o padrão.",
        "<red>A velocidade precisa estar entre 1 e 10.",
        "<yellow>Use <gray>/speed walk</gray> ou <gray>/speed fly</gray> com um valor de 1 a 10,"
            + " ou <gray>/speed reset</gray> para restaurar o padrão.</yellow>");
  }

  private static MessagePair resolve(String self, String other, int valor) {
    String formatted = Integer.toString(valor);
    return new MessagePair(self.replace("{valor}", formatted), other.replace("{valor}", formatted));
  }

  public MessagePair whenWalkSet(int valor) {
    return resolve(walkSet, walkSetOther, valor);
  }

  public MessagePair whenFlySet(int valor) {
    return resolve(flySet, flySetOther, valor);
  }

  public MessagePair whenReset() {
    return new MessagePair(reset, resetOther);
  }
}
