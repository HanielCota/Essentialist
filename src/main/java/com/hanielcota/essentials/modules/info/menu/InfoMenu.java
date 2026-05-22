package com.hanielcota.essentials.modules.info.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.modules.info.service.InfoEntry;
import com.hanielcota.essentials.modules.info.service.InfoService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * Single /info menu with category and detail tabs. Switching tabs re-renders this same inventory
 * via {@link ClickContext#refresh()} — it never opens a separate menu, which keeps the framework's
 * navigation history and session intact.
 */
public final class InfoMenu implements Menu {

  public static final String ID = "essentials.info";

  private static final int ROWS = 4;
  private static final List<Integer> CONTENT_SLOTS = List.of(10, 11, 12, 13, 14, 15, 16, 31);
  private static final int BACK_INDEX = 7;

  /** A handler-less slot the renderer skips while still advancing the slot index. */
  private static final SlotDefinition SKIP = SlotDefinition.withHandler(-1, null);

  private final ConfigHandle<InfoConfig> config;
  private final InfoService service;
  private final Map<UUID, Tab> openTab = new ConcurrentHashMap<>();

  public InfoMenu(ConfigHandle<InfoConfig> config, InfoService service) {
    this.config = Objects.requireNonNull(config, "config");
    this.service = Objects.requireNonNull(service, "service");
  }

  /** Resets the viewer to the category tab; call before opening the menu. */
  public void reset(UUID viewer) {
    Objects.requireNonNull(viewer, "viewer");
    openTab.remove(viewer);
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    Objects.requireNonNull(menus, "menus");
    var pagination = PaginationConfig.builder().contentSlots(CONTENT_SLOTS).build();

    MenuFramework.builder(ID, menus)
        .rows(ROWS)
        .title(ComponentUtils.mini(config.value().menuTitle()))
        .pagination(pagination)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(Player player, MenuSession session) {
    Tab tab = openTab.getOrDefault(player.getUniqueId(), Tab.CATEGORIES);
    return switch (tab) {
      case CATEGORIES -> categorySlots();
      case SERVER -> detailSlots(service.serverEntries());
      case PLAYER -> detailSlots(service.playerEntries(player));
      case ABOUT -> detailSlots(service.aboutEntries());
    };
  }

  private List<SlotDefinition> categorySlots() {
    return List.of(
        SKIP,
        category(
            Material.COMMAND_BLOCK, "<yellow>Servidor", "<gray>Status do servidor.", Tab.SERVER),
        SKIP,
        category(Material.PLAYER_HEAD, "<yellow>Jogador", "<gray>Suas informações.", Tab.PLAYER),
        SKIP,
        category(
            Material.ENCHANTED_BOOK, "<yellow>Essentialist", "<gray>Sobre o plugin.", Tab.ABOUT));
  }

  private List<SlotDefinition> detailSlots(List<InfoEntry> entries) {
    var slots = new ArrayList<SlotDefinition>(BACK_INDEX + 1);
    for (var entry : entries) {
      var template =
          ItemTemplate.builder(entry.icon())
              .name(entry.name())
              .lore(entry.lore().toArray(String[]::new))
              .italic(false)
              .build();
      slots.add(SlotDefinition.of(-1, template, click -> {}));
    }
    while (slots.size() < BACK_INDEX) {
      slots.add(SKIP);
    }
    var back = ItemTemplate.builder(Material.ARROW).name("<yellow>Voltar").italic(false).build();
    slots.add(SlotDefinition.of(-1, back, click -> switchTab(click, Tab.CATEGORIES)));
    return slots;
  }

  private SlotDefinition category(Material icon, String name, String lore, Tab target) {
    var template = ItemTemplate.builder(icon).name(name).lore(lore).italic(false).build();
    return SlotDefinition.of(-1, template, click -> switchTab(click, target));
  }

  private void switchTab(ClickContext click, Tab tab) {
    openTab.put(click.player().getUniqueId(), tab);
    click.refresh();
  }

  private enum Tab {
    CATEGORIES,
    SERVER,
    PLAYER,
    ABOUT
  }
}
