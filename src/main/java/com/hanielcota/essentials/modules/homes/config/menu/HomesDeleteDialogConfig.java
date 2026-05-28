package com.hanielcota.essentials.modules.homes.config.menu;

import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record HomesDeleteDialogConfig(
    @Comment("Delete-confirmation menu rows (1-6).") int rows,
    @Comment("Slot of the delete-confirmation prompt item.") int promptSlot,
    @Comment("Material of the delete-confirmation prompt item.") Material promptMaterial,
    @Comment("Slot of the delete-confirmation yes button.") int yesSlot,
    @Comment("Material of the delete-confirmation yes button.") Material yesMaterial,
    @Comment("Slot of the delete-confirmation no button.") int noSlot,
    @Comment("Material of the delete-confirmation no button.") Material noMaterial) {

  public static HomesDeleteDialogConfig defaults() {
    return new HomesDeleteDialogConfig(
        3, 13, Material.PAPER, 11, Material.LIME_WOOL, 15, Material.RED_WOOL);
  }
}
