package com.hanielcota.essentials.modules.kit.config;

import com.hanielcota.essentials.modules.kit.domain.KitCategory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Root kit config (kit.yml): behaviour, categories, the three menus and the messages. */
@ConfigSerializable
public record KitConfig(
    @Comment(
            "When the inventory is full, drop the overflow at the player's feet instead of losing"
                + " it.")
        boolean dropWhenInventoryFull,
    @Comment("Sound played on a successful claim (Bukkit sound key). Empty disables it.")
        String claimSound,
    @Comment("Volume of the claim sound.") float claimVolume,
    @Comment("Pitch of the claim sound.") float claimPitch,
    @Comment("Kit categories keyed by id. Each kit references one of these ids.")
        Map<String, KitCategoryConfig> categories,
    @Comment("The /kit category menu.") KitCategoryMenuConfig categoryMenu,
    @Comment("The paginated kit list menu.") KitListMenuConfig listMenu,
    @Comment("The read-only kit preview menu.") KitPreviewMenuConfig previewMenu,
    @Comment("Chat messages.") KitMessages messages) {

  public KitConfig {
    categories = categories == null ? new LinkedHashMap<>() : new LinkedHashMap<>(categories);
  }

  public static KitConfig defaults() {
    var general = new KitCategoryConfig("<green>General", Material.CHEST, 1);
    var vip = new KitCategoryConfig("<gold>VIP", Material.DIAMOND, 2);

    var categories = new LinkedHashMap<String, KitCategoryConfig>();
    categories.put("general", general);
    categories.put("vip", vip);

    return new KitConfig(
        true,
        "entity.experience_orb.pickup",
        1.0f,
        1.0f,
        categories,
        KitCategoryMenuConfig.defaults(),
        KitListMenuConfig.defaults(),
        KitPreviewMenuConfig.defaults(),
        KitMessages.defaults());
  }

  public boolean playsClaimSound() {
    return !this.claimSound.isBlank();
  }

  /** Categories as domain objects, sorted by order then id. */
  public List<KitCategory> sortedCategories() {
    var resolved = new ArrayList<KitCategory>(this.categories.size());
    for (var entry : this.categories.entrySet()) {
      resolved.add(toCategory(entry.getKey(), entry.getValue()));
    }

    resolved.sort(Comparator.comparingInt(KitCategory::order).thenComparing(KitCategory::id));
    return List.copyOf(resolved);
  }

  /** Resolves a category by id, synthesising a fallback when the id is unknown. */
  public KitCategory category(@NonNull String id) {
    var configured = this.categories.get(id);
    if (configured == null) {
      return new KitCategory(id, id, Material.CHEST, Integer.MAX_VALUE);
    }

    return toCategory(id, configured);
  }

  private static KitCategory toCategory(@NonNull String id, @NonNull KitCategoryConfig config) {
    return new KitCategory(id, config.displayName(), config.icon(), config.order());
  }
}
