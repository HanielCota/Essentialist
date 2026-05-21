package com.hanielcota.essentials.command;

import com.hanielcota.essentials.exception.PluginException;

public class CommandException extends PluginException {

  public CommandException(String message) {
    super(message);
  }

  public CommandException(String message, Throwable cause) {
    super(message, cause);
  }
}
