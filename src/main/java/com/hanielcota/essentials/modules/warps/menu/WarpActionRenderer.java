package com.hanielcota.essentials.modules.warps.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.service.WarpFavorites;
import com.hanielcota.essentials.modules.warps.service.WarpLikes;
import com.hanielcota.essentials.modules.warps.service.WarpOccupancy;
import com.hanielcota.essentials.modules.warps.service.WarpSelectionResolver;
import com.hanielcota.essentials.shared.Numbers;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/** Builds the per-viewer item templates of the warp action submenu. No click logic here. */
@RequiredArgsConstructor
public final class WarpActionRenderer {

  private final ConfigHandle<WarpsConfig> config;
  private final WarpFavorites favorites;
  private final WarpLikes likes;
  private final WarpOccupancy occupancy;
  private final WarpSelectionResolver resolver;

  private static ItemTemplate unavailable() {
    return MenuTemplates.simple(Material.BARRIER, "<red>Warp indisponível", List.of());
  }

  public ItemTemplate info(@NonNull Player player) {
    var warpOpt = this.resolver.resolve(player.getUniqueId());
    if (warpOpt.isEmpty()) {
      return unavailable();
    }

    var warp = warpOpt.get();
    var snap = this.config.value();
    var coords =
        Numbers.display(warp.x())
            + ", "
            + Numbers.display(warp.y())
            + ", "
            + Numbers.display(warp.z());

    var lore = new ArrayList<String>();
    lore.addAll(snap.loreFor(warp));
    lore.add("<gray>Mundo: <white>" + warp.world());
    lore.add("<gray>Local: <white>" + coords);
    lore.add("<gray>Jogadores aqui: <white>" + this.occupancy.count(warp.name()));
    lore.add("<gray>Curtidas: <white>" + this.likes.count(warp.name()));
    if (snap.isPvp(warp.name())) {
      lore.add("<red>PVP ativo");
    }

    var builder = ItemTemplate.builder(snap.iconFor(warp));
    builder.name(snap.displayNameFor(warp));
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);
    return builder.build();
  }

  public ItemTemplate favorite(@NonNull Player player) {
    var warpOpt = this.resolver.resolve(player.getUniqueId());
    if (warpOpt.isEmpty()) {
      return unavailable();
    }

    var favorited = this.favorites.isFavorite(player.getUniqueId(), warpOpt.get().name());
    var name = favorited ? "<yellow>★ Remover dos favoritos" : "<gray>☆ Favoritar";
    var hint = favorited ? "<gray>Clique para desfavoritar." : "<gray>Clique para favoritar.";

    return MenuTemplates.simple(Material.NETHER_STAR, name, List.of(hint));
  }

  public ItemTemplate like(@NonNull Player player) {
    var warpOpt = this.resolver.resolve(player.getUniqueId());
    if (warpOpt.isEmpty()) {
      return unavailable();
    }

    var warp = warpOpt.get();
    var liked = this.likes.hasLiked(player.getUniqueId(), warp.name());
    var name = liked ? "<red>❤ Descurtir" : "<gray>♡ Curtir";
    var countLine = "<gray>Curtidas: <white>" + this.likes.count(warp.name());

    return MenuTemplates.simple(Material.REDSTONE, name, List.of(countLine));
  }

  public ItemTemplate occupants(@NonNull Player player) {
    var warpOpt = this.resolver.resolve(player.getUniqueId());
    if (warpOpt.isEmpty()) {
      return unavailable();
    }

    var count = this.occupancy.count(warpOpt.get().name());
    var lore = List.of("<gray>Online aqui agora: <white>" + count, "<green>Clique para ver.");

    return MenuTemplates.simple(Material.PLAYER_HEAD, "<yellow>Quem está aqui", lore);
  }

  public ItemTemplate teleport(@NonNull Player player) {
    return MenuTemplates.simple(
        Material.ENDER_PEARL, "<green>Teleportar", List.of("<gray>Ir até esta warp."));
  }

  public ItemTemplate back(@NonNull Player player) {
    return MenuTemplates.simple(Material.ARROW, "<gray>Voltar", List.of("<gray>Voltar à lista."));
  }
}
