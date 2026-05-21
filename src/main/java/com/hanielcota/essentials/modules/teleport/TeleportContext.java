package com.hanielcota.essentials.modules.teleport;

import com.hanielcota.essentials.command.Commands;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.teleport.config.TeleportMessages;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.Objects;
import org.bukkit.entity.Player;

public record TeleportContext(
    ConfigHandle<TeleportMessages> config, PaperCommandFramework framework) {

  public TeleportContext {
    Objects.requireNonNull(config, "config");
    Objects.requireNonNull(framework, "framework");
  }

  public TeleportMessages snapshot() {
    return config.value();
  }

  public void success(Player player, String message) {
    framework.actorOf(player).sendSuccess(message);
  }

  public void error(Player player, String message) {
    framework.actorOf(player).sendError(message);
  }

  public boolean isSelf(Player a, Player b) {
    return Commands.isSelf(framework.actorOf(a), b);
  }

  public void notifyTarget(Player sender, Player target, String senderMsg, String targetMsg) {
    framework.actorOf(sender).sendDualMessage(framework.actorOf(target), senderMsg, targetMsg);
  }
}
