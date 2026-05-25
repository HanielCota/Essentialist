package com.hanielcota.essentials.modules.nick.service;

/** Result of a {@code /nick off} attempt. Variants are dataless — enum is enough. */
public enum NickResetOutcome {
  ALREADY_HAS_NO_NICK,
  OK
}
