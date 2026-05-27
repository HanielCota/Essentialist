package com.hanielcota.essentials.modules.homes.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.service.HomeLimitReachedMessageResolver;
import com.hanielcota.essentials.modules.homes.service.HomeMaterialResolver;
import com.hanielcota.essentials.modules.homes.service.HomeNameResolver;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("sethome")
@EssentialsCommand
@Permission("essentials.home.set")
@Cooldown(duration = "2s")
@Description("Define uma home com o nome dado (ou \"home\" se ausente).")
@Syntax("/sethome [nome] [material]")
public record SetHomeCommand(
    ConfigHandle<HomesConfig> config,
    HomeService service,
    HomeNameResolver nameResolver,
    HomeMaterialResolver materialResolver,
    HomeLimitReachedMessageResolver limitReachedResolver) {

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor actor,
      @Arg("nome") Optional<String> rawName,
      @Arg("material") Optional<String> rawMaterial) {
    var sender = actor.unwrap(Player.class);
    var snap = this.config.value();
    var messages = snap.messages();
    var name = this.nameResolver.resolve(rawName.orElse(""));

    if (name == null) {
      var invalidNameMsg = messages.invalidName();
      return CommandResult.invalidUsage(actor, invalidNameMsg);
    }

    var material = this.materialResolver.resolve(rawMaterial.orElse(""));
    if (material == null) {
      var invalidMaterialTemplate = messages.invalidMaterial();
      var invalidMaterialMsg =
          invalidMaterialTemplate.replace("{material}", rawMaterial.orElse(""));
      return CommandResult.invalidUsage(actor, invalidMaterialMsg);
    }

    var location = sender.getLocation();
    var outcome = this.service.save(sender, name, location, material);

    switch (outcome) {
      case CREATED -> {
        var homeSetTemplate = messages.homeSet();
        var homeSetMsg = homeSetTemplate.replace("{name}", name);
        actor.sendSuccess(homeSetMsg);
      }
      case UPDATED -> {
        var homeUpdatedTemplate = messages.homeUpdated();
        var homeUpdatedMsg = homeUpdatedTemplate.replace("{name}", name);
        actor.sendSuccess(homeUpdatedMsg);
      }
      case LIMIT_REACHED -> {
        var limitReachedMsg = this.limitReachedResolver.resolve(name, sender);
        return CommandResult.invalidUsage(actor, limitReachedMsg);
      }
    }
    return CommandResult.success();
  }
}
