package com.hanielcota.essentials.modules.homes.menu.presentation;

import com.github.hanielcota.menuframework.api.ItemRenderer;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.domain.Home;
import lombok.NonNull;

public record HomeEntryRenderer(ConfigHandle<HomesConfig> config) implements ItemRenderer<Home> {

  @Override
  public @NonNull ItemTemplate render(@NonNull Home home, int humanIndex) {
    var menu = config.value().menu();

    var world = home.world();
    var x = home.x();
    var y = home.y();
    var z = home.z();

    var name = menu.formatItemName(home.name());
    var lore = menu.renderItemLore(world, x, y, z);

    return ItemTemplate.builder(home.material())
        .name(name)
        .lore(lore)
        .glow(menu.itemGlow())
        .italic(false)
        .build();
  }
}
