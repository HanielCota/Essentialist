package com.hanielcota.essentials.bootstrap;

import java.util.List;

/**
 * Attaches a diagnostic breadcrumb to a bootstrap failure so the logs show which stage failed and
 * which stages had already completed. Extracted from {@link EssentialsBootstrap}.
 */
final class StageFailureDiagnostics {

  private StageFailureDiagnostics() {}

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
