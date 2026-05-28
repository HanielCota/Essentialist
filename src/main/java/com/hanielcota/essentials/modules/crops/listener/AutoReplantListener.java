package com.hanielcota.essentials.modules.crops.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.crops.config.CropsConfig;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;

@RequiredArgsConstructor
public final class AutoReplantListener implements Listener {

  private final ConfigHandle<CropsConfig> config;

  private static Optional<Material> seedOf(@NonNull Material crop) {
    return switch (crop) {
      case WHEAT -> Optional.of(Material.WHEAT_SEEDS);
      case CARROTS -> Optional.of(Material.CARROT);
      case POTATOES -> Optional.of(Material.POTATO);
      case BEETROOTS -> Optional.of(Material.BEETROOT_SEEDS);
      case NETHER_WART -> Optional.of(Material.NETHER_WART);
      default -> Optional.empty();
    };
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockDrop(@NonNull BlockDropItemEvent event) {
    var blockState = event.getBlockState();
    var material = blockState.getType();

    // NETHER_WART is Ageable but is not part of the vanilla minecraft:crops tag, so it must be
    // admitted explicitly or its seedOf/replant branch is unreachable.
    var replantable = Tag.CROPS.isTagged(material) || material == Material.NETHER_WART;

    if (!replantable) {
      return;
    }

    var blockData = blockState.getBlockData();

    if (!(blockData instanceof Ageable ageable)) {
      return;
    }

    if (ageable.getAge() != ageable.getMaximumAge()) {
      return;
    }

    var snap = this.config.value();

    if (!snap.autoReplant() || !snap.isCropAllowed(material)) {
      return;
    }

    var worldName = blockState.getWorld().getName();

    if (!snap.appliesTo(worldName)) {
      return;
    }

    if (snap.replantConsumesSeed()) {
      consumeSeed(event, material);
    }

    replant(event, material);
  }

  private void replant(@NonNull BlockDropItemEvent event, @NonNull Material material) {
    var block = event.getBlock();
    block.setType(material);

    var newData = block.getBlockData();

    if (newData instanceof Ageable newAgeable) {
      newAgeable.setAge(0);
      block.setBlockData(newAgeable);
    }
  }

  private void consumeSeed(@NonNull BlockDropItemEvent event, @NonNull Material crop) {
    var seedOpt = seedOf(crop);

    if (seedOpt.isEmpty()) {
      return;
    }

    var seed = seedOpt.get();
    var drops = event.getItems();
    var iterator = drops.iterator();

    while (iterator.hasNext()) {
      var dropped = iterator.next();
      var stack = dropped.getItemStack();

      if (stack.getType() != seed) {
        continue;
      }

      var remaining = stack.getAmount() - 1;

      if (remaining <= 0) {
        iterator.remove();
        return;
      }

      stack.setAmount(remaining);
      dropped.setItemStack(stack);
      return;
    }
  }
}
