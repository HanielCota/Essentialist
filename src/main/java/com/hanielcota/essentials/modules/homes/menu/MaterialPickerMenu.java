package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialIconRegistry;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialPickerPresentation;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Paginated material picker. Shows every Minecraft item from the category the player chose in
 * {@link MaterialCategoryMenu}.
 *
 * <p>Performance optimisations from MenuFramework:
 *
 * <ul>
 *   <li>{@link PaginationConfig} with a dense content-slot grid — maximises items per page.
 *   <li>{@link MaterialIconRegistry} pre-builds every {@link SlotDefinition} at plugin startup; the
 *       dynamic-content provider only copies the cached list, never touches {@link
 *       Material#values()} or string formatting.
 *   <li>The pagination engine handles page splitting and nav buttons natively — no manual page-math
 *       in application code.
 * </ul>
 */
@RequiredArgsConstructor
public final class MaterialPickerMenu implements Menu {

  public static final String ID = "essentials.homes.picker";

  private static final int ROWS = 6;

  /** Interior content slots: excludes the outer border so nav buttons have room. */
  private static final List<Integer> CONTENT_SLOTS =
      List.of(
          10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
          38, 39, 40, 41, 42, 43);

  private static final int BACK_SLOT = 49;

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final MenuService menus;
  private final HomesActionTarget target;
  private final MaterialPickerPresentation presentation;
  private final MaterialIconRegistry registry;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menusRef) {
    var menuSpec = this.config.value().menu();
    var menuTitle = ComponentUtils.mini(menuSpec.staticPickerTitle());

    var pagination = PaginationConfig.builder().contentSlots(CONTENT_SLOTS).build();

    MenuFramework.builder(ID, menusRef)
        .rows(ROWS)
        .title(menuTitle)
        .pagination(pagination)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var uuid = player.getUniqueId();
    var category = this.target.peekCategory(uuid);

    if (category == null) {
      return List.of();
    }

    var icons = this.registry.iconsFor(category);
    var slots = new java.util.ArrayList<SlotDefinition>(icons.size() + 1);

    for (var icon : icons) {
      var pickedMaterial = icon.material();
      slots.add(
          SlotDefinition.of(
              -1,
              icon.template(),
              ctx -> {
                var clicked = ctx.player();
                var clickedUuid = clicked.getUniqueId();
                var homeName = this.target.consume(clickedUuid);
                this.target.consumeCategory(clickedUuid);

                ctx.close();

                if (homeName == null) {
                  this.menus.open(clicked, HomesMenu.ID);
                  return;
                }

                var messages = this.config.value().messages();
                var applied = this.service.setMaterial(clickedUuid, homeName, pickedMaterial);

                var replyText =
                    this.presentation.reply(messages, homeName, pickedMaterial, applied);
                var replyComponent = ComponentUtils.mini(replyText);
                clicked.sendMessage(replyComponent);

                this.menus.open(clicked, HomesMenu.ID);
              }));
    }

    slots.add(backButtonSlot());

    return slots;
  }

  private @NonNull SlotDefinition backButtonSlot() {
    var back =
        ItemTemplate.builder(org.bukkit.Material.BARRIER)
            .name("<red>Voltar às categorias")
            .italic(false)
            .build();

    return SlotDefinition.of(
        BACK_SLOT,
        back,
        ctx -> {
          var clicked = ctx.player();
          var uuid = clicked.getUniqueId();
          this.target.consumeCategory(uuid);
          ctx.close();
          this.menus.open(clicked, MaterialCategoryMenu.ID);
        });
  }
}
