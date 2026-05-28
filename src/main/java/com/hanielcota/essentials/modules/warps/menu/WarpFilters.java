package com.hanielcota.essentials.modules.warps.menu;

import com.hanielcota.essentials.modules.warps.domain.Warp;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Applies a {@link WarpFilter} to a warp list using the viewer's {@link WarpFilterData}. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WarpFilters {

  public static @NonNull List<Warp> apply(
      @NonNull List<Warp> warps, @NonNull WarpFilter filter, @NonNull WarpFilterData data) {
    return switch (filter) {
      case DEFAULT -> warps;
      case MOST_PLAYERS -> sortedBy(warps, warp -> data.players(warp.name()), true);
      case LEAST_PLAYERS -> sortedBy(warps, warp -> data.players(warp.name()), false);
      case MOST_LIKED -> sortedBy(warps, warp -> data.likes(warp.name()), true);
      case FAVORITES -> warps.stream().filter(warp -> data.favorite(warp.name())).toList();
      case PVP -> warps.stream().filter(warp -> data.pvp(warp.name())).toList();
    };
  }

  private static List<Warp> sortedBy(
      @NonNull List<Warp> warps, @NonNull ToIntFunction<Warp> metric, boolean descending) {
    Comparator<Warp> byMetric = Comparator.comparingInt(metric);
    if (descending) {
      byMetric = byMetric.reversed();
    }

    var byName = Comparator.comparing(Warp::name, String.CASE_INSENSITIVE_ORDER);
    var ordering = byMetric.thenComparing(byName);

    return warps.stream().sorted(ordering).toList();
  }
}
