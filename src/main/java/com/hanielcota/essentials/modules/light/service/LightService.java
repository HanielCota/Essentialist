package com.hanielcota.essentials.modules.light.service;

import java.util.Objects;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class LightService {

  private final NamespacedKey key;

  public LightService(Plugin plugin) {
    Objects.requireNonNull(plugin, "plugin");
    this.key = new NamespacedKey(plugin, "light_night_vision");
  }

  /**
   * Toggles command-managed night vision.
   *
   * <p>The on/off state is tracked in the player's {@code PersistentDataContainer} instead of being
   * inferred from the effect's duration. This keeps the toggle correct even where another source (a
   * beacon, a potion) keeps reapplying a finite-duration night vision effect.
   *
   * @return {@code true} when night vision was enabled, {@code false} when disabled
   */
  public boolean toggle(Player player) {
    boolean enabled = !player.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    set(player, enabled);
    return enabled;
  }

  /** Enables or disables command-managed night vision explicitly. */
  public void set(Player player, boolean enabled) {
    var pdc = player.getPersistentDataContainer();
    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    if (enabled) {
      pdc.set(key, PersistentDataType.BYTE, (byte) 1);
      player.addPotionEffect(
          new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0));
    } else {
      pdc.remove(key);
    }
  }
}
