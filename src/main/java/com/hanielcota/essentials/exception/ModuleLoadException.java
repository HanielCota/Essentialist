package com.hanielcota.essentials.exception;

public class ModuleLoadException extends PluginException {

  public ModuleLoadException(String moduleId, String message) {
    super("[" + moduleId + "] " + message);
  }

  public ModuleLoadException(String moduleId, String message, Throwable cause) {
    super("[" + moduleId + "] " + message, cause);
  }
}
