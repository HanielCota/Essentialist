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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Single /info menu with category and detail tabs. Switching tabs re-renders this same inventory
 * via {@link ClickContext#refresh()} â€” it never opens a separate menu, which keeps the
 * framework's navigation history and session intact.
 */
@RequiredArgsConstructor
public final class InfoMenu implements Menu, Listener {

  public static final String ID = "essentials.info";

  private static final int ROWS = 4;
  private static final List<Integer> CONTENT_SLOTS = List.of(9, 10, 11, 12, 13, 14, 15, 16, 17, 31);
  private static final int BACK_INDEX = 9;

  /** A handler-less slot the renderer skips while still advancing the slot index. */
  private static final SlotDefinition SKIP = SlotDefinition.withHandler(-1, null);

  private final ConfigHandle<InfoConfig> config;
  private final InfoService service;
  private final Map<UUID, Tab> openTab = new ConcurrentHashMap<>();
  private final Map<UUID, UUID> playerTarget = new ConcurrentHashMap<>();

  private static SlotDefinition entryItem(@NonNull InfoEntry entry) {
    var builder =
        ItemTemplate.builder(entry.icon())
            .name(entry.name())
            .lore(entry.lore().toArray(String[]::new))
            .italic(false);
    if (entry.headOwner() != null) {
      builder.head(entry.headOwner());
    }
    return SlotDefinition.of(-1, builder.build(), click -> {});
  }

  /**
   * Prepares the menu for {@code viewer} before it is opened: the player tab will show {@code
   * target}, and the menu starts on the player tab whenever a different player was requested.
   */
  public void prepare(@NonNull UUID viewer, @NonNull UUID target) {
    this.playerTarget.put(viewer, target);
    this.openTab.put(viewer, viewer.equals(target) ? Tab.CATEGORIES : Tab.PLAYER);
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var pagination = PaginationConfig.builder().contentSlots(CONTENT_SLOTS).build();

    var menuTitle = ComponentUtils.mini(this.config.value().menuTitle());
    MenuFramework.builder(ID, menus)
        .rows(ROWS)
        .title(menuTitle)
        .pagination(pagination)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    Tab tab = this.openTab.getOrDefault(player.getUniqueId(), Tab.CATEGORIES);
    return switch (tab) {
      case CATEGORIES -> categorySlots();
      case SERVER -> detailSlots(this.service.serverEntries());
      case PLAYER -> detailSlots(this.service.playerEntries(resolveTarget(player)));
      case ABOUT -> detailSlots(this.service.aboutEntries());
    };
  }

  /**
   * The player whose info {@code viewer} is looking at â€” the /info argument, or {@code viewer}.
   */
  private Player resolveTarget(@NonNull Player viewer) {
    UUID targetId = this.playerTarget.getOrDefault(viewer.getUniqueId(), viewer.getUniqueId());
    Player target = Bukkit.getPlayer(targetId);
    return target != null ? target : viewer;
  }

  private List<SlotDefinition> categorySlots() {
    return List.of(
        SKIP,
        SKIP,
        category(
            Material.COMMAND_BLOCK, "<yellow>Servidor", "<gray>Status do servidor.", Tab.SERVER),
        SKIP,
        category(
            Material.PLAYER_HEAD,
            "<yellow>Jogador",
            "<gray>InformaÃ§Ãµes de um jogador.",
            Tab.PLAYER),
        SKIP,
        category(
            Material.ENCHANTED_BOOK, "<yellow>Essentialist", "<gray>Sobre o plugin.", Tab.ABOUT));
  }

  private List<SlotDefinition> detailSlots(@NonNull List<InfoEntry> entries) {
    var slots = new ArrayList<SlotDefinition>(BACK_INDEX + 1);
    for (var entry : entries) {
      slots.add(entryItem(entry));
    }
    while (slots.size() < BACK_INDEX) {
      slots.add(SKIP);
    }
    var back = ItemTemplate.builder(Material.ARROW).name("<yellow>Voltar").italic(false).build();
    slots.add(SlotDefinition.of(-1, back, click -> switchTab(click, Tab.CATEGORIES)));
    return slots;
  }

  private SlotDefinition category(
      @NonNull Material icon, @NonNull String name, @NonNull String lore, @NonNull Tab target) {
    var template = ItemTemplate.builder(icon).name(name).lore(lore).italic(false).build();
    return SlotDefinition.of(-1, template, click -> switchTab(click, target));
  }

  private void switchTab(@NonNull ClickContext click, @NonNull Tab tab) {
    this.openTab.put(click.player().getUniqueId(), tab);
    click.refresh();
  }

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var id = event.getPlayer().getUniqueId();
    this.openTab.remove(id);
    this.playerTarget.remove(id);
  }

  private enum Tab {
    CATEGORIES,
    SERVER,
    PLAYER,
    ABOUT
  }
}
