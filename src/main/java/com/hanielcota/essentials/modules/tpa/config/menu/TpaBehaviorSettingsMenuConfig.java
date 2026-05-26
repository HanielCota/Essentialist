package com.hanielcota.essentials.modules.tpa.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Behavior settings sub-menu — controls how the viewer's requests auto-respond. Holds the
 * auto-accept-favorites toggle and the DND stage cycle. Opened from {@code TpaSettingsMenu}.
 */
@ConfigSerializable
public record TpaBehaviorSettingsMenuConfig(
    @Comment("Behavior settings menu title.") String title,
    @Comment("Behavior settings menu rows (1-6).") int rows,
    @Comment("Slot of the auto-accept-favorites toggle.") int autoAcceptSlot,
    @Comment("Material used when a toggle is enabled.") Material enabledIcon,
    @Comment("Material used when a toggle is disabled.") Material disabledIcon,
    @Comment("Name of the auto-accept toggle. Placeholder: {state}.") String autoAcceptName,
    @Comment("Lore of the auto-accept toggle. Placeholder: {state}.") List<String> autoAcceptLore,
    @Comment("Slot of the do-not-disturb cycle button.") int dndSlot,
    @Comment("Material of the DND button when DND is off.") Material dndOffIcon,
    @Comment("Material of the DND button when DND is on.") Material dndOnIcon,
    @Comment("Name of the DND button. Placeholder: {state}.") String dndName,
    @Comment(
            "Lore of the DND button. Use {state} for the current label, {remaining} for time left "
                + "and {options} to list every stage with the active one marked.")
        List<String> dndLore,
    @Comment("Label used in {state} when DND is off.") String dndStateOff,
    @Comment("Label used in {state} when DND is for stage 1 (default 30 minutes).")
        String dndStateStage1,
    @Comment("Label used in {state} when DND is for stage 2 (default 1 hour).")
        String dndStateStage2,
    @Comment("Label used in {state} when DND is for stage 3 (default 4 hours).")
        String dndStateStage3,
    @Comment("Suffix appended to the active stage in the {options} expansion.")
        String dndActiveMarker,
    @Comment("Minutes of DND for stage 1. Default 30.") int dndStage1Minutes,
    @Comment("Minutes of DND for stage 2. Default 60.") int dndStage2Minutes,
    @Comment("Minutes of DND for stage 3. Default 240.") int dndStage3Minutes,
    @Comment("DND {remaining} format when less than one minute is left.")
        String dndRemainingUnderMinute,
    @Comment("DND {remaining} format for less than one hour. Placeholder: {minutes}.")
        String dndRemainingMinutes,
    @Comment("DND {remaining} format for whole hours. Placeholder: {hours}.")
        String dndRemainingHours,
    @Comment(
            "DND {remaining} format for hours plus extra minutes. Placeholders: {hours},"
                + " {minutes}.")
        String dndRemainingHoursMinutes,
    @Comment("DND {remaining} fallback shown when DND is off.") String dndRemainingFallback,
    @Comment("Slot of the back item.") int backSlot,
    @Comment("Material of the back item.") Material backIcon,
    @Comment("Name of the back item.") String backName,
    @Comment("Lore of the back item.") List<String> backLore,
    @Comment("Label used when a toggle is enabled.") String enabledLabel,
    @Comment("Label used when a toggle is disabled.") String disabledLabel) {

  public static TpaBehaviorSettingsMenuConfig defaults() {
    return new TpaBehaviorSettingsMenuConfig(
        "Comportamento",
        3,
        10,
        Material.LIME_DYE,
        Material.GRAY_DYE,
        "<yellow>Auto-aceitar favoritos: {state}",
        List.of(
            "<gray>Pedidos dos seus favoritos",
            "<gray>são aceitos sem perguntar.",
            "",
            "<yellow>Clique para alternar."),
        12,
        Material.RED_DYE,
        Material.GRAY_DYE,
        "<yellow>Não perturbe: {state}",
        List.of(
            "<gray>Enquanto ativo, seus pedidos",
            "<gray>de TPA são recusados em silêncio.",
            "<gray>Tempo restante: <white>{remaining}",
            "",
            "{options}",
            "",
            "<yellow>Clique para mudar."),
        "<green>desligado",
        "30 minutos",
        "1 hora",
        "4 horas",
        " <gold>◀",
        30,
        60,
        240,
        "<1m",
        "{minutes}m",
        "{hours}h",
        "{hours}h{minutes}m",
        "<gray>—",
        22,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna às configurações."),
        "<green>ligado",
        "<red>desligado");
  }
}
