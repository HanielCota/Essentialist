package com.hanielcota.essentials.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;

/**
 * Fluent builder for interactive (clickable / hoverable) chat messages.
 *
 * <p>Each segment is written in MiniMessage format and may receive click and hover actions through
 * a {@link ClickableMessageSegment} lambda. The builder itself is a {@link ComponentLike}, so it
 * can be passed anywhere Adventure expects a component.
 *
 * <pre>{@code
 * ClickableMessage.create()
 *     .append("<gray>Pedido de <gold>Steve</gold>: ")
 *     .append("<green>[Aceitar]", s -> s
 *         .runCommand("/tpaccept Steve")
 *         .hover("<gray>Aceitar o pedido"))
 *     .space()
 *     .append("<red>[Recusar]", s -> s
 *         .runCommand("/tpdeny Steve")
 *         .hover("<gray>Recusar o pedido"))
 *     .send(player);
 * }</pre>
 *
 * <p>For fully static clickable text, MiniMessage already supports {@code <click>} and {@code
 * <hover>} tags directly in config strings via {@link ComponentUtils#mini(String)}. This builder is
 * meant for messages whose command, URL or tooltip is computed at runtime.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClickableMessage implements ComponentLike {

  private final List<Component> parts = new ArrayList<>();

  /** Creates a new, empty message builder. */
  public static ClickableMessage create() {
    return new ClickableMessage();
  }

  /** Appends a plain MiniMessage segment with no interactive actions. */
  public ClickableMessage append(@NonNull String mini) {
    var component = ComponentUtils.mini(mini);
    this.parts.add(component);
    return this;
  }

  /** Appends a MiniMessage segment configured with click and/or hover actions. */
  public ClickableMessage append(
      @NonNull String mini, @NonNull Consumer<ClickableMessageSegment> action) {
    var component = ComponentUtils.mini(mini);
    return append(component, action);
  }

  /** Appends an already-built component. */
  public ClickableMessage append(@NonNull Component component) {
    this.parts.add(component);
    return this;
  }

  /** Appends an already-built component configured with click and/or hover actions. */
  public ClickableMessage append(
      @NonNull Component component, @NonNull Consumer<ClickableMessageSegment> action) {
    var segment = new ClickableMessageSegment();
    action.accept(segment);

    var modifiedComponent = segment.applyTo(component);
    this.parts.add(modifiedComponent);
    return this;
  }

  /** Appends a single space. */
  public ClickableMessage space() {
    this.parts.add(Component.space());
    return this;
  }

  /** Appends a line break. */
  public ClickableMessage newline() {
    this.parts.add(Component.newline());
    return this;
  }

  /** Builds the final immutable component. */
  public Component build() {
    if (this.parts.isEmpty()) {
      return Component.empty();
    }

    var componentArray = this.parts.toArray(Component[]::new);
    return Component.textOfChildren(componentArray);
  }

  @Override
  public @NonNull Component asComponent() {
    return build();
  }

  /** Sends the built message to one or more audiences (players, console, broadcast, ...). */
  public void send(@NonNull Audience... audiences) {
    var message = build();

    for (var audience : audiences) {
      audience.sendMessage(message);
    }
  }
}
