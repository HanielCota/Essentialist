package com.hanielcota.essentials.modules.invsee.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.invsee.config.InvseeConfig;
import com.hanielcota.essentials.modules.invsee.service.InvseeService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("invsee")
@EssentialsCommand
@Permission("essentials.invsee")
@PlayerOnly
@Cooldown(duration = "3s")
@Description("Abre o inventário, a armadura e a off-hand de outro jogador.")
@Syntax("/invsee <jogador>")
public record InvseeCommand(ConfigHandle<InvseeConfig> config, InvseeService service) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor sender, @OnlinePlayer @NonNull Player target) {
    var viewer = sender.unwrap(Player.class);
    var snap = this.config.value();

    if (target.equals(viewer)) {
      var selfMsg = snap.self();
      sender.sendError(selfMsg);
      return;
    }

    var targetName = target.getName();
    var title = snap.formatTitle(targetName);
    var viewOpt = this.service.createView(viewer, target, title);

    if (viewOpt.isEmpty()) {
      var alreadyViewedMsg = snap.alreadyViewed();
      sender.sendError(alreadyViewedMsg);
      return;
    }

    var view = viewOpt.get();
    viewer.openInventory(view);

    var openedMsg = snap.formatOpened(targetName);
    sender.sendSuccess(openedMsg);
  }
}
