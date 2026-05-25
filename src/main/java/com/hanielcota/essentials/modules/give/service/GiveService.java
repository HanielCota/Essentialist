package com.hanielcota.essentials.modules.give.service;

import com.hanielcota.essentials.modules.give.domain.GiveResult;
import java.util.function.BiConsumer;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class GiveService {

  /** Gives {@code amount} of {@code material} to the player, returning how many did not fit. */
  public int give(@NonNull Player player, @NonNull Material material, int amount) {
    var result = giveResult(player, material, amount);

    return result.leftover();
  }

  public GiveResult giveResult(@NonNull Player player, @NonNull Material material, int amount) {
    var stack = new ItemStack(material, amount);
    var inventory = player.getInventory();
    var leftovers = inventory.addItem(stack);
    var leftoverStacks = leftovers.values();

    var leftover = 0;
    for (var stackEntry : leftoverStacks) {
      leftover += stackEntry.getAmount();
    }

    return GiveResult.of(amount, leftover);
  }

  /**
   * Gives {@code amount} of {@code material} to every player in the roster. Each delivery invokes
   * {@code onEach} so the caller can notify recipients. Returns the number of players that received
   * at least one item (skips full inventories).
   */
  public int giveAll(
      @NonNull Iterable<? extends Player> roster,
      @NonNull Material material,
      int amount,
      @NonNull BiConsumer<Player, GiveResult> onEach) {
    var delivered = 0;
    for (var player : roster) {
      var result = giveResult(player, material, amount);
      onEach.accept(player, result);
      if (!result.noneGiven()) {
        delivered++;
      }
    }

    return delivered;
  }
}
