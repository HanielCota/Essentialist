package com.hanielcota.essentials.modules.homes.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.HomesMessages;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Command("sethome")
@EssentialsCommand
@Permission("essentials.home.set")
@Cooldown(duration = "2s")
@Description("Define uma home com o nome dado (ou \"home\" se ausente).")
@Syntax("/sethome [nome] [material]")
public record SetHomeCommand(ConfigHandle<HomesConfig> config, HomeService service) {

  @DefaultSubcommand
  public void execute(
      CommandActor actor,
      @DefaultValue("") @Arg("nome") String rawName,
      @DefaultValue("") @Arg("material") String rawMaterial) {
    var sender = actor.unwrap(Player.class);
    var snap = config.value();
    var messages = snap.messages();
    var name = rawName.isBlank() ? snap.defaultHomeName() : rawName;

    var material = resolveMaterial(rawMaterial, snap.defaultMaterial());
    if (material == null) {
      actor.sendError(messages.invalidMaterial().replace("{material}", rawMaterial));
      return;
    }

    var outcome = service.save(sender, name, sender.getLocation(), material);
    switch (outcome) {
      case CREATED -> actor.sendSuccess(messages.homeSet().replace("{name}", name));
      case UPDATED -> actor.sendSuccess(messages.homeUpdated().replace("{name}", name));
      case LIMIT_REACHED -> actor.sendError(limitReachedMessage(messages, name, sender));
      default -> throw new IllegalStateException("Unexpected outcome: " + outcome);
    }
  }

  private static Material resolveMaterial(String raw, Material fallback) {
    if (raw.isBlank()) return fallback;

    var match = Material.matchMaterial(raw);
    if (match == null || !match.isItem()) return null;
    return match;
  }

  private String limitReachedMessage(HomesMessages messages, String name, Player sender) {
    return messages
        .limitReached()
        .replace("{name}", name)
        .replace("{limit}", Integer.toString(service.limit(sender)));
  }
}
