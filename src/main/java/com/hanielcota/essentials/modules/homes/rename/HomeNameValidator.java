package com.hanielcota.essentials.modules.homes.rename;

/**
 * Rules a new home name must obey, decoupled from the chat-input plumbing so the policy can be
 * swapped without touching the listener.
 */
public interface HomeNameValidator {

  boolean isValid(String name);
}
