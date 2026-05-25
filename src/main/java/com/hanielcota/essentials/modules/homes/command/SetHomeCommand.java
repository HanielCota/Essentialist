package com.hanielcota.essentials.modules.homes.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.material.HomeMaterialResolver;
import com.hanielcota.essentials.modules.homes.name.HomeNameResolver;
import com.hanielcota.essentials.modules.homes.service.HomeLimitReachedMessageResolver;
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
  public void execute(
      @NonNull CommandActor actor,
      @DefaultValue("") @Arg("nome") String rawName,
      @DefaultValue("") @Arg("material") String rawMaterial) {
    var sender = actor.unwrap(Player.class);
    var snap = this.config.value();
    var messages = snap.messages();
    var name = this.nameResolver.resolve(rawName);

    if (name == null) {
      var invalidNameMsg = messages.invalidName();
      actor.sendError(invalidNameMsg);
      return;
    }

    var material = this.materialResolver.resolve(rawMaterial);
    if (material == null) {
      var invalidMaterialTemplate = messages.invalidMaterial();
      var invalidMaterialMsg = invalidMaterialTemplate.replace("{material}", rawMaterial);
      actor.sendError(invalidMaterialMsg);
      return;
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
        actor.sendError(limitReachedMsg);
      }
    }
  }
}
