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
        String emptyOther,
    @Comment("When true, /clear also removes worn armor and the off-hand item.")
        boolean clearArmor) {

  public static ClearConfig defaults() {
    return new ClearConfig(
        "<green>Inventory cleared (<gold>{count}</gold> item(s) removed).",
        "<green>Cleared <gold>{player}</gold>'s inventory (<gold>{count}</gold> item(s)).",
        "<red>Your inventory is already empty.",
        "<red><gold>{player}</gold>'s inventory is already empty.",
        false);
  }

  public MessagePair whenCleared() {
    return new MessagePair(cleared, clearedOther);
  }

  public MessagePair whenEmpty() {
    return new MessagePair(empty, emptyOther);
  }
}
