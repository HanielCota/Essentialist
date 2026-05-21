package com.hanielcota.essentials.command;

import com.hanielcota.essentials.validation.Preconditions;
import java.util.List;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record CommandContext(CommandSender sender, String label, List<String> args) {

  public CommandContext {
    Objects.requireNonNull(sender, "sender");
    Objects.requireNonNull(label, "label");
    args = List.copyOf(args == null ? List.of() : args);
  }

  public boolean isPlayer() {
    return sender instanceof Player;
  }

  public Player asPlayer() {
    if (!(sender instanceof Player player)) {
      throw new CommandException("Sender is not a player");
    }
    return player;
  }

  public void requirePermission(String permission) {
    Preconditions.notBlank(permission, "permission");
    if (!sender.hasPermission(permission)) {
      throw new CommandPermissionException(permission);
    }
  }
}
