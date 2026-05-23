package com.hanielcota.essentials.modules.homes.material;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import java.util.function.Predicate;
import lombok.NonNull;
import org.bukkit.Material;

public record HomeMaterialResolver(
    ConfigHandle<HomesConfig> config, Predicate<Material> itemMaterial) {

  // Construtor canônico customizado para incluir validações defensivas automáticas
  public HomeMaterialResolver(
      @NonNull ConfigHandle<HomesConfig> config, @NonNull Predicate<Material> itemMaterial) {
    this.config = config;
    this.itemMaterial = itemMaterial;
  }

  public HomeMaterialResolver(@NonNull ConfigHandle<HomesConfig> config) {
    this(config, Material::isItem);
  }

  public Material resolve(String rawMaterial) {
    if (rawMaterial == null || rawMaterial.isBlank()) {
      return config.value().defaultMaterial();
    }

    var material = Material.matchMaterial(rawMaterial);
    if (material != null && itemMaterial.test(material)) {
      return material;
    }

    return null;
  }
}
