package com.hanielcota.essentials.modules.essentials.config;

import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record ModulesMenuConfig(
    @Comment("Inventory title of the module-control menu.") String title,
    @Comment("Menu height in rows (3-6).") int rows,
    @Comment("Slots (0-based) the module items are placed in, in order.")
        List<Integer> contentSlots,
    @Comment("Icon for an enabled module.") Material enabledMaterial,
    @Comment("Icon for a disabled module.") Material disabledMaterial,
    @Comment("Item name of an enabled module. Placeholder: {module}.") String enabledName,
    @Comment("Item name of a disabled module. Placeholder: {module}.") String disabledName,
    @Comment("Lore lines of an enabled module.") List<String> enabledLore,
    @Comment("Lore lines of a disabled module.") List<String> disabledLore,
    @Comment("Extra lore line appended when a change is awaiting a restart.") String pendingLine,
    @Comment("Chat feedback when a module is switched on. Placeholder: {module}.") String toggledOn,
    @Comment("Chat feedback when a module is switched off. Placeholder: {module}.")
        String toggledOff,
    @Comment("The 'how it works' guide item.") ModulesInfoConfig info,
    @Comment("Category filter button (cycles the shown category).") ModulesFilterConfig filter,
    @Comment("Previous/next page buttons (used when a category overflows the content slots).")
        NavigationButtonsConfig navigation) {

  // The menu needs content rows plus a bottom control row (filter + page buttons), so anything
  // below 3 cannot lay out coherently — such values fall back to the default height.
  private static final int MIN_ROWS = 3;
  private static final int MAX_ROWS = 6;
  private static final int DEFAULT_ROWS = 6;
  private static final int SLOTS_PER_ROW = 9;
  private static final int DEFAULT_INFO_SLOT = 4;

  // Inner grid of a 6-row chest (cols 2-8 of rows 2-5), so the border frames the content.
  private static final List<Integer> DEFAULT_CONTENT_SLOTS =
      List.of(
          10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
          38, 39, 40, 41, 42, 43);

  public static ModulesMenuConfig defaults() {
    return new ModulesMenuConfig(
        "Modules",
        DEFAULT_ROWS,
        DEFAULT_CONTENT_SLOTS,
        Material.LIME_DYE,
        Material.GRAY_DYE,
        "<green>{module}",
        "<red>{module}",
        List.of(
            "<dark_gray>▪ <gray>Status: <green>● Enabled",
            "",
            "<yellow>➜ <gray>Click to <red>disable<gray>."),
        List.of(
            "<dark_gray>▪ <gray>Status: <red>● Disabled",
            "",
            "<yellow>➜ <gray>Click to <green>enable<gray>."),
        "<gold>⚠ <gray>Applies on the next restart.",
        "<green>Module <gold>{module}</gold> will be enabled on the next restart.",
        "<red>Module <gold>{module}</gold> will be disabled on the next restart.",
        ModulesInfoConfig.defaults(),
        ModulesFilterConfig.defaults(),
        NavigationButtonsConfig.defaults(48, 50));
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

  public List<Integer> effectiveContentSlots() {
    var rows = effectiveRows();

    return MenuLayouts.sanitizeSlots(this.contentSlots, rows);
  }

  public int effectiveInfoSlot(int rows) {
    var slot = this.info.slot();

    return MenuLayouts.sanitizeSlot(slot, rows, DEFAULT_INFO_SLOT);
  }

  public int effectiveFilterSlot(int rows) {
    var slot = this.filter.slot();
    var fallback = (rows - 1) * SLOTS_PER_ROW;

    return MenuLayouts.sanitizeSlot(slot, rows, fallback);
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
