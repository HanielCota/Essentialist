package com.hanielcota.essentials.modules.info.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.List;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/** The /info root menu: three category icons that open the detail sub-menus. */
public final class InfoMenu implements Menu {

  public static final String ID = "essentials.info";
  public static final String SERVER_ID = "essentials.info.server";
  public static final String PLAYER_ID = "essentials.info.player";
  public static final String ABOUT_ID = "essentials.info.about";

  private static final int ROWS = 3;
  private static final List<Integer> CATEGORY_SLOTS = List.of(11, 13, 15);

  private final ConfigHandle<InfoConfig> config;

  public InfoMenu(ConfigHandle<InfoConfig> config) {
    this.config = Objects.requireNonNull(config, "config");
  }

  private static SlotDefinition category(
      Material icon, String name, String lore, String targetMenu) {
    var template = ItemTemplate.builder(icon).name(name).lore(lore).italic(false).build();
    return SlotDefinition.of(-1, template, click -> click.open(targetMenu));
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    Objects.requireNonNull(menus, "menus");
    var pagination = PaginationConfig.builder().contentSlots(CATEGORY_SLOTS).build();

    MenuFramework.builder(ID, menus)
        .rows(ROWS)
        .title(ComponentUtils.mini(config.value().menuTitle()))
        .pagination(pagination)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(Player player, MenuSession session) {
    return List.of(
        category(
            Material.COMMAND_BLOCK, "<yellow>Servidor", "<gray>Status do servidor.", SERVER_ID),
        category(Material.PLAYER_HEAD, "<yellow>Jogador", "<gray>Suas informações.", PLAYER_ID),
        category(
            Material.ENCHANTED_BOOK, "<yellow>Essentialist", "<gray>Sobre o plugin.", ABOUT_ID));
  }
}
