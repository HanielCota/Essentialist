package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.util.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Yes/no confirmation before deleting a home. The home to act on is read from {@link
 * HomesActionTarget} on click, set by {@link HomeClickHandler} when the player right-clicks a home.
 */
@RequiredArgsConstructor
public final class DeleteHomeDialog implements EssentialsMenu {

  public static final String ID = "essentials.homes.delete";

  private final ConfigHandle<HomesConfig> config;
  private final DeleteHomeClickHandler clickHandler;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var configSnap = this.config.value();
    var menuSpec = configSnap.menu();
    var title = title(configSnap);
    var prompt = promptItem(configSnap);
    var yes = yesButton(configSnap);
    var no = noButton(configSnap);

    MenuFramework.builder(ID, menus)
        .rows(menuSpec.effectiveDeleteRows())
        .title(title)
        .slot(menuSpec.effectiveDeletePromptSlot(), prompt, click -> {})
        .slot(menuSpec.effectiveDeleteYesSlot(), yes, this.clickHandler::confirm)
        .slot(menuSpec.effectiveDeleteNoSlot(), no, this.clickHandler::cancel)
        .build()
        .register();
  }

  private @NonNull net.kyori.adventure.text.Component title(@NonNull HomesConfig configSnap) {
    var messages = configSnap.messages();
    var titleText = messages.deleteConfirmTitle();
    return ComponentUtils.mini(titleText);
  }

  private @NonNull ItemTemplate promptItem(@NonNull HomesConfig configSnap) {
    var menuSpec = configSnap.menu();
    var messages = configSnap.messages();
    var promptName = messages.deleteConfirmPrompt();

    return ItemTemplate.builder(menuSpec.deletePromptMaterial())
        .name(promptName)
        .italic(false)
        .build();
  }

  private @NonNull ItemTemplate yesButton(@NonNull HomesConfig configSnap) {
    var menuSpec = configSnap.menu();
    var messages = configSnap.messages();
    var yesName = messages.deleteConfirmYes();

    return ItemTemplate.builder(menuSpec.deleteYesMaterial()).name(yesName).italic(false).build();
  }

  private @NonNull ItemTemplate noButton(@NonNull HomesConfig configSnap) {
    var menuSpec = configSnap.menu();
    var messages = configSnap.messages();
    var noName = messages.deleteConfirmNo();

    return ItemTemplate.builder(menuSpec.deleteNoMaterial()).name(noName).italic(false).build();
  }
}
