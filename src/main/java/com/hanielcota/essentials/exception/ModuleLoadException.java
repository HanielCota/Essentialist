package com.hanielcota.essentials.exception;

public class ModuleLoadException extends PluginException {

  public ModuleLoadException(String moduleId, String message) {
    super(prefixed(moduleId, message));
  }

  public ModuleLoadException(String moduleId, String message, Throwable cause) {
    super(prefixed(moduleId, message), cause);
  }

  private static String prefixed(String moduleId, String message) {
    return "[" + moduleId + "] " + message;
  }
}
