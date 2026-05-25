package com.hanielcota.essentials.modules.homes.menu.presentation;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.menu.HomesMainMenuSection;
import com.hanielcota.essentials.modules.homes.domain.Home;
import lombok.NonNull;

public record HomeEntryRenderer(ConfigHandle<HomesConfig> config) {

  public @NonNull ItemTemplate render(@NonNull Home home) {
    var snap = this.config.value();
    var menu = snap.menu();

    var world = home.world();
    var x = home.x();
    var y = home.y();
    var z = home.z();
    var yaw = home.yaw();
    var createdAt = home.createdAt();

    var homeName = home.name();
    var material = home.material();

    var name = HomesMainMenuSection.itemName(menu, homeName);
    var placeholders = HomeMenuPlaceholders.of(world, x, y, z, yaw, createdAt, menu);
    var lore = HomesMainMenuSection.itemLore(menu, placeholders);
    var glow = menu.itemGlow();

    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(lore);
    builder.glow(glow);
    builder.italic(false);

    return builder.build();
  }
}
