package com.hanielcota.essentials.modules.essentials.config;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record ModulesMenuConfig(
    @Comment("Inventory title of the module-control menu.") String title,
    @Comment("Menu height in rows (1-6). The bottom row holds the category tabs.") int rows,
    @Comment("Icon for an enabled module.") Material enabledMaterial,
    @Comment("Icon for a disabled module.") Material disabledMaterial,
    @Comment("Item name of an enabled module. Placeholder: {module}.") String enabledName,
    @Comment("Item name of a disabled module. Placeholder: {module}.") String disabledName,
    @Comment("Lore lines of an enabled module.") List<String> enabledLore,
    @Comment("Lore lines of a disabled module.") List<String> disabledLore,
    @Comment("Extra lore line appended when a change is awaiting a restart.") String pendingLine,
    @Comment("Chat feedback when a module is switched on. Placeholder: {module}.") String toggledOn,
    @Comment("Chat feedback when a module is switched off. Placeholder: {module}.")
        String toggledOff) {

  private static final int MIN_ROWS = 1;
  private static final int MAX_ROWS = 6;
  private static final int DEFAULT_ROWS = 6;

  public static ModulesMenuConfig defaults() {
    return new ModulesMenuConfig(
        "Módulos",
        DEFAULT_ROWS,
        Material.LIME_DYE,
        Material.GRAY_DYE,
        "<green>{module}",
        "<red>{module}",
        List.of("<gray>Estado: <green>Ativado", "<yellow>Clique para desativar."),
        List.of("<gray>Estado: <red>Desativado", "<yellow>Clique para ativar."),
        "<gold>⟳ Aplica no próximo restart.",
        "<green>Módulo <gold>{module}</gold> será ativado no próximo restart.",
        "<red>Módulo <gold>{module}</gold> será desativado no próximo restart.");
  }

  public int effectiveRows() {
    if (this.rows < MIN_ROWS) {
      return DEFAULT_ROWS;
    }
    if (this.rows > MAX_ROWS) {
      return MAX_ROWS;
    }

    return this.rows;
  }

  public Material material(boolean enabled) {
    return enabled ? this.enabledMaterial : this.disabledMaterial;
  }

  public String name(boolean enabled, @NonNull String moduleId) {
    var template = enabled ? this.enabledName : this.disabledName;

    return template.replace("{module}", moduleId);
  }

  public List<String> lore(boolean enabled, boolean pending) {
    var base = enabled ? this.enabledLore : this.disabledLore;
    var lines = new ArrayList<>(base);

    if (pending) {
      lines.add(this.pendingLine);
    }

    return lines;
  }

  public String toggleFeedback(boolean nowEnabled, @NonNull String moduleId) {
    var template = nowEnabled ? this.toggledOn : this.toggledOff;

    return template.replace("{module}", moduleId);
  }
}
