package com.hanielcota.essentials.modules.info.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.modules.info.service.InfoEntry;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * A generic /info detail menu: it renders a list of {@link InfoEntry} rows and a back button. The
 * three categories (server, player, about) are instances of this class with different providers.
 */
public final class InfoCategoryMenu implements Menu {

  private static final int ROWS = 3;
  private static final List<Integer> CONTENT_SLOTS = List.of(9, 10, 11, 12, 13, 14, 15, 16, 17);
  private static final int BACK_SLOT = 22;

  private final String id;
  private final Supplier<String> title;
  private final Function<Player, List<InfoEntry>> entriesProvider;

  public InfoCategoryMenu(
      String id, Supplier<String> title, Function<Player, List<InfoEntry>> entriesProvider) {
    this.id = Objects.requireNonNull(id, "id");
    this.title = Objects.requireNonNull(title, "title");
    this.entriesProvider = Objects.requireNonNull(entriesProvider, "entriesProvider");
  }

  @Override
  public @NonNull String id() {
    return id;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    Objects.requireNonNull(menus, "menus");
    var pagination = PaginationConfig.builder().contentSlots(CONTENT_SLOTS).build();
    var backButton =
        ItemTemplate.builder(Material.ARROW).name("<yellow>Voltar").italic(false).build();

    MenuFramework.builder(id, menus)
        .rows(ROWS)
        .title(ComponentUtils.mini(title.get()))
        .pagination(pagination)
        .backButton(BACK_SLOT, backButton)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(Player player, MenuSession session) {
    var entries = entriesProvider.apply(player);
    var slots = new ArrayList<SlotDefinition>(entries.size());
    for (var entry : entries) {
      var template =
          ItemTemplate.builder(entry.icon())
              .name(entry.name())
              .lore(entry.lore().toArray(String[]::new))
              .italic(false)
              .build();
      slots.add(SlotDefinition.of(-1, template, click -> {}));
    }
    return slots;
  }
}
