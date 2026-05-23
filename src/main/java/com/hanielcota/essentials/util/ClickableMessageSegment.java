package com.hanielcota.essentials.util;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

/**
 * Configures the click and hover actions of a single message segment.
 *
 * <p>Extracted to a package-private top-level class to adhere to the Single Responsibility
 * Principle (SRP) and simplify ClickableMessage.
 */
public final class ClickableMessageSegment {

  private ClickEvent click;
  private HoverEvent<?> hover;
  private String insertion;

  ClickableMessageSegment() {}

  /** Runs the given command (e.g. {@code "/spawn"}) when the segment is clicked. */
  public ClickableMessageSegment runCommand(@NonNull String command) {
    this.click = ClickEvent.runCommand(command);
    return this;
  }

  /** Places the given text in the player's chat box when the segment is clicked. */
  public ClickableMessageSegment suggestCommand(@NonNull String command) {
    this.click = ClickEvent.suggestCommand(command);
    return this;
  }

  /** Opens the given URL when the segment is clicked. */
  public ClickableMessageSegment openUrl(@NonNull String url) {
    this.click = ClickEvent.openUrl(url);
    return this;
  }

  /** Copies the given text to the player's clipboard when the segment is clicked. */
  public ClickableMessageSegment copyToClipboard(@NonNull String text) {
    this.click = ClickEvent.copyToClipboard(text);
    return this;
  }

  /** Inserts the given text into the chat box when the segment is shift-clicked. */
  public ClickableMessageSegment insertion(@NonNull String text) {
    this.insertion = text;
    return this;
  }

  /** Shows a MiniMessage tooltip when the segment is hovered. */
  public ClickableMessageSegment hover(@NonNull String mini) {
    var tooltipComponent = ComponentUtils.mini(mini);
    return hover(tooltipComponent);
  }

  /** Shows a component tooltip when the segment is hovered. */
  public ClickableMessageSegment hover(@NonNull Component tooltip) {
    this.hover = HoverEvent.showText(tooltip);
    return this;
  }

  Component applyTo(@NonNull Component component) {
    var result = component;

    if (click != null) {
      result = result.clickEvent(click);
    }

    if (hover != null) {
      result = result.hoverEvent(hover);
    }

    if (insertion != null) {
      result = result.insertion(insertion);
    }

    return result;
  }
}
