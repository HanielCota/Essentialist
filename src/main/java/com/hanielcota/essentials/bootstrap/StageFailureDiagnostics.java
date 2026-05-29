package com.hanielcota.essentials.bootstrap;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Attaches a diagnostic breadcrumb to a bootstrap failure so the logs show which stage failed and
 * which stages had already completed. Extracted from {@link EssentialsBootstrap}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class StageFailureDiagnostics {

  static void attach(
      RuntimeException cause, List<BootstrapStage> completed, List<BootstrapStage> stages) {
    var completedNames = completed.stream().map(BootstrapStage::name).toList();
    var failedIndex = completed.size();
    var failedName = failedIndex < stages.size() ? stages.get(failedIndex).name() : "<unknown>";

    var breadcrumb =
        new IllegalStateException(
            "Bootstrap failed at stage '" + failedName + "'; completed: " + completedNames);
    cause.addSuppressed(breadcrumb);
  }
}
