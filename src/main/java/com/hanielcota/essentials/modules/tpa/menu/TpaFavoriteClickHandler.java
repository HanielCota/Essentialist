package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.modules.tpa.command.TpaFavoriteAddNotifier;
import com.hanielcota.essentials.modules.tpa.command.TpaFavoritePromptOrchestrator;
import com.hanielcota.essentials.modules.tpa.domain.TpaContact;
import com.hanielcota.essentials.modules.tpa.domain.TpaFavorite;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteSelections;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class TpaFavoriteClickHandler {

  private final @NonNull TpaFavoriteService favorites;
  private final @NonNull TpaProfileService profiles;
  private final @NonNull TpaFavoriteSelections selections;
  private final @NonNull TpaFavoritePromptOrchestrator prompt;
  private final @NonNull TpaFavoriteAddNotifier addNotifier;

  void selectFavorite(@NonNull ClickContext click, @NonNull TpaFavorite entry) {
    var viewerId = click.player().getUniqueId();
    this.selections.select(viewerId, entry);

    click.switchTo(TpaFavoriteActionMenu.ID);
  }

  void selectSuggestion(@NonNull ClickContext click, @NonNull TpaContact contact) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var added = this.favorites.add(viewerId, contact.targetId(), contact.targetName());

    if (added) {
      this.addNotifier.notify(viewer.getName(), contact.targetId());
    }

    var newFavorite = new TpaFavorite(viewerId, contact.targetId(), contact.targetName());
    this.selections.select(viewerId, newFavorite);

    click.switchTo(TpaFavoriteActionMenu.ID);
  }

  void add(@NonNull ClickContext click) {
    var player = click.player();

    click.close();
    this.prompt.prompt(player);
  }

  void cycleOrdering(@NonNull ClickContext click) {
    var viewerId = click.player().getUniqueId();
    this.profiles.cycleFavoriteOrdering(viewerId);
    click.refresh();
  }
}
