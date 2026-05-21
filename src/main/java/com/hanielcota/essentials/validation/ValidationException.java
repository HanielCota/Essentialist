package com.hanielcota.essentials.validation;

import com.hanielcota.essentials.exception.PluginException;

public class ValidationException extends PluginException {

  public ValidationException(String message) {
    super(message);
  }
}
