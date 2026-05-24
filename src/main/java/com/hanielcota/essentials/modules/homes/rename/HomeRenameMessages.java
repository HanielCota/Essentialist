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
    var withName = promptTemplate.replace("{name}", homeName);
    var withSecondsS = withName.replace("{seconds}s", timeoutText);
    var withSeconds = withSecondsS.replace("{seconds}", secondsStr);
    return withSeconds.replace("{timeout}", timeoutText);
  }

  static String timeout(@NonNull HomesMessages messages, long seconds) {
    var secondsStr = Long.toString(seconds);
    var renameTimeoutMsg = messages.renameTimeout();
    return renameTimeoutMsg.replace("{seconds}", secondsStr);
  }

  static String result(
      @NonNull HomesMessages messages,
      @NonNull String oldName,
      @NonNull String newName,
      @NonNull RenameResult result) {

    return switch (result) {
      case RENAMED -> {
        var renamedMsg = messages.renamed();
        var withOld = renamedMsg.replace("{old}", oldName);
        yield withOld.replace("{new}", newName);
      }

      case NOT_FOUND -> {
        var renameLostMsg = messages.renameLost();
        yield renameLostMsg.replace("{name}", oldName);
      }

      case NAME_TAKEN -> {
        var renameTakenMsg = messages.renameTaken();
        yield renameTakenMsg.replace("{name}", newName);
      }

      default -> throw new IllegalStateException("Resultado de renomeação desconhecido: " + result);
    };
  }
}
