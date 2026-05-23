package com.hanielcota.essentials.util;

import java.util.Objects;
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
  public ClickableMessageSegment runCommand(String command) {
    this.click = ClickEvent.runCommand(Objects.requireNonNull(command, "command"));
    return this;
  }

  /** Places the given text in the player's chat box when the segment is clicked. */
  public ClickableMessageSegment suggestCommand(String command) {
    this.click = ClickEvent.suggestCommand(Objects.requireNonNull(command, "command"));
    return this;
  }

  /** Opens the given URL when the segment is clicked. */
  public ClickableMessageSegment openUrl(String url) {
    this.click = ClickEvent.openUrl(Objects.requireNonNull(url, "url"));
    return this;
  }

  /** Copies the given text to the player's clipboard when the segment is clicked. */
  public ClickableMessageSegment copyToClipboard(String text) {
    this.click = ClickEvent.copyToClipboard(Objects.requireNonNull(text, "text"));
    return this;
  }

  /** Inserts the given text into the chat box when the segment is shift-clicked. */
  public ClickableMessageSegment insertion(String text) {
    this.insertion = Objects.requireNonNull(text, "text");
    return this;
  }

  /** Shows a MiniMessage tooltip when the segment is hovered. */
  public ClickableMessageSegment hover(String mini) {
    return hover(ComponentUtils.mini(mini));
  }

  /** Shows a component tooltip when the segment is hovered. */
  public ClickableMessageSegment hover(Component tooltip) {
    this.hover = HoverEvent.showText(Objects.requireNonNull(tooltip, "tooltip"));
    return this;
  }

  Component applyTo(Component component) {
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
