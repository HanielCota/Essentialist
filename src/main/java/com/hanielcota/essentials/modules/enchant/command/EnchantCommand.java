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
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
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

  @DefaultSubcommand
  public void apply(
      CommandActor sender,
      @Arg("encantamento") Enchantment enchantment,
      @DefaultValue("1") @Arg("nivel") int level) {
    Objects.requireNonNull(sender, "sender");
    Objects.requireNonNull(enchantment, "enchantment");

    var snap = config.value();
    if (level < 1) {
      sender.sendError(snap.invalidLevel());
      return;
    }

    var result = service.apply(sender.unwrap(Player.class), enchantment, level);
    if (result == EnchantService.Result.EMPTY_HAND) {
      sender.sendError(snap.emptyHand());
      return;
    }
    sender.sendSuccess(snap.formatApplied(enchantName(enchantment), level));
  }

  @Subcommand("remove")
  public void remove(CommandActor sender, @Arg("encantamento") Enchantment enchantment) {
    Objects.requireNonNull(sender, "sender");
    Objects.requireNonNull(enchantment, "enchantment");

    var snap = config.value();
    String label = enchantName(enchantment);

    switch (service.remove(sender.unwrap(Player.class), enchantment)) {
      case EMPTY_HAND -> sender.sendError(snap.emptyHand());
      case NOT_ENCHANTED -> sender.sendError(snap.formatNotEnchanted(label));
      case REMOVED -> sender.sendSuccess(snap.formatRemoved(label));
      default -> throw new IllegalStateException("Unexpected enchant result");
    }
  }

  @Subcommand("clear")
  public void clear(CommandActor sender) {
    Objects.requireNonNull(sender, "sender");

    var snap = config.value();
    int removed = service.clearAll(sender.unwrap(Player.class));
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

  private static String enchantName(Enchantment enchantment) {
    return enchantment.getKey().getKey();
  }
}
