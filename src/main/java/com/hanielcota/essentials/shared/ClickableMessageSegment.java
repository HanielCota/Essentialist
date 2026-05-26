package com.hanielcota.essentials.shared;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public final class ClickableMessageSegment {

  private ClickEvent click;
  private HoverEvent<?> hover;

  ClickableMessageSegment() {}

  public ClickableMessageSegment runCommand(@NonNull String command) {
    this.click = ClickEvent.runCommand(command);
    return this;
  }

  public ClickableMessageSegment hover(@NonNull String mini) {
    var tooltipComponent = ComponentUtils.mini(mini);
    return hover(tooltipComponent);
  }

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
