package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.api.ItemRenderer;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.service.Home;
import org.jspecify.annotations.NonNull;

public record HomeEntryRenderer(ConfigHandle<HomesConfig> config) implements ItemRenderer<Home> {

  @Override
  public @NonNull ItemTemplate render(@NonNull Home home, int humanIndex) {
    var menu = config.value().menu();
    var lore = menu.renderItemLore(home.world(), home.x(), home.y(), home.z());

    return ItemTemplate.builder(home.material())
        .name(menu.formatItemName(home.name()))
        .lore(lore)
        .glow(menu.itemGlow())
        .italic(false)
        .build();
  }
}
