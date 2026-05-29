package com.hanielcota.essentials.modules.kit.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.kit.config.KitConfig;
import com.hanielcota.essentials.modules.kit.config.KitDefinitionConfig;
import com.hanielcota.essentials.modules.kit.domain.KitClaimResult;
import com.hanielcota.essentials.modules.kit.menu.KitCategoryMenu;
import com.hanielcota.essentials.modules.kit.service.KitAdminService;
import com.hanielcota.essentials.modules.kit.service.KitCatalog;
import com.hanielcota.essentials.modules.kit.service.KitClaimService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.Min;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Locale;
import java.util.function.UnaryOperator;
import lombok.NonNull;
import org.bukkit.Material;
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

  @Subcommand("setcooldown")
  @Permission("essentials.kit.admin")
  @Description("Define o cooldown do kit em segundos (0 desativa).")
  @Syntax("/kit setcooldown <kit> <segundos>")
  public CommandResult setCooldown(
      @NonNull CommandActor actor, @Arg("kit") String kit, @Min(0) @Arg("segundos") long seconds) {
    UnaryOperator<KitDefinitionConfig> mutation =
        definition -> definition.withCooldownSeconds(seconds);

    return applyEdit(actor, kit, mutation);
  }

  @Subcommand("setdaily")
  @Permission("essentials.kit.admin")
  @Description("Liga/desliga o reset diário do cooldown do kit.")
  @Syntax("/kit setdaily <kit> <true|false>")
  public CommandResult setDaily(
      @NonNull CommandActor actor, @Arg("kit") String kit, @Arg("valor") boolean value) {
    UnaryOperator<KitDefinitionConfig> mutation = definition -> definition.withDailyReset(value);

    return applyEdit(actor, kit, mutation);
  }

  @Subcommand("setonetime")
  @Permission("essentials.kit.admin")
  @Description("Liga/desliga o modo uso único do kit.")
  @Syntax("/kit setonetime <kit> <true|false>")
  public CommandResult setOneTime(
      @NonNull CommandActor actor, @Arg("kit") String kit, @Arg("valor") boolean value) {
    UnaryOperator<KitDefinitionConfig> mutation = definition -> definition.withOneTime(value);

    return applyEdit(actor, kit, mutation);
  }

  @Subcommand("setpermission")
  @Permission("essentials.kit.admin")
  @Description("Define a permissão do kit ('none' para liberar a todos).")
  @Syntax("/kit setpermission <kit> <permissao>")
  public CommandResult setPermission(
      @NonNull CommandActor actor, @Arg("kit") String kit, @Arg("permissao") String permission) {
    var value = permission.equalsIgnoreCase("none") ? "" : permission;
    UnaryOperator<KitDefinitionConfig> mutation = definition -> definition.withPermission(value);

    return applyEdit(actor, kit, mutation);
  }

  @Subcommand("seticon")
  @Permission("essentials.kit.admin")
  @Description("Define o ícone do kit na lista.")
  @Syntax("/kit seticon <kit> <material>")
  public CommandResult setIcon(
      @NonNull CommandActor actor, @Arg("kit") String kit, @Arg("material") Material material) {
    UnaryOperator<KitDefinitionConfig> mutation = definition -> definition.withIcon(material);

    return applyEdit(actor, kit, mutation);
  }

  @Subcommand("setcategory")
  @Permission("essentials.kit.admin")
  @Description("Define a categoria do kit.")
  @Syntax("/kit setcategory <kit> <categoria>")
  public CommandResult setCategory(
      @NonNull CommandActor actor, @Arg("kit") String kit, @Arg("categoria") String category) {
    UnaryOperator<KitDefinitionConfig> mutation = definition -> definition.withCategory(category);

    return applyEdit(actor, kit, mutation);
  }

  @Subcommand("rename")
  @Permission("essentials.kit.admin")
  @Description("Define o nome de exibição do kit (MiniMessage).")
  @Syntax("/kit rename <kit> <nome>")
  public CommandResult rename(
      @NonNull CommandActor actor, @Arg("kit") String kit, @GreedyString @Arg("nome") String name) {
    UnaryOperator<KitDefinitionConfig> mutation = definition -> definition.withDisplayName(name);

    return applyEdit(actor, kit, mutation);
  }

  private CommandResult applyEdit(
      @NonNull CommandActor actor,
      @NonNull String kit,
      @NonNull UnaryOperator<KitDefinitionConfig> mutation) {
    var messages = this.config.value().messages();

    var edited = this.admin.edit(kit, mutation);
    if (!edited) {
      return CommandResult.invalidUsage(messages.formatUnknownKit(kit));
    }

    var editedMsg = messages.formatEdited(kit);
    actor.sendSuccess(editedMsg);

    return CommandResult.success();
  }
}
