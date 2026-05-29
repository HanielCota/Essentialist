package com.hanielcota.essentials.modules.environment.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.environment.config.EnvironmentConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.PigZapEvent;

@RequiredArgsConstructor
public final class LightningTransformListener implements Listener {

  private final ConfigHandle<EnvironmentConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPigZap(@NonNull PigZapEvent event) {
    var worldName = event.getEntity().getWorld().getName();

    if (!isProtected(worldName)) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onCreeperPower(@NonNull CreeperPowerEvent event) {
    if (event.getCause() != CreeperPowerEvent.PowerCause.LIGHTNING) {
      return;
    }

    var worldName = event.getEntity().getWorld().getName();

    if (!isProtected(worldName)) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onTransform(@NonNull EntityTransformEvent event) {
    if (event.getTransformReason() != EntityTransformEvent.TransformReason.LIGHTNING) {
      return;
    }

    var worldName = event.getEntity().getWorld().getName();

    if (!isProtected(worldName)) {
      return;
    }

    event.setCancelled(true);
  }

  private boolean isProtected(@NonNull String worldName) {
    var snap = this.config.value();
    return snap.preventLightningTransform() && snap.appliesTo(worldName);
  }
}
