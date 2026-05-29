package com.hanielcota.essentials.modules.kit.config;

import com.hanielcota.essentials.shared.Placeholders;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Every chat line the kit module can send. */
@ConfigSerializable
public record KitMessages(
    @Comment("Shown after a kit is claimed. Placeholders: {kit}.") String claimed,
    @Comment("Shown when the kit is on cooldown. Placeholders: {kit}, {time}.") String onCooldown,
    @Comment("Shown when a one-time kit was already claimed. Placeholders: {kit}.")
        String alreadyClaimed,
    @Comment("Shown when the player lacks the kit permission. Placeholders: {kit}.")
        String noPermission,
    @Comment("Shown when the kit has no items. Placeholders: {kit}.") String empty,
    @Comment("Shown when the kit does not exist. Placeholders: {kit}.") String unknownKit,
    @Comment("Shown when items overflowed and were dropped on the ground.") String inventoryFull,
    @Comment("Shown to the admin after /kit create. Placeholders: {kit}, {count}.") String created,
    @Comment("Shown when /kit create is run with an empty inventory.") String createEmpty,
    @Comment("Shown to the admin after /kit delete. Placeholders: {kit}.") String deleted,
    @Comment("Shown after /kit reload. Placeholders: {count}.") String reloaded,
    @Comment("Shown when the console runs /kit (the menu needs a player).") String menuPlayerOnly,
    @Comment("Shown when the inventory has no room and overflow dropping is disabled.")
        String inventoryNoSpace,
    @Comment("Shown to the giver after /kit give. Placeholders: {kit}, {player}.") String gave,
    @Comment("Shown to the giver when /kit give could not deliver. Placeholders: {player}.")
        String giveFailed) {

  public static KitMessages defaults() {
    return new KitMessages(
        "<green>You claimed the kit <gold>{kit}</gold>.",
        "<red>Kit <gold>{kit}</gold> is on cooldown — <yellow>{time}</yellow> left.",
        "<red>You already claimed the one-time kit <gold>{kit}</gold>.",
        "<red>You do not have access to the kit <gold>{kit}</gold>.",
        "<red>Kit <gold>{kit}</gold> has no items.",
        "<red>Kit <gold>{kit}</gold> does not exist.",
        "<yellow>Your inventory was full — the extra items were dropped at your feet.",
        "<green>Kit <gold>{kit}</gold> saved with <gold>{count}</gold> item(s).",
        "<red>Your inventory is empty — there is nothing to save.",
        "<yellow>Kit <gold>{kit}</gold> deleted.",
        "<green>Reloaded <gold>{count}</gold> kit(s).",
        "<red>The kit menu can only be opened by players.",
        "<red>Your inventory is full — free some space and try again.",
        "<green>Gave the kit <gold>{kit}</gold> to <gold>{player}</gold>.",
        "<red>Could not give the kit to <gold>{player}</gold> — their inventory is full.");
  }

  public String formatClaimed(@NonNull String kit) {
    return claimed.replace("{kit}", kit);
  }

  public String formatOnCooldown(@NonNull String kit, @NonNull String time) {
    return Placeholders.format(onCooldown, "kit", kit, "time", time);
  }

  public String formatAlreadyClaimed(@NonNull String kit) {
    return alreadyClaimed.replace("{kit}", kit);
  }

  public String formatNoPermission(@NonNull String kit) {
    return noPermission.replace("{kit}", kit);
  }

  public String formatEmpty(@NonNull String kit) {
    return empty.replace("{kit}", kit);
  }

  public String formatUnknownKit(@NonNull String kit) {
    return unknownKit.replace("{kit}", kit);
  }

  public String formatCreated(@NonNull String kit, int count) {
    return Placeholders.format(created, "kit", kit, "count", count);
  }

  public String formatDeleted(@NonNull String kit) {
    return deleted.replace("{kit}", kit);
  }

  public String formatReloaded(int count) {
    return reloaded.replace("{count}", String.valueOf(count));
  }

  public String formatGave(@NonNull String kit, @NonNull String player) {
    return Placeholders.format(gave, "kit", kit, "player", player);
  }

  public String formatGiveFailed(@NonNull String player) {
    return giveFailed.replace("{player}", player);
  }
}
