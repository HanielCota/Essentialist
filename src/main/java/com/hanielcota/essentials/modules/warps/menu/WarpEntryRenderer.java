package com.hanielcota.essentials.modules.warps.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.config.WarpsMenuConfig;
import com.hanielcota.essentials.modules.warps.domain.Warp;
import com.hanielcota.essentials.modules.warps.service.WarpFavorites;
import com.hanielcota.essentials.modules.warps.service.WarpLikes;
import com.hanielcota.essentials.modules.warps.service.WarpOccupancy;
import com.hanielcota.essentials.shared.Numbers;
import com.hanielcota.essentials.shared.Placeholders;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class WarpEntryRenderer {

  private static final String DESCRIPTION_TOKEN = "{description}";
  private static final String PVP_TOKEN = "{pvp}";
  private static final String FAVORITE_TOKEN = "{favorite}";
  private static final String WARP_TOKEN = "{warp}";

  private final ConfigHandle<WarpsConfig> config;
  private final WarpOccupancy occupancy;
  private final WarpLikes likes;
  private final WarpFavorites favorites;

  public @NonNull ItemTemplate render(@NonNull Warp warp, @NonNull UUID viewerId) {
    var snap = this.config.value();
    var menu = snap.menu();

    var icon = snap.iconFor(warp);
    var displayName = snap.displayNameFor(warp);
    var description = snap.loreFor(warp);
    var pvp = snap.isPvp(warp.name());
    var favorited = this.favorites.isFavorite(viewerId, warp.name());

    var players = this.occupancy.count(warp.name());
    var likeCount = this.likes.count(warp.name());

    var name = menu.entryName().replace(WARP_TOKEN, displayName);
    var lore = buildLore(menu, warp, description, pvp, favorited, players, likeCount);

    var builder = ItemTemplate.builder(icon);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  private static List<String> buildLore(
      @NonNull WarpsMenuConfig menu,
      @NonNull Warp warp,
      @NonNull List<String> description,
      boolean pvp,
      boolean favorited,
      int players,
      int likeCount) {
    var placeholders =
        Map.of(
            "world", warp.world(),
            "x", Numbers.display(warp.x()),
            "y", Numbers.display(warp.y()),
            "z", Numbers.display(warp.z()),
            "players", Integer.toString(players),
            "likes", Integer.toString(likeCount));

    var lore = new ArrayList<String>(menu.entryLore().size() + description.size());
    for (var line : menu.entryLore()) {
      if (line.equals(DESCRIPTION_TOKEN)) {
        lore.addAll(description);
        continue;
      }
      if (line.equals(PVP_TOKEN)) {
        if (pvp) {
          lore.add(menu.pvpTag());
        }
        continue;
      }
      if (line.equals(FAVORITE_TOKEN)) {
        if (favorited) {
          lore.add(menu.favoriteTag());
        }
        continue;
      }
      lore.add(Placeholders.format(line, placeholders));
    }

    return lore;
  }
}
