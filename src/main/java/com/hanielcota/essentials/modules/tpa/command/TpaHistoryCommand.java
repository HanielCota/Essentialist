package com.hanielcota.essentials.modules.tpa.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.TpaHistory;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenu;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.util.ComponentUtils;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.UUID;
import org.bukkit.entity.Player;

@Command("tpahistory")
@EssentialsCommand
@Permission("essentials.tpa.history")
@Cooldown(duration = "3s")
@Description("Abre o histórico dos últimos pedidos de teleporte enviados.")
@Syntax("/tpahistory [jogador]")
public record TpaHistoryCommand(
    ConfigHandle<TpaConfig> config,
    TpaHistory history,
    MenuService menus,
    TpaHistoryMenu menu,
    PlayerProvider players) {

  private static final String OTHERS_PERMISSION = "essentials.tpa.history.others";

  @DefaultSubcommand
  public void execute(CommandActor actor, @DefaultValue("") @Arg("jogador") String targetName) {
    var sender = actor.unwrap(Player.class);
    var snap = config.value();

    if (targetName.isEmpty()) {
      openFor(actor, sender, sender.getUniqueId(), /* self */ true, sender.getName());
      return;
    }

    if (!actor.hasPermission(OTHERS_PERMISSION)) {
      actor.sendError(snap.messages().noPermissionOther());
      return;
    }

    var resolved = players.offlineByName(targetName);
    if (resolved.isEmpty()) {
      actor.sendError(snap.messages().playerNotFound().replace("{player}", targetName));
      return;
    }

    var target = resolved.get();
    var resolvedName = target.getName() != null ? target.getName() : targetName;
    openFor(actor, sender, target.getUniqueId(), /* self */ false, resolvedName);
  }

  private void openFor(
      CommandActor actor, Player viewer, UUID subject, boolean self, String subjectName) {
    var snap = config.value();
    var entries = history.list(subject);

    if (entries.isEmpty()) {
      actor.sendError(
          self
              ? snap.messages().noHistory()
              : snap.messages().noHistoryOther().replace("{player}", subjectName));
      return;
    }

    if (!self) {
      actor.sendMessage(
          ComponentUtils.mini(
              snap.messages().viewingOther().replace("{player}", subjectName)));
    }

    menu.prefetch(viewer.getUniqueId(), entries);
    menus.open(viewer, TpaHistoryMenu.ID);
  }
}
