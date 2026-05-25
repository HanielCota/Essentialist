package com.hanielcota.essentials.modules.enchant.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.enchant.config.EnchantConfig;
import com.hanielcota.essentials.modules.enchant.service.EnchantService;
import io.github.hanielcota.commandframework.annotation.*;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

@Command("enchant")
@EssentialsCommand
@Permission("essentials.enchant")
@PlayerOnly
@Cooldown(duration = "3s")
@Description("Encanta o item na mão.")
@Syntax("/enchant <encantamento> [nível] | /enchant remove <encantamento> | /enchant clear")
public record EnchantCommand(ConfigHandle<EnchantConfig> config, EnchantService service) {

  private static String enchantName(@NonNull Enchantment enchantment) {
    return enchantment.getKey().getKey();
  }

  @DefaultSubcommand
  public void apply(
      @NonNull CommandActor sender,
      @Suggestions("enchantments") @Arg("encantamento") Enchantment enchantment,
      @DefaultValue("1") @Arg("nivel") int level) {
    var snap = this.config.value();
    if (level < 1) {
      sender.sendError(snap.invalidLevel());
      return;
    }

    var player = sender.unwrap(Player.class);
    var result = this.service.apply(player, enchantment, level);

    if (result == EnchantService.Result.EMPTY_HAND) {
      sender.sendError(snap.emptyHand());
      return;
    }

    var label = enchantName(enchantment);
    var appliedMsg = snap.formatApplied(label, level);

    sender.sendSuccess(appliedMsg);
  }

  @Subcommand("remove")
  public void remove(
      @NonNull CommandActor sender,
      @Suggestions("enchantments") @Arg("encantamento") Enchantment enchantment) {
    var snap = this.config.value();
    var label = enchantName(enchantment);

    var player = sender.unwrap(Player.class);
    var result = this.service.remove(player, enchantment);

    switch (result) {
      case EMPTY_HAND -> sender.sendError(snap.emptyHand());
      case NOT_ENCHANTED -> {
        var notEnchantedMsg = snap.formatNotEnchanted(label);
        sender.sendError(notEnchantedMsg);
      }
      case REMOVED -> {
        var removedMsg = snap.formatRemoved(label);
        sender.sendSuccess(removedMsg);
      }
      case APPLIED -> throw new IllegalStateException("Unexpected enchant result: APPLIED");
    }
  }

  @Subcommand("clear")
  public void clear(@NonNull CommandActor sender) {
    var snap = this.config.value();
    var player = sender.unwrap(Player.class);
    var removed = this.service.clearAll(player);

    if (removed < 0) {
      sender.sendError(snap.emptyHand());
      return;
    }

    if (removed == 0) {
      sender.sendError(snap.nothingToClear());
      return;
    }

    var clearedMsg = snap.formatCleared(removed);
    sender.sendSuccess(clearedMsg);
  }
}
