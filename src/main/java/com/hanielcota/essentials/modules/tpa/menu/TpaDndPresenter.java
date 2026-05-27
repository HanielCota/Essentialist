package com.hanielcota.essentials.modules.tpa.menu;

import com.hanielcota.essentials.menu.ListMarkers;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaBehaviorSettingsMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.modules.tpa.service.TpaDndCycle;
import com.hanielcota.essentials.shared.Placeholders;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TpaDndPresenter {

  public static TpaDndCycle.Durations durations(@NonNull TpaBehaviorSettingsMenuConfig settings) {
    return TpaDndCycle.Durations.ofMinutes(
        settings.dndStage1Minutes(), settings.dndStage2Minutes(), settings.dndStage3Minutes());
  }

  public static String stageLabel(
      @NonNull TpaBehaviorSettingsMenuConfig settings, @NonNull TpaDndCycle.Stage stage) {
    return switch (stage) {
      case OFF -> settings.dndStateOff();
      case STAGE_1 -> settings.dndStateStage1();
      case STAGE_2 -> settings.dndStateStage2();
      case STAGE_3 -> settings.dndStateStage3();
    };
  }

  public static String stageRemaining(
      @NonNull TpaBehaviorSettingsMenuConfig settings, @NonNull TpaProfile profile, long now) {
    var until = profile.dndUntilEpochMs();
    if (until <= now) {
      return settings.dndRemainingFallback();
    }
    var remaining = Duration.ofMillis(until - now);
    var totalMinutes = remaining.toMinutes();
    if (totalMinutes < 1) {
      return settings.dndRemainingUnderMinute();
    }
    if (totalMinutes < 60) {
      var minutesText = Long.toString(totalMinutes);
      return settings.dndRemainingMinutes().replace("{minutes}", minutesText);
    }
    var hours = totalMinutes / 60;
    var mins = totalMinutes % 60;
    var hoursText = Long.toString(hours);
    if (mins == 0) {
      return settings.dndRemainingHours().replace("{hours}", hoursText);
    }
    var minutesText = Long.toString(mins);

    return Placeholders.format(
        settings.dndRemainingHoursMinutes(), "hours", hoursText, "minutes", minutesText);
  }

  public static List<String> renderLore(
      @NonNull TpaBehaviorSettingsMenuConfig settings,
      @NonNull String stateLabel,
      @NonNull String remainingLabel,
      @NonNull TpaDndCycle.Stage current) {
    var values = Map.of("state", stateLabel, "remaining", remainingLabel);
    var lines = new ArrayList<String>(settings.dndLore().size() + 4);
    for (var template : settings.dndLore()) {
      if (template.contains("{options}")) {
        lines.addAll(dndOptions(settings, current));
        continue;
      }
      lines.add(Placeholders.format(template, values));
    }
    return lines;
  }

  public static List<String> dndOptions(
      @NonNull TpaBehaviorSettingsMenuConfig settings, @NonNull TpaDndCycle.Stage current) {
    var marker = settings.dndActiveMarker();
    return List.of(
        ListMarkers.markActive(settings.dndStateOff(), marker, current == TpaDndCycle.Stage.OFF),
        ListMarkers.markActive(
            settings.dndStateStage1(), marker, current == TpaDndCycle.Stage.STAGE_1),
        ListMarkers.markActive(
            settings.dndStateStage2(), marker, current == TpaDndCycle.Stage.STAGE_2),
        ListMarkers.markActive(
            settings.dndStateStage3(), marker, current == TpaDndCycle.Stage.STAGE_3));
  }

  public static List<String> applyState(@NonNull List<String> lore, @NonNull String state) {
    var replaced = new ArrayList<String>(lore.size());
    for (var line : lore) {
      replaced.add(line.replace("{state}", state));
    }
    return replaced;
  }
}
