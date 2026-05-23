package com.hanielcota.essentials.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jspecify.annotations.NonNull;

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
public final class ClickableMessage implements ComponentLike {

  private final List<Component> parts = new ArrayList<>();

  private ClickableMessage() {}

  /** Creates a new, empty message builder. */
  public static ClickableMessage create() {
    return new ClickableMessage();
  }

  /** Appends a plain MiniMessage segment with no interactive actions. */
  public ClickableMessage append(String mini) {
    parts.add(ComponentUtils.mini(mini));
    return this;
  }

  /** Appends a MiniMessage segment configured with click and/or hover actions. */
  public ClickableMessage append(String mini, Consumer<ClickableMessageSegment> action) {
    return append(ComponentUtils.mini(mini), action);
  }

  /** Appends an already-built component. */
  public ClickableMessage append(Component component) {
    parts.add(component);
    return this;
  }

  /** Appends an already-built component configured with click and/or hover actions. */
  public ClickableMessage append(Component component, Consumer<ClickableMessageSegment> action) {

    var segment = new ClickableMessageSegment();
    action.accept(segment);
    parts.add(segment.applyTo(component));
    return this;
  }

  /** Appends a single space. */
  public ClickableMessage space() {
    parts.add(Component.space());
    return this;
  }

  /** Appends a line break. */
  public ClickableMessage newline() {
    parts.add(Component.newline());
    return this;
  }

  /** Builds the final immutable component. */
  public Component build() {
    if (parts.isEmpty()) {
      return Component.empty();
    }
    return Component.textOfChildren(parts.toArray(Component[]::new));
  }

  @Override
  public @NonNull Component asComponent() {
    return build();
  }

  /** Sends the built message to one or more audiences (players, console, broadcast, ...). */
  public void send(Audience... audiences) {

    var message = build();
    for (var audience : audiences) {
      audience.sendMessage(message);
    }
  }
}
