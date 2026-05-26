package com.hanielcota.essentials.modules.homes.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import java.util.function.Predicate;
import lombok.NonNull;
import org.bukkit.Material;

public record HomeMaterialResolver(
    @NonNull ConfigHandle<HomesConfig> config, @NonNull Predicate<Material> itemMaterial) {

  public HomeMaterialResolver(@NonNull ConfigHandle<HomesConfig> config) {
    this(config, Material::isItem);
  }

  public Material resolve(@NonNull String rawMaterial) {
    if (rawMaterial.isBlank()) {
      var snap = this.config.value();
      return snap.defaultMaterial();
    }

    var material = Material.matchMaterial(rawMaterial);

    if (material == null) {
      return null;
    }

    if (!this.itemMaterial.test(material)) {
      return null;
    }

    return material;
  }
}
