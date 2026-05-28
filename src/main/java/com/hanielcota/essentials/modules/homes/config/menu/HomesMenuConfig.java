package com.hanielcota.essentials.modules.homes.config.menu;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record HomesMenuConfig(
    @Comment("Main /homes menu settings.") HomesMainMenuConfig main,
    @Comment("Material category submenu settings.") HomesCategoryMenuConfig category,
    @Comment("Material picker submenu settings.") HomesPickerMenuConfig picker,
    @Comment("Delete-confirmation dialog settings.") HomesDeleteDialogConfig deleteDialog,
    @Comment("Right-click options submenu settings.") HomesOptionsMenuConfig options) {

  public static HomesMenuConfig defaults() {
    return new HomesMenuConfig(
        HomesMainMenuConfig.defaults(),
        HomesCategoryMenuConfig.defaults(),
        HomesPickerMenuConfig.defaults(),
        HomesDeleteDialogConfig.defaults(),
        HomesOptionsMenuConfig.defaults());
  }
}
