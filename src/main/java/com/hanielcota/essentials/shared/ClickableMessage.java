package com.hanielcota.essentials.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClickableMessage implements ComponentLike {

  private final List<Component> parts = new ArrayList<>();

  public static ClickableMessage create() {
    return new ClickableMessage();
  }

  public ClickableMessage append(@NonNull String mini) {
    var component = ComponentUtils.mini(mini);
    this.parts.add(component);
    return this;
  }

  public ClickableMessage append(
      @NonNull String mini, @NonNull Consumer<ClickableMessageSegment> action) {
    var component = ComponentUtils.mini(mini);
    return append(component, action);
  }

  public ClickableMessage append(@NonNull Component component) {
    this.parts.add(component);
    return this;
  }

  public ClickableMessage append(
      @NonNull Component component, @NonNull Consumer<ClickableMessageSegment> action) {
    var segment = new ClickableMessageSegment();
    action.accept(segment);

    var modifiedComponent = segment.applyTo(component);
    this.parts.add(modifiedComponent);
    return this;
  }

  public ClickableMessage space() {
    this.parts.add(Component.space());
    return this;
  }

  public ClickableMessage newline() {
    this.parts.add(Component.newline());
    return this;
  }

  public Component build() {
    if (this.parts.isEmpty()) {
      return Component.empty();
    }

    var components = this.parts.toArray(Component[]::new);
    return Component.textOfChildren(components);
  }

  @Override
  public @NonNull Component asComponent() {
    return build();
  }

  public void send(@NonNull Audience... audiences) {
    var message = build();

    for (var audience : audiences) {
      audience.sendMessage(message);
    }
  }
}
