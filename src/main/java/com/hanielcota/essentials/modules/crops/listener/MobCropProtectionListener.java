package com.hanielcota.essentials.modules.crops.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.crops.config.CropsConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

@RequiredArgsConstructor
public final class MobCropProtectionListener implements Listener {

  private final ConfigHandle<CropsConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEntityChangeBlock(@NonNull EntityChangeBlockEvent event) {
    if (event.getEntity() instanceof Player) {
      return;
    }

    var snap = this.config.value();
    var entityType = event.getEntityType();

    if (!snap.isMobBlocked(entityType)) {
      return;
    }

    var block = event.getBlock();
    var worldName = block.getWorld().getName();

    if (!snap.appliesTo(worldName)) {
      return;
    }

    var material = block.getType();

    if (material == Material.FARMLAND && snap.preventMobTrampling()) {
      event.setCancelled(true);
      return;
    }

    var isManagedCrop = Tag.CROPS.isTagged(material) && snap.isCropAllowed(material);

    if (isManagedCrop && snap.preventMobDamage()) {
      event.setCancelled(true);
    }
  }
}
