package com.hanielcota.essentials.exception;

public class ConfigurationException extends PluginException {

  public ConfigurationException(String message) {
    super(message);
  }

  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
