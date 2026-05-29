package com.hanielcota.essentials.modules.kit.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.kit.config.KitConfig;
import com.hanielcota.essentials.modules.kit.domain.KitClaimResult;
import com.hanielcota.essentials.modules.kit.menu.KitCategoryMenu;
import com.hanielcota.essentials.modules.kit.service.KitAdminService;
import com.hanielcota.essentials.modules.kit.service.KitCatalog;
import com.hanielcota.essentials.modules.kit.service.KitClaimService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Locale;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("kit")
@Permission("essentials.kit")
@Description("Abre o menu de kits.")
@Syntax("/kit | /kit give <jogador> <kit> | /kit create <nome> | /kit delete <nome> | /kit reload")
public record KitCommand(
    ConfigHandle<KitConfig> config,
    MenuService menus,
    KitAdminService admin,
    KitCatalog catalog,
    KitClaimService claimService,
    KitClaimNotifier notifier) {

  @DefaultSubcommand
  public CommandResult open(@NonNull CommandActor actor) {
    var messages = this.config.value().messages();

    if (!actor.isPlayer()) {
      actor.sendMessage(messages.menuPlayerOnly());
      return CommandResult.success();
    }

    var player = actor.unwrap(Player.class);
    MenuOpenings.open(this.menus, player, KitCategoryMenu.ID, actor);

    return CommandResult.success();
  }

  @Subcommand("give")
  @Permission("essentials.kit.admin")
  @Description("Entrega um kit a um jogador (ignora cooldown e permissão).")
  @Syntax("/kit give <jogador> <kit>")
  public CommandResult give(
      @NonNull CommandActor actor,
      @OnlinePlayer @NonNull Player target,
      @Arg("kit") String kitName) {
    var messages = this.config.value().messages();
    var id = kitName.toLowerCase(Locale.ROOT);

    var kit = this.catalog.find(id);
    if (kit.isEmpty()) {
      return CommandResult.invalidUsage(messages.formatUnknownKit(kitName));
    }

    var outcome = this.claimService.deliver(target, kit.get());
    var targetName = target.getName();

    if (outcome.result() != KitClaimResult.CLAIMED) {
      var failMsg = messages.formatGiveFailed(targetName);
      return CommandResult.invalidUsage(failMsg);
    }

    this.notifier.notify(target, kit.get(), outcome);

    var gaveMsg = messages.formatGave(kit.get().displayName(), targetName);
    actor.sendSuccess(gaveMsg);

    return CommandResult.success();
  }

  @Subcommand("create")
  @Permission("essentials.kit.admin")
  @PlayerOnly
  @Description("Cria ou atualiza um kit com o conteúdo do seu inventário.")
  @Syntax("/kit create <nome>")
  public CommandResult create(@NonNull CommandActor actor, @Arg("nome") String name) {
    var player = actor.unwrap(Player.class);
    var messages = this.config.value().messages();

    var count = this.admin.create(player, name);
    if (count == 0) {
      return CommandResult.invalidUsage(messages.createEmpty());
    }

    var createdMsg = messages.formatCreated(name, count);
    actor.sendSuccess(createdMsg);

    return CommandResult.success();
  }

  @Subcommand("delete")
  @Permission("essentials.kit.admin")
  @Description("Remove um kit.")
  @Syntax("/kit delete <nome>")
  public CommandResult delete(@NonNull CommandActor actor, @Arg("nome") String name) {
    var messages = this.config.value().messages();

    var removed = this.admin.delete(name);
    if (!removed) {
      var unknownMsg = messages.formatUnknownKit(name);
      return CommandResult.invalidUsage(unknownMsg);
    }

    var deletedMsg = messages.formatDeleted(name);
    actor.sendSuccess(deletedMsg);

    return CommandResult.success();
  }

  @Subcommand("reload")
  @Permission("essentials.kit.admin")
  @Description("Recarrega as definições de kit do disco.")
  @Syntax("/kit reload")
  public CommandResult reload(@NonNull CommandActor actor) {
    var messages = this.config.value().messages();

    var count = this.admin.reload();
    var reloadedMsg = messages.formatReloaded(count);

    actor.sendSuccess(reloadedMsg);
    return CommandResult.success();
  }
}
