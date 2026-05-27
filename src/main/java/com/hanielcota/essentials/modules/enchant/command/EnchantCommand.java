package com.hanielcota.essentials.modules.enchant.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.enchant.config.EnchantConfig;
import com.hanielcota.essentials.modules.enchant.service.EnchantService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Suggestions;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
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

  @DefaultSubcommand
  public CommandResult apply(
      @NonNull CommandActor sender,
      @Suggestions("enchantments") @Arg("encantamento") Enchantment enchantment,
      @DefaultValue("1") @Arg("nivel") int level) {
    var snap = this.config.value();
    if (level < 1) {
      return CommandResult.invalidUsage(snap.invalidLevel());
    }

    var player = sender.unwrap(Player.class);
    var result = this.service.apply(player, enchantment, level);

    if (result == EnchantService.ApplyResult.EMPTY_HAND) {
      return CommandResult.invalidUsage(snap.emptyHand());
    }

    var label = enchantment.getKey().getKey();
    var appliedMsg = snap.formatApplied(label, level);

    sender.sendSuccess(appliedMsg);
    return CommandResult.success();
  }

  @Subcommand("remove")
  public CommandResult remove(
      @NonNull CommandActor sender,
      @Suggestions("enchantments") @Arg("encantamento") Enchantment enchantment) {
    var snap = this.config.value();
    var label = enchantment.getKey().getKey();

    var player = sender.unwrap(Player.class);
    var result = this.service.remove(player, enchantment);

    return switch (result) {
      case EMPTY_HAND -> CommandResult.invalidUsage(snap.emptyHand());
      case NOT_ENCHANTED -> {
        var notEnchantedMsg = snap.formatNotEnchanted(label);
        yield CommandResult.invalidUsage(notEnchantedMsg);
      }
      case REMOVED -> {
        var removedMsg = snap.formatRemoved(label);
        sender.sendSuccess(removedMsg);
        yield CommandResult.success();
      }
    };
  }

  @Subcommand("clear")
  public CommandResult clear(@NonNull CommandActor sender) {
    var snap = this.config.value();
    var player = sender.unwrap(Player.class);
    var removed = this.service.clearAll(player);

    if (removed < 0) {
      return CommandResult.invalidUsage(snap.emptyHand());
    }

    if (removed == 0) {
      return CommandResult.invalidUsage(snap.nothingToClear());
    }

    var clearedMsg = snap.formatCleared(removed);
    sender.sendSuccess(clearedMsg);
    return CommandResult.success();
  }
}
