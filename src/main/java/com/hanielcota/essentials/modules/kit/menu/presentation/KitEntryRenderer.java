package com.hanielcota.essentials.modules.kit.menu.presentation;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.kit.config.KitConfig;
import com.hanielcota.essentials.modules.kit.config.KitListMenuConfig;
import com.hanielcota.essentials.modules.kit.domain.Kit;
import com.hanielcota.essentials.modules.kit.service.KitCooldownService;
import com.hanielcota.essentials.modules.kit.service.KitDurations;
import com.hanielcota.essentials.shared.Placeholders;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Builds a kit's list icon, reflecting its state for the viewer (available / cooldown / locked).
 */
@RequiredArgsConstructor
public final class KitEntryRenderer {

  private final ConfigHandle<KitConfig> config;
  private final KitCooldownService cooldowns;

  public ItemTemplate render(@NonNull Player player, @NonNull Kit kit) {
    var cfg = this.config.value().listMenu();
    var uuid = player.getUniqueId();
    var name = cfg.itemName().replace("{kit}", kit.displayName());

    if (kit.hasPermission() && !player.hasPermission(kit.permission())) {
      return locked(cfg, name, cfg.noPermissionLore());
    }
    if (kit.oneTime() && this.cooldowns.hasClaimed(uuid, kit)) {
      return locked(cfg, name, cfg.claimedLore());
    }

    var remaining = kit.hasCooldown() ? this.cooldowns.remainingSeconds(uuid, kit) : 0;
    if (remaining > 0) {
      var time = KitDurations.format(remaining);
      var lore = Placeholders.replaceInAll(cfg.cooldownLore(), "{time}", time);

      return entry(kit.icon(), name, lore, false);
    }

    return entry(kit.icon(), name, cfg.availableLore(), cfg.glowWhenAvailable());
  }

  private static ItemTemplate locked(
      @NonNull KitListMenuConfig cfg, @NonNull String name, @NonNull List<String> lore) {
    return entry(cfg.lockedMaterial(), name, lore, false);
  }

  private static ItemTemplate entry(
      @NonNull Material material, @NonNull String name, @NonNull List<String> lore, boolean glow) {
    var loreArray = lore.toArray(String[]::new);

    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(loreArray);
    builder.italic(false);
    builder.glow(glow);

    return builder.build();
  }
}
