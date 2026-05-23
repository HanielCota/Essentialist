package com.hanielcota.essentials.modules.near.config;

import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record NearConfig(
    @Comment("Default search radius (blocks) when /near is used without an argument.")
        int defaultRadius,
    @Comment("Largest radius accepted by /near.") int maxRadius,
    @Comment("Result line. Placeholders: {radius}, {count}, {players}.") String found,
    @Comment("One nearby player. Placeholders: {player}, {distance}.") String entry,
    @Comment("Text placed between entries.") String separator,
    @Comment("Shown when nobody is nearby. Placeholder: {radius}.") String none,
    @Comment("Shown when the radius is out of range. Placeholder: {max}.") String invalidRadius) {

  public static NearConfig defaults() {
    return new NearConfig(
        100,
        500,
        "<yellow>Jogadores num raio de <gold>{radius}</gold>m <gray>(<gold>{count}</gold>)<gray>:"
            + " {players}",
        "<white>{player} <gray>({distance}m)",
        "<gray>, ",
        "<yellow>Nenhum jogador num raio de <gold>{radius}</gold> blocos.",
        "<red>O raio precisa estar entre <gold>1</gold> e <gold>{max}</gold>.");
  }

  public String formatEntry(@NonNull String player, int distance) {
    var distanceStr = Integer.toString(distance);

    return entry.replace("{player}", player).replace("{distance}", distanceStr);
  }

  public String formatFound(int radius, int count, @NonNull String players) {
    var radiusStr = Integer.toString(radius);
    var countStr = Integer.toString(count);

    return found.replace("{radius}", radiusStr).replace("{count}", countStr).replace("{players}", players);
  }

  public String formatNone(int radius) {
    var radiusStr = Integer.toString(radius);

    return none.replace("{radius}", radiusStr);
  }

  public String formatInvalidRadius() {
    var maxStr = Integer.toString(maxRadius);

    return invalidRadius.replace("{max}", maxStr);
  }
}
