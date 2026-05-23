package com.hanielcota.essentials.modules.homes.rename;

import com.hanielcota.essentials.modules.homes.config.messages.HomesMessages;
import com.hanielcota.essentials.modules.homes.service.HomeService.RenameResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class HomeRenameMessages {

  static String prompt(@NonNull HomesMessages messages, @NonNull String homeName, long seconds) {
    var timeoutText = seconds <= 0 ? "sem limite" : seconds + "s";
    var secondsStr = Long.toString(seconds);

    var promptTemplate = messages.renamePrompt();
    return promptTemplate
        .replace("{name}", homeName)
        .replace("{seconds}s", timeoutText)
        .replace("{seconds}", secondsStr)
        .replace("{timeout}", timeoutText);
  }

  static String timeout(@NonNull HomesMessages messages, long seconds) {
    var secondsStr = Long.toString(seconds);
    return messages.renameTimeout().replace("{seconds}", secondsStr);
  }

  static String result(
      @NonNull HomesMessages messages,
      @NonNull String oldName,
      @NonNull String newName,
      @NonNull RenameResult result) {

    return switch (result) {
      case RENAMED -> messages.renamed().replace("{old}", oldName).replace("{new}", newName);

      case NOT_FOUND -> messages.renameLost().replace("{name}", oldName);

      case NAME_TAKEN -> messages.renameTaken().replace("{name}", newName);

      default -> throw new IllegalStateException("Resultado de renomeação desconhecido: " + result);
    };
  }
}
