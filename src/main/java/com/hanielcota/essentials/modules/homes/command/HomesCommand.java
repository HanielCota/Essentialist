package com.hanielcota.essentials.modules.homes.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.service.Home;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.util.ClickableMessage;
import com.hanielcota.essentials.util.Numbers;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command("homes")
@EssentialsCommand
@Permission("essentials.home.list")
@Cooldown(duration = "3s")
@Description("Lista suas homes, clicáveis para teleporte.")
@Syntax("/homes")
public record HomesCommand(ConfigHandle<HomesConfig> config, HomeService service) {

  @DefaultSubcommand
  public void execute(CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var snap = config.value();
    var messages = snap.messages();

    var homes = service.list(sender.getUniqueId());
    if (homes.isEmpty()) {
      actor.sendError(messages.noHomes());
      return;
    }

    var limit = service.limit(sender);
    var message = ClickableMessage.create();
    message.append(
        messages
            .listHeader()
            .replace("{count}", Integer.toString(homes.size()))
            .replace("{limit}", Integer.toString(limit)));

    for (var home : homes) {
      message
          .newline()
          .append(
              renderEntry(home, messages.listEntry()),
              slot ->
                  slot.runCommand("/home " + home.name())
                      .hover(messages.listEntryHover().replace("{name}", home.name())));
    }
    message.send(sender);
  }

  private static String renderEntry(Home home, String template) {
    return template
        .replace("{name}", home.name())
        .replace("{world}", home.world())
        .replace("{x}", Numbers.compact(home.x()))
        .replace("{y}", Numbers.compact(home.y()))
        .replace("{z}", Numbers.compact(home.z()));
  }
}
