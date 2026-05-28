package com.hanielcota.essentials.modules.warps.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.service.WarpNameValidator;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Command("setwarp")
@EssentialsCommand
@Permission("essentials.warp.set")
@Description("Cria ou sobrescreve uma warp na sua localização atual.")
@Syntax("/setwarp <nome> [ícone]")
public record SetWarpCommand(
    ConfigHandle<WarpsConfig> config, WarpService service, WarpNameValidator validator) {

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor actor,
      @Arg("nome") String name,
      @Arg("ícone") Optional<Material> icon) {
    var sender = actor.unwrap(Player.class);
    var snap = this.config.value();
    var messages = snap.messages();

    var maxLength = snap.warpNameMaxLength();
    var pattern = snap.allowedNamePattern();

    if (!this.validator.isValid(name, maxLength, pattern)) {
      var maxText = Integer.toString(maxLength);
      var invalidMsg = messages.invalidName().replace("{max}", maxText);
      return CommandResult.invalidUsage(invalidMsg);
    }

    var warpOpt = this.service.findWarp(name);
    var existed = warpOpt.isPresent();

    // SELECT is COLLATE NOCASE so "/setwarp SPAWN" finds an existing "Spawn".
    // Reuse the stored name when overwriting so the canonical case (and any
    // per-warp permission node derived from it) stays stable.
    var persistedName = existed ? warpOpt.get().name() : name;

    // Keep the existing icon when overwriting without specifying a new one.
    var fallbackIcon = warpOpt.map(w -> w.icon()).orElse(snap.defaultIcon());
    var iconMaterial = icon.orElse(fallbackIcon);

    this.service.save(persistedName, sender, iconMaterial);

    var template = existed ? messages.warpUpdated() : messages.warpSet();
    var successMsg = template.replace("{name}", persistedName);

    actor.sendSuccess(successMsg);
    return CommandResult.success();
  }
}
