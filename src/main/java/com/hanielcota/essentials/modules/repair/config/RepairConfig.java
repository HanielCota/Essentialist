package com.hanielcota.essentials.modules.repair.config;

import com.hanielcota.essentials.config.MessagePair;
import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record RepairConfig(
    @Comment("Shown to the player when their held item is repaired.") String repairedHand,
    @Comment("Placeholders: {player}.") String repairedHandOther,
    @Comment("Shown after /reparar tudo. Placeholders: {count}.") String repairedAll,
    @Comment("Placeholders: {player}, {count}.") String repairedAllOther,
    @Comment("Shown when the held item does not need repair.") String nothingHand,
    @Comment("Placeholders: {player}.") String nothingHandOther,
    @Comment("Shown when no inventory item needs repair.") String nothingAll,
    @Comment("Placeholders: {player}.") String nothingAllOther,
    @Comment("Shown when the player's main hand is empty.") String emptyHand,
    @Comment("Placeholders: {player}.") String emptyHandOther,
    @Comment("Materials that can never be repaired (Bukkit Material names).")
        List<Material> blacklist,
    @Comment("Maximum items repaired per /reparar tudo execution.") int repairAllLimit) {

  public static RepairConfig defaults() {
    return new RepairConfig(
        "<green>Item repaired.",
        "<green>Repaired the item of <gold>{player}</gold>.",
        "<green>Repaired <gold>{count}</gold> item(s).",
        "<green>Repaired <gold>{count}</gold> item(s) of <gold>{player}</gold>.",
        "<red>The item in your hand does not need repair.",
        "<red>The item in <gold>{player}</gold>'s hand does not need repair.",
        "<red>No item in your inventory needs repair.",
        "<red>No item in <gold>{player}</gold>'s inventory needs repair.",
        "<red>Your hand is empty.",
        "<red><gold>{player}</gold>'s hand is empty.",
        List.of(),
        41);
  }

  public MessagePair whenHandRepaired() {
    return new MessagePair(repairedHand, repairedHandOther);
  }

  public MessagePair whenAllRepaired() {
    return new MessagePair(repairedAll, repairedAllOther);
  }

  public MessagePair whenNothingHand() {
    return new MessagePair(nothingHand, nothingHandOther);
  }

  public MessagePair whenNothingAll() {
    return new MessagePair(nothingAll, nothingAllOther);
  }

  public MessagePair whenEmptyHand() {
    return new MessagePair(emptyHand, emptyHandOther);
  }
}
