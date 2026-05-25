package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickHandler;
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

    var rows = menuSpec.effectiveDeleteRows();
    var promptSlot = menuSpec.effectiveDeletePromptSlot();
    var yesSlot = menuSpec.effectiveDeleteYesSlot();
    var noSlot = menuSpec.effectiveDeleteNoSlot();

    ClickHandler noopClick = click -> {};
    ClickHandler confirmClick = this.clickHandler::confirm;
    ClickHandler cancelClick = this.clickHandler::cancel;

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(title);
    builder.slot(promptSlot, prompt, noopClick);
    builder.slot(yesSlot, yes, confirmClick);
    builder.slot(noSlot, no, cancelClick);

    var menu = builder.build();
    menu.register();
  }

  private @NonNull net.kyori.adventure.text.Component title(@NonNull HomesConfig configSnap) {
    var messages = configSnap.messages();
    var titleText = messages.deleteConfirmTitle();
    return ComponentUtils.mini(titleText);
  }

  private @NonNull ItemTemplate promptItem(@NonNull HomesConfig configSnap) {
    var menuSpec = configSnap.menu();
    var messages = configSnap.messages();
    var material = menuSpec.deletePromptMaterial();
    var promptName = messages.deleteConfirmPrompt();

    var builder = ItemTemplate.builder(material);
    builder.name(promptName);
    builder.italic(false);

    return builder.build();
  }

  private @NonNull ItemTemplate yesButton(@NonNull HomesConfig configSnap) {
    var menuSpec = configSnap.menu();
    var messages = configSnap.messages();
    var material = menuSpec.deleteYesMaterial();
    var yesName = messages.deleteConfirmYes();

    var builder = ItemTemplate.builder(material);
    builder.name(yesName);
    builder.italic(false);

    return builder.build();
  }

  private @NonNull ItemTemplate noButton(@NonNull HomesConfig configSnap) {
    var menuSpec = configSnap.menu();
    var messages = configSnap.messages();
    var material = menuSpec.deleteNoMaterial();
    var noName = messages.deleteConfirmNo();

    var builder = ItemTemplate.builder(material);
    builder.name(noName);
    builder.italic(false);

    return builder.build();
  }
}
