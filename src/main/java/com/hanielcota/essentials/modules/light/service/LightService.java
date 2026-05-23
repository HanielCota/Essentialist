package com.hanielcota.essentials.modules.light.service;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class LightService {

  private final NamespacedKey key;

  public LightService(Plugin plugin) {
    this.key = new NamespacedKey(plugin, "light_night_vision");
  }

  private static void applyEffect(Player player) {
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0));
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
    boolean next = !isEnabled(player);
    set(player, next);
    return next;
  }

  /** Whether {@code player} has command-managed night vision active. */
  public boolean isEnabled(Player player) {
    return player.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
  }

  /** Enables or disables command-managed night vision explicitly. */
  public void set(Player player, boolean enabled) {
    var pdc = player.getPersistentDataContainer();
    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    if (!enabled) {
      pdc.remove(key);
      return;
    }
    pdc.set(key, PersistentDataType.BYTE, (byte) 1);
    applyEffect(player);
  }

  /**
   * Re-applies the infinite night-vision effect without touching the PDC flag. Use this after
   * external events (death/respawn, milk bucket) that wipe the effect but leave the PDC saying it
   * should still be on.
   */
  public void reapply(Player player) {
    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    applyEffect(player);
  }
}
