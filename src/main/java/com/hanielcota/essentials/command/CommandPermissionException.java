package com.hanielcota.essentials.command;

import java.util.Objects;

public class CommandPermissionException extends CommandException {

  private final String permission;

  public CommandPermissionException(String permission) {
    super("Missing permission: " + Objects.requireNonNull(permission, "permission"));
    this.permission = permission;
  }

  public String permission() {
    return permission;
  }
}
