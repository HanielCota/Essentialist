package com.hanielcota.essentials.modules.info.menu.presentation;

import com.hanielcota.essentials.modules.info.config.InfoEntryConfig;
import com.hanielcota.essentials.shared.Placeholders;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * One row of information shown in the /info menu: an icon, a name and lore lines. When {@code
 * headTexture} is set, the row renders that base64 texture as a player head; when only {@code
 * headOwner} is set, it falls back to the UUID path.
 *
 * <p>The base64 texture path exists to work around the Steve-head bug: MenuFramework's UUID applier
 * uses {@code Bukkit.getOfflinePlayer(uuid).setOwningPlayer(...)} which doesn't reliably populate
 * the texture under Paper, so the cached ItemStack ends up with a blank profile. Pre-resolving the
 * texture from the live {@link Player} sidesteps that.
 */
public record InfoEntry(
    Material icon,
    String name,
    List<String> lore,
    @Nullable UUID headOwner,
    @Nullable String headTexture) {

  public InfoEntry {
    lore = List.copyOf(lore);
  }

  public static InfoEntry of(
      @NonNull Material icon, @NonNull String name, @NonNull String... lore) {
    return new InfoEntry(icon, name, List.of(lore), null, null);
  }

  /** An entry rendered as the player head of {@code owner}. */
  public static InfoEntry head(@NonNull UUID owner, @NonNull String name, @NonNull String... lore) {
    return new InfoEntry(Material.PLAYER_HEAD, name, List.of(lore), owner, null);
  }

  /** Expands {@code template}'s name/lore against {@code values}; uses the template's icon. */
  public static InfoEntry from(@NonNull InfoEntryConfig template, @NonNull Map<String, ?> values) {
    var name = Placeholders.format(template.name(), values);
    var lore = Placeholders.formatAll(template.lore(), values);

    return new InfoEntry(template.icon(), name, lore, null, null);
  }

  /**
   * Same as {@link #from} but renders the player head using {@code owner}'s live texture, so the
   * skin always shows (no Steve fallback).
   */
  public static InfoEntry headFrom(
      @NonNull Player owner, @NonNull InfoEntryConfig template, @NonNull Map<String, ?> values) {
    var name = Placeholders.format(template.name(), values);
    var lore = Placeholders.formatAll(template.lore(), values);
    var ownerId = owner.getUniqueId();
    var textures = PlayerHeadTextures.of(owner);

    return new InfoEntry(Material.PLAYER_HEAD, name, lore, ownerId, textures);
  }
}
