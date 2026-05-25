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

  ClickableMessageSegment() {}

  /** Runs the given command (e.g. {@code "/spawn"}) when the segment is clicked. */
  public ClickableMessageSegment runCommand(@NonNull String command) {
    this.click = ClickEvent.runCommand(command);
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

    if (this.click != null) {
      result = result.clickEvent(this.click);
    }

    if (this.hover != null) {
      result = result.hoverEvent(this.hover);
    }

    return result;
  }
}
