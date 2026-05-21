package com.hanielcota.essentials.exception;

public class ModuleLoadException extends PluginException {

  private final String moduleId;

  public ModuleLoadException(String moduleId, String message) {
    super("[" + moduleId + "] " + message);
    this.moduleId = moduleId;
  }

  public ModuleLoadException(String moduleId, String message, Throwable cause) {
    super("[" + moduleId + "] " + message, cause);
    this.moduleId = moduleId;
  }

  public String moduleId() {
    return moduleId;
  }
}
