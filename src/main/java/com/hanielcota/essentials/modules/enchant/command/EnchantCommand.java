package com.hanielcota.essentials.modules.enchant.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.enchant.config.EnchantConfig;
import com.hanielcota.essentials.modules.enchant.service.EnchantService;
import io.github.hanielcota.commandframework.annotation.*;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.Objects;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

@Command("enchant")
@EssentialsCommand
@Permission("essentials.enchant")
@Cooldown(duration = "3s")
@Description("Encanta o item na mão.")
@Syntax("/enchant <encantamento> [nível] | /enchant remove <encantamento> | /enchant clear")
public record EnchantCommand(ConfigHandle<EnchantConfig> config, EnchantService service) {

  private static String enchantName(Enchantment enchantment) {
    return enchantment.getKey().getKey();
  }

  @DefaultSubcommand
  public void apply(
      CommandActor sender,
      @Suggestions("enchantments") @Arg("encantamento") Enchantment enchantment,
      @DefaultValue("1") @Arg("nivel") int level) {
    Objects.requireNonNull(sender, "sender");
    Objects.requireNonNull(enchantment, "enchantment");

    if (!sender.isPlayer()) {
      sender.sendError("<red>Este comando só pode ser executado por jogadores.");
      return;
    }

    var snap = config.value();
    if (level < 1) {
      sender.sendError(snap.invalidLevel());
      return;
    }

    var player = sender.unwrap(Player.class);
    var result = service.apply(player, enchantment, level);

    if (result == EnchantService.Result.EMPTY_HAND) {
      sender.sendError(snap.emptyHand());
      return;
    }

    sender.sendSuccess(snap.formatApplied(enchantName(enchantment), level));
  }

  @Subcommand("remove")
  public void remove(
      CommandActor sender,
      @Suggestions("enchantments") @Arg("encantamento") Enchantment enchantment) {
    Objects.requireNonNull(sender, "sender");
    Objects.requireNonNull(enchantment, "enchantment");

    if (!sender.isPlayer()) {
      sender.sendError("<red>Este comando só pode ser executado por jogadores.");
      return;
    }

    var snap = config.value();
    var label = enchantName(enchantment);

    var player = sender.unwrap(Player.class);
    var result = service.remove(player, enchantment);

    switch (result) {
      case EMPTY_HAND -> sender.sendError(snap.emptyHand());
      case NOT_ENCHANTED -> sender.sendError(snap.formatNotEnchanted(label));
      case REMOVED -> sender.sendSuccess(snap.formatRemoved(label));
      default -> throw new IllegalStateException("Unexpected enchant result: " + result);
    }
  }

  @Subcommand("clear")
  public void clear(CommandActor sender) {
    Objects.requireNonNull(sender, "sender");

    if (!sender.isPlayer()) {
      sender.sendError("<red>Este comando só pode ser executado por jogadores.");
      return;
    }

    var snap = config.value();
    var player = sender.unwrap(Player.class);
    var removed = service.clearAll(player);

    if (removed < 0) {
      sender.sendError(snap.emptyHand());
      return;
    }

    if (removed == 0) {
      sender.sendError(snap.nothingToClear());
      return;
    }

    sender.sendSuccess(snap.formatCleared(removed));
  }
}
