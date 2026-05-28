package com.hanielcota.essentials.modules.speed.config;

import com.hanielcota.essentials.config.MessagePair;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record SpeedConfig(
    @Comment("Lowest value accepted by /speed walk|fly.") int minSpeed,
    @Comment(
            "Highest value accepted by /speed walk|fly. The value maps linearly so this max becomes"
                + " full speed.")
        int maxSpeed,
    @Comment("Walk speed restored by /speed reset (vanilla default is 0.2).") float resetWalkSpeed,
    @Comment("Fly speed restored by /speed reset (vanilla default is 0.1).") float resetFlySpeed,
    @Comment("Shown when walk speed is set. Placeholder: {valor}.") String walkSet,
    @Comment("Placeholders: {player}, {valor}.") String walkSetOther,
    @Comment("Shown when fly speed is set. Placeholder: {valor}.") String flySet,
    @Comment("Placeholders: {player}, {valor}.") String flySetOther,
    @Comment("Shown when walk and fly speed are restored to the defaults.") String reset,
    @Comment("Placeholders: {player}.") String resetOther,
    @Comment(
            "Shown when the speed value is outside the configured range. Placeholders: {min},"
                + " {max}.")
        String invalid,
    @Comment("Shown when /speed is used without a subcommand. Placeholders: {min}, {max}.")
        String usage) {

  public static SpeedConfig defaults() {
    return new SpeedConfig(
        1,
        10,
        0.2f,
        0.1f,
        "<green>Velocidade de caminhada definida para <gold>{valor}</gold>.",
        "<green>Velocidade de caminhada de <gold>{player}</gold> definida para"
            + " <gold>{valor}</gold>.",
        "<green>Velocidade de voo definida para <gold>{valor}</gold>.",
        "<green>Velocidade de voo de <gold>{player}</gold> definida para <gold>{valor}</gold>.",
        "<green>Velocidades de caminhada e voo restauradas para o padrão.",
        "<green>Velocidades de <gold>{player}</gold> restauradas para o padrão.",
        "<red>A velocidade precisa estar entre {min} e {max}.",
        "<yellow>Use <gray>/speed walk</gray> ou <gray>/speed fly</gray> com um valor de {min} a"
            + " {max}, ou <gray>/speed reset</gray> para restaurar o padrão.</yellow>");
  }

  private static MessagePair resolve(@NonNull String self, @NonNull String other, int valor) {
    var formatted = Integer.toString(valor);
    var selfFormatted = self.replace("{valor}", formatted);
    var otherFormatted = other.replace("{valor}", formatted);
    return new MessagePair(selfFormatted, otherFormatted);
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

  public String formatInvalid() {
    var min = Integer.toString(minSpeed);
    var max = Integer.toString(maxSpeed);

    return invalid.replace("{min}", min).replace("{max}", max);
  }

  public String formatUsage() {
    var min = Integer.toString(minSpeed);
    var max = Integer.toString(maxSpeed);

    return usage.replace("{min}", min).replace("{max}", max);
  }
}
