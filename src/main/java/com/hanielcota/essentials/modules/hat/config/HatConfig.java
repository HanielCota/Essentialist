package com.hanielcota.essentials.modules.hat.config;

import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record HatConfig(
    @Comment("Shown when a hat is equipped.") String equipped,
    @Comment("Shown when the player is not holding any item.") String emptyHand,
    @Comment("Shown when the held item is not in the whitelist.") String notAllowed,
    @Comment(
            "Shown when the swap would drop the previous helmet because the inventory is full"
                + " — the swap is aborted so the previous helmet is never lost.")
        String inventoryFull,
    @Comment(
            "Materials that can be equipped as a hat. Leave empty to allow every material"
                + " (back-compat). Defaults to vanilla helmets + mob/player heads + carved"
                + " pumpkin.")
        List<Material> materialWhitelist) {

  public static HatConfig defaults() {
    return new HatConfig(
        "<green>Chapéu equipado.",
        "<red>Você precisa estar segurando um item para usar como chapéu.",
        "<red>Esse item não pode ser usado como chapéu.",
        "<red>Sem espaço no inventário para o chapéu anterior.",
        List.of(
            Material.LEATHER_HELMET,
            Material.CHAINMAIL_HELMET,
            Material.IRON_HELMET,
            Material.GOLDEN_HELMET,
            Material.DIAMOND_HELMET,
            Material.NETHERITE_HELMET,
            Material.TURTLE_HELMET,
            Material.PLAYER_HEAD,
            Material.ZOMBIE_HEAD,
            Material.SKELETON_SKULL,
            Material.WITHER_SKELETON_SKULL,
            Material.CREEPER_HEAD,
            Material.DRAGON_HEAD,
            Material.PIGLIN_HEAD,
            Material.CARVED_PUMPKIN));
  }

  /** Empty whitelist means "allow anything"; otherwise the material must appear in the list. */
  public boolean isAllowed(@NonNull Material material) {
    return this.materialWhitelist.isEmpty() || this.materialWhitelist.contains(material);
  }
}
