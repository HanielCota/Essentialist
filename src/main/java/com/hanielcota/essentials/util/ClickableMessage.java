package com.hanielcota.essentials.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jspecify.annotations.NonNull;

/**
 * Fluent builder for interactive (clickable / hoverable) chat messages.
 *
 * <p>Each segment is written in MiniMessage format and may receive click and hover actions through
 * a {@link Segment} lambda. The builder itself is a {@link ComponentLike}, so it can be passed
 * anywhere Adventure expects a component.
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
  public ClickableMessage append(String mini, Consumer<Segment> action) {
    Objects.requireNonNull(mini, "mini");
    return append(ComponentUtils.mini(mini), action);
  }

  /** Appends an already-built component. */
  public ClickableMessage append(Component component) {
    parts.add(Objects.requireNonNull(component, "component"));
    return this;
  }

  /** Appends an already-built component configured with click and/or hover actions. */
  public ClickableMessage append(Component component, Consumer<Segment> action) {
    Objects.requireNonNull(component, "component");
    Objects.requireNonNull(action, "action");

    var segment = new Segment();
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
    Objects.requireNonNull(audiences, "audiences");

    Component message = build();
    for (Audience audience : audiences) {
      Objects.requireNonNull(audience, "audience").sendMessage(message);
    }
  }

  /** Configures the click and hover actions of a single message segment. */
  public static final class Segment {

    private ClickEvent click;
    private HoverEvent<?> hover;
    private String insertion;

    private Segment() {}

    /** Runs the given command (e.g. {@code "/spawn"}) when the segment is clicked. */
    public Segment runCommand(String command) {
      this.click = ClickEvent.runCommand(Objects.requireNonNull(command, "command"));
      return this;
    }

    /** Places the given text in the player's chat box when the segment is clicked. */
    public Segment suggestCommand(String command) {
      this.click = ClickEvent.suggestCommand(Objects.requireNonNull(command, "command"));
      return this;
    }

    /** Opens the given URL when the segment is clicked. */
    public Segment openUrl(String url) {
      this.click = ClickEvent.openUrl(Objects.requireNonNull(url, "url"));
      return this;
    }

    /** Copies the given text to the player's clipboard when the segment is clicked. */
    public Segment copyToClipboard(String text) {
      this.click = ClickEvent.copyToClipboard(Objects.requireNonNull(text, "text"));
      return this;
    }

    /** Inserts the given text into the chat box when the segment is shift-clicked. */
    public Segment insertion(String text) {
      this.insertion = Objects.requireNonNull(text, "text");
      return this;
    }

    /** Shows a MiniMessage tooltip when the segment is hovered. */
    public Segment hover(String mini) {
      return hover(ComponentUtils.mini(mini));
    }

    /** Shows a component tooltip when the segment is hovered. */
    public Segment hover(Component tooltip) {
      this.hover = HoverEvent.showText(Objects.requireNonNull(tooltip, "tooltip"));
      return this;
    }

    private Component applyTo(Component component) {
      Component result = component;
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
}
