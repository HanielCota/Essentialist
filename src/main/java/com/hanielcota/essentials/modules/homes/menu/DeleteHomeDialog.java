package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickHandler;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.menu.DeleteDialogSection;
import com.hanielcota.essentials.shared.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

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
    var deleteDialog = configSnap.menu().deleteDialog();

    var title = title(configSnap);
    var prompt = promptItem(configSnap);
    var yes = yesButton(configSnap);
    var no = noButton(configSnap);

    var rows = DeleteDialogSection.rows(deleteDialog);
    var promptSlot = DeleteDialogSection.promptSlot(deleteDialog);
    var yesSlot = DeleteDialogSection.yesSlot(deleteDialog);
    var noSlot = DeleteDialogSection.noSlot(deleteDialog);

    ClickHandler noopClick = click -> {};
    ClickHandler confirmClick = this.clickHandler::confirm;
    ClickHandler cancelClick = this.clickHandler::cancel;

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(title);
    builder.slot(promptSlot, prompt, noopClick);
    builder.slot(yesSlot, yes, confirmClick);
    builder.slot(noSlot, no, cancelClick);

    builder.buildAndRegister();
  }

  private @NonNull Component title(@NonNull HomesConfig configSnap) {
    var messages = configSnap.messages();
    var titleText = messages.deleteConfirmTitle();
    var titleComponent = ComponentUtils.mini(titleText);

    return titleComponent;
  }

  private @NonNull ItemTemplate promptItem(@NonNull HomesConfig configSnap) {
    var deleteDialog = configSnap.menu().deleteDialog();
    var messages = configSnap.messages();
    var material = deleteDialog.promptMaterial();
    var promptName = messages.deleteConfirmPrompt();

    return MenuTemplates.simple(material, promptName);
  }

  private @NonNull ItemTemplate yesButton(@NonNull HomesConfig configSnap) {
    var deleteDialog = configSnap.menu().deleteDialog();
    var messages = configSnap.messages();
    var material = deleteDialog.yesMaterial();
    var yesName = messages.deleteConfirmYes();

    return MenuTemplates.simple(material, yesName);
  }

  private @NonNull ItemTemplate noButton(@NonNull HomesConfig configSnap) {
    var deleteDialog = configSnap.menu().deleteDialog();
    var messages = configSnap.messages();
    var material = deleteDialog.noMaterial();
    var noName = messages.deleteConfirmNo();

    return MenuTemplates.simple(material, noName);
  }
}
