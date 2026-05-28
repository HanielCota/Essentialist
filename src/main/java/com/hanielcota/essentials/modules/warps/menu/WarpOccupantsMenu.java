package com.hanielcota.essentials.modules.warps.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import com.hanielcota.essentials.menu.PaginatedInfoMenus;
import com.hanielcota.essentials.modules.warps.service.WarpOccupancy;
import com.hanielcota.essentials.modules.warps.service.WarpSelection;
import com.hanielcota.essentials.shared.PlayerHeadTextures;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Lists the players currently at the selected warp, with their heads. Opened from {@link
 * WarpActionsMenu}; reads the warp from {@link WarpSelection}. Read-only.
 */
@RequiredArgsConstructor
public final class WarpOccupantsMenu implements EssentialsMenu {

  public static final String ID = "essentials.warps.occupants";

  private static final String TITLE = "Quem está aqui";
  private static final int ROWS = 6;
  private static final int CONTENT_SLOTS = (ROWS - 1) * 9;
  private static final int BACK_SLOT = (ROWS - 1) * 9;
  private static final int PREVIOUS_SLOT = (ROWS - 1) * 9 + 3;
  private static final int INFO_SLOT = (ROWS - 1) * 9 + 4;
  private static final int NEXT_SLOT = (ROWS - 1) * 9 + 5;

  private final WarpSelection selection;
  private final WarpOccupancy occupancy;

  private static List<Integer> contentSlots() {
    return IntStream.range(0, CONTENT_SLOTS).boxed().toList();
  }

  private static ItemTemplate infoTemplate(int online) {
    var lore = List.of("<gray>Jogadores online aqui: <white>" + online);
    return MenuTemplates.info(Material.NETHER_STAR, "<gray>Quem está aqui", lore);
  }

  private static ItemTemplate headOf(@NonNull Player online) {
    var builder = ItemTemplate.builder(Material.PLAYER_HEAD);
    builder.name("<yellow>" + online.getName());
    builder.italic(false);
    PlayerHeadTextures.applyTo(builder, online);
    return builder.build();
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var navigation = NavigationButtonsConfig.defaults(PREVIOUS_SLOT, NEXT_SLOT);
    var baseInfo = infoTemplate(0);
    var backTemplate = MenuTemplates.simple(Material.ARROW, "<gray>Voltar", List.of());

    PaginatedInfoMenus.register(
        menus,
        ID,
        ROWS,
        TITLE,
        contentSlots(),
        navigation,
        INFO_SLOT,
        baseInfo,
        this::buildSlots,
        builder -> builder.slot(BACK_SLOT, backTemplate, click -> click.open(WarpActionsMenu.ID)));
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var warpName = this.selection.of(player.getUniqueId());
    if (warpName == null) {
      return List.of();
    }

    var occupants = this.occupancy.occupants(warpName);
    var slots = new ArrayList<SlotDefinition>(occupants.size() + 1);
    slots.add(SlotDefinition.of(INFO_SLOT, infoTemplate(occupants.size()), click -> {}));

    for (var occupantId : occupants) {
      var online = Bukkit.getPlayer(occupantId);
      if (online == null) {
        continue;
      }

      slots.add(SlotDefinition.of(-1, headOf(online), click -> {}));
    }

    return slots;
  }
}
