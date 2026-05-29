package com.hanielcota.essentials.modules.environment.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.environment.config.EnvironmentConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

@RequiredArgsConstructor
public final class MobGriefListener implements Listener {

  private final ConfigHandle<EnvironmentConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEntityChangeBlock(@NonNull EntityChangeBlockEvent event) {
    var entity = event.getEntity();

    // Only living mobs grief — excludes FallingBlock (sand/gravel physics) and projectiles, which
    // also fire this event but are not griefing.
    if (!(entity instanceof LivingEntity)) {
      return;
    }

    var snap = this.config.value();
    var worldName = event.getBlock().getWorld().getName();
    var entityTypeName = entity.getType().name();

    if (!snap.preventMobGriefing() || !snap.appliesTo(worldName)) {
      return;
    }
    if (!snap.isGriefEntityBlocked(entityTypeName)) {
      return;
    }

    event.setCancelled(true);
  }
}
