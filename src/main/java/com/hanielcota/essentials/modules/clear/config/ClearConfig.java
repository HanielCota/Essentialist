package com.hanielcota.essentials.modules.clear.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record ClearConfig(
    @Comment("Shown to the player when their inventory is cleared. Placeholders: {count}.")
        String cleared,
    @Comment(
            "Shown to the executor when clearing another inventory. Placeholders: {player},"
                + " {count}.")
        String clearedOther,
    @Comment("Shown when the player has nothing to clear.") String empty,
    @Comment("Shown to the executor when target has nothing to clear. Placeholders: {player}.")
        String emptyOther) {

  public static ClearConfig defaults() {
    return new ClearConfig(
        "<green>Inventário limpo (<gold>{count}</gold> item(ns) removido(s)).",
        "<green>Limpo o inventário de <gold>{player}</gold> (<gold>{count}</gold> item(ns)).",
        "<red>Seu inventário já está vazio.",
        "<red>O inventário de <gold>{player}</gold> já está vazio.");
  }

  public MessagePair whenCleared() {
    return new MessagePair(cleared, clearedOther);
  }

  public MessagePair whenEmpty() {
    return new MessagePair(empty, emptyOther);
  }
}
