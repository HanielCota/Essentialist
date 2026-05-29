package com.hanielcota.essentials.modules.give.config;

import com.hanielcota.essentials.config.MessagePair;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record GiveConfig(
    @Comment("Shown when the full amount is received. Placeholders: {amount}, {item}.")
        String given,
    @Comment("Placeholders: {amount}, {item}, {player}.") String givenOther,
    @Comment("Shown when only part of the items fit. Placeholders: {amount}, {leftover}, {item}.")
        String partial,
    @Comment("Placeholders: {amount}, {leftover}, {item}, {player}.") String partialOther,
    @Comment("Shown when nothing fit because the inventory is full. Placeholders: {item}.")
        String inventoryFull,
    @Comment("Placeholders: {item}, {player}.") String inventoryFullOther,
    @Comment("Shown when the material cannot be given as an item.") String invalidItem,
    @Comment("Shown when the amount is not a positive number.") String invalidAmount,
    @Comment("Shown when the amount exceeds maxAmount. Placeholders: {max}.") String amountTooLarge,
    @Comment("Largest amount accepted by /give in a single command.") int maxAmount,
    @Comment("Shown to the sender after /give all. Placeholders: {amount}, {item}, {count}.")
        String givenAll) {

  public static GiveConfig defaults() {
    return new GiveConfig(
        "<green>You received <gold>{amount}x {item}</gold>.",
        "<green>You gave <gold>{amount}x {item}</gold> to <gold>{player}</gold>.",
        "<yellow>You received <gold>{amount}x {item}</gold>; <gold>{leftover}</gold> did not fit.",
        "<yellow>You gave <gold>{amount}x {item}</gold> to <gold>{player}</gold>;"
            + " <gold>{leftover}</gold> did not fit.",
        "<red>The inventory is full.",
        "<red><gold>{player}</gold>'s inventory is full.",
        "<red>That material cannot be given as an item.",
        "<red>The amount must be a positive number.",
        "<red>The maximum amount per command is <gold>{max}</gold>.",
        2304,
        "<green>You gave <gold>{amount}x {item}</gold> to <gold>{count}</gold> player(s).");
  }

  public MessagePair whenGiven() {
    return new MessagePair(given, givenOther);
  }

  public MessagePair whenPartial() {
    return new MessagePair(partial, partialOther);
  }

  public MessagePair whenInventoryFull() {
    return new MessagePair(inventoryFull, inventoryFullOther);
  }

  public String formatAmountTooLarge() {
    var maxStr = Integer.toString(maxAmount);

    return amountTooLarge.replace("{max}", maxStr);
  }

  public String formatGivenAll(@NonNull String item, int amount, int count) {
    var amountStr = Integer.toString(amount);
    var countStr = Integer.toString(count);

    var withAmount = givenAll.replace("{amount}", amountStr);
    var withItem = withAmount.replace("{item}", item);

    return withItem.replace("{count}", countStr);
  }
}
