package com.hanielcota.essentials.modules.info.menu.presentation;

import com.hanielcota.essentials.modules.info.config.InfoEntryConfig;
import com.hanielcota.essentials.shared.Placeholders;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Material;
import org.jspecify.annotations.Nullable;

/**
 * One row of information shown in the /info menu: an icon, a name and lore lines. When {@code
 * headOwner} is set, the row is rendered as that player's head (texture resolution happens at
 * render time via {@code PlayerHeadTextures}).
 */
public record InfoEntry(Material icon, String name, List<String> lore, @Nullable UUID headOwner) {

  public InfoEntry {
    lore = List.copyOf(lore);
  }

  public static InfoEntry of(
      @NonNull Material icon, @NonNull String name, @NonNull String... lore) {
    return new InfoEntry(icon, name, List.of(lore), null);
  }

  /** An entry rendered as the player head of {@code owner}. */
  public static InfoEntry head(@NonNull UUID owner, @NonNull String name, @NonNull String... lore) {
    return new InfoEntry(Material.PLAYER_HEAD, name, List.of(lore), owner);
  }

  /** Expands {@code template}'s name/lore against {@code values}; uses the template's icon. */
  public static InfoEntry from(@NonNull InfoEntryConfig template, @NonNull Map<String, ?> values) {
    var name = Placeholders.format(template.name(), values);
    var lore = Placeholders.formatAll(template.lore(), values);

    return new InfoEntry(template.icon(), name, lore, null);
  }

  /** Same as {@link #from} but forces {@code PLAYER_HEAD} with {@code owner}'s skin. */
  public static InfoEntry headFrom(
      @NonNull UUID owner, @NonNull InfoEntryConfig template, @NonNull Map<String, ?> values) {
    var name = Placeholders.format(template.name(), values);
    var lore = Placeholders.formatAll(template.lore(), values);

    return new InfoEntry(Material.PLAYER_HEAD, name, lore, owner);
  }
}
