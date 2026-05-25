package com.hanielcota.essentials.modules.near.command;

import com.hanielcota.essentials.modules.near.config.NearConfig;
import com.hanielcota.essentials.modules.near.service.NearService;
import java.util.List;
import lombok.NonNull;

/**
 * Renders the {@code /near} result list: joins each entry with the configured separator after
 * formatting it via {@link NearConfig#formatEntry(String, int)}.
 */
public final class NearResultFormatter {

  public String join(@NonNull NearConfig snap, @NonNull List<NearService.Nearby> nearby) {
    var separator = snap.separator();
    var builder = new StringBuilder();
    var first = true;

    for (var found : nearby) {
      var name = found.name();
      var distance = found.distance();
      var entryMsg = snap.formatEntry(name, distance);

      if (!first) {
        builder.append(separator);
      }
      builder.append(entryMsg);
      first = false;
    }

    return builder.toString();
  }
}
