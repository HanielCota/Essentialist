package com.hanielcota.essentials.modules.tpa.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.TpaHistory;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenuState;
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
import lombok.NonNull;
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
    TpaHistoryMenuState state,
    PlayerProvider players) {

  private static final String OTHERS_PERMISSION = "essentials.tpa.history.others";

  @DefaultSubcommand
  public void execute(
      @NonNull CommandActor actor, @DefaultValue("") @Arg("jogador") String targetName) {
    var sender = actor.unwrap(Player.class);
    var snap = this.config.value();
    var messages = snap.messages();

    if (targetName.isEmpty()) {
      var senderId = sender.getUniqueId();
      var senderName = sender.getName();
      openFor(actor, sender, senderId, /* self */ true, senderName);
      return;
    }

    if (!actor.hasPermission(OTHERS_PERMISSION)) {
      actor.sendError(messages.noPermissionOther());
      return;
    }

    var resolved = this.players.offlineByName(targetName);
    if (resolved.isEmpty()) {
      var notFoundTemplate = messages.playerNotFound();
      var notFoundMsg = notFoundTemplate.replace("{player}", targetName);
      actor.sendError(notFoundMsg);
      return;
    }

    var target = resolved.get();
    var targetId = target.getUniqueId();

    var resolvedRawName = target.getName();
    var resolvedName = resolvedRawName != null ? resolvedRawName : targetName;

    openFor(actor, sender, targetId, /* self */ false, resolvedName);
  }

  private void openFor(
      @NonNull CommandActor actor,
      @NonNull Player viewer,
      @NonNull UUID subject,
      boolean self,
      @NonNull String subjectName) {
    var snap = this.config.value();
    var messages = snap.messages();

    var entries = this.history.list(subject);
    if (entries.isEmpty()) {
      var selfTemplate = messages.noHistory();
      var otherTemplate = messages.noHistoryOther();

      var emptyMsg = renderEmptyMessage(self, subjectName, selfTemplate, otherTemplate);
      actor.sendError(emptyMsg);
      return;
    }

    if (!self) {
      var viewingTemplate = messages.viewingOther();
      var viewingMsg = viewingTemplate.replace("{player}", subjectName);
      var viewingComponent = ComponentUtils.mini(viewingMsg);
      actor.sendMessage(viewingComponent);
    }

    var viewerId = viewer.getUniqueId();
    this.state.prefetch(viewerId, entries);

    MenuOpenings.open(this.menus, viewer, TpaHistoryMenu.ID, actor);
  }

  private static String renderEmptyMessage(
      boolean self,
      @NonNull String subjectName,
      @NonNull String selfTemplate,
      @NonNull String otherTemplate) {
    if (self) {
      return selfTemplate;
    }
    return otherTemplate.replace("{player}", subjectName);
  }
}
