package com.hanielcota.essentials.core;

import lombok.NonNull;

/**
 * One ordered teardown action registered by a bootstrap stage. {@link CoreLifecycle#shutdown()}
 * invokes every step in reverse-registration order so the most recently bootstrapped
 * infrastructure tears down first.
 *
 * <p>Bootstrap rollback uses the same registry, so the close ordering is defined exactly once.
 */
public interface ShutdownStep {

  String label();

  void run();

  static ShutdownStep of(@NonNull String label, @NonNull Runnable action) {
    return new ShutdownStep() {
      @Override
      public String label() {
        return label;
      }

      @Override
      public void run() {
        action.run();
      }
    };
  }
}
