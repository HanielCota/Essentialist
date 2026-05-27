package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.ClickHandler;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.menu.PaginatedInfoMenus;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.menu.HomesMainMenuSection;
import com.hanielcota.essentials.modules.homes.config.menu.HomesMenuConfig;
import com.hanielcota.essentials.modules.homes.create.HomeCreateOrchestrator;
import com.hanielcota.essentials.modules.homes.domain.Home;
import com.hanielcota.essentials.modules.homes.domain.HomeOrdering;
import com.hanielcota.essentials.modules.homes.menu.presentation.HomeEntryRenderer;
import com.hanielcota.essentials.modules.homes.menu.presentation.HomesSortRenderer;
import com.hanielcota.essentials.modules.homes.service.HomeOrderingPreferences;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class HomesMenu implements EssentialsMenu {

  public static final String ID = "essentials.homes";

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final HomeEntryRenderer renderer;
  private final HomeClickHandler clickHandler;
  private final HomesMenuState state;
  private final HomeCreateOrchestrator create;
  private final HomeOrderingPreferences orderings;
  private final HomesSortRenderer sortRenderer = new HomesSortRenderer();

  private static @NonNull ItemTemplate buildInfoTemplate(@NonNull HomesMenuConfig menuSpec) {
    var infoMaterial = menuSpec.infoMaterial();
    var infoName = menuSpec.infoName();
    var infoLore = menuSpec.infoLore();

    return MenuTemplates.simple(infoMaterial, infoName, infoLore);
  }

  private static @NonNull ItemTemplate buildCreateTemplate(@NonNull HomesMenuConfig menuSpec) {
    var createMaterial = menuSpec.createMaterial();
    var createName = menuSpec.createName();
    var createLore = menuSpec.createLore();

    return MenuTemplates.simple(createMaterial, createName, createLore);
  }

  private static Comparator<Home> comparator(@NonNull HomeOrdering ordering) {
    var byName = Comparator.comparing(Home::name, String.CASE_INSENSITIVE_ORDER);
    var secondary =
        switch (ordering) {
          case NAME -> byName;
          case MOST_USED ->
              Comparator.comparingLong(Home::teleportCount).reversed().thenComparing(byName);
          case RECENT ->
              Comparator.comparingLong(Home::lastUsedAt).reversed().thenComparing(byName);
        };
    return Comparator.comparing(Home::pinned).reversed().thenComparing(secondary);
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var menuSpec = snap.menu();

    var rows = HomesMainMenuSection.rows(menuSpec);
    var titleText = menuSpec.title();
    var contentSlots = HomesMainMenuSection.contentSlots(menuSpec);
    var navigation = menuSpec.navigation();
    var infoTemplate = buildInfoTemplate(menuSpec);
    var infoSlot = HomesMainMenuSection.infoSlot(menuSpec);
    var createTemplate = buildCreateTemplate(menuSpec);
    var createSlot = HomesMainMenuSection.createSlot(menuSpec);

    PaginatedInfoMenus.register(
        menus,
        ID,
        rows,
        titleText,
        contentSlots,
        navigation,
        infoSlot,
        infoTemplate,
        this::buildSlots,
        builder -> builder.slot(createSlot, createTemplate, this::onCreateClicked));
  }

  private void onCreateClicked(@NonNull ClickContext click) {
    click.close();
    this.create.prompt(click.player());
  }

  private void onSortClicked(@NonNull ClickContext click) {
    var uuid = click.player().getUniqueId();
    this.orderings.cycle(uuid);
    click.refresh();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var uuid = player.getUniqueId();
    var prefetched = this.state.consumePrefetch(uuid);
    var entries = prefetched != null ? prefetched : this.service.homesOf(uuid);

    var ordering = this.orderings.of(uuid);
    var sorted = new ArrayList<>(entries);
    sorted.sort(comparator(ordering));

    var slots = new ArrayList<SlotDefinition>(sorted.size() + 2);

    var infoSlot = perViewerInfoSlot(player);
    if (infoSlot != null) {
      slots.add(infoSlot);
    }

    slots.add(sortSlot(ordering));

    for (var home : sorted) {
      var template = this.renderer.render(home);
      ClickHandler onClick = click -> this.clickHandler.handle(click, home);
      var slot = SlotDefinition.of(-1, template, onClick);

      slots.add(slot);
    }

    return slots;
  }

  // The framework overlays this dynamic slot on top of PaginatedInfoMenus' static info template,
  // so each viewer sees their own head in the info slot. Skipped when the admin disabled
  // infoUsePlayerHead to keep the static template visible.
  private SlotDefinition perViewerInfoSlot(@NonNull Player player) {
    var menuSpec = this.config.value().menu();
    if (!menuSpec.infoUsePlayerHead()) {
      return null;
    }

    var infoSlot = HomesMainMenuSection.infoSlot(menuSpec);
    var builder = ItemTemplate.builder(menuSpec.infoMaterial());
    builder.head(player.getUniqueId());
    builder.name(menuSpec.infoName());
    builder.lore(menuSpec.infoLore().toArray(String[]::new));
    builder.italic(false);

    return SlotDefinition.of(infoSlot, builder.build(), click -> {});
  }

  private SlotDefinition sortSlot(@NonNull HomeOrdering ordering) {
    var menuSpec = this.config.value().menu();
    var template = this.sortRenderer.sortTemplate(menuSpec, ordering);
    var safeSlot = HomesMainMenuSection.sortSlot(menuSpec);

    return SlotDefinition.of(safeSlot, template, this::onSortClicked);
  }
}
