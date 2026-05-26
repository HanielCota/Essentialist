package com.hanielcota.essentials.modules.chat.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Root config of the {@code chat} module.
 *
 * <p>Channel layout follows the spec: prefix-routed {@code global}, default {@code local} chat
 * limited by radius, and an opt-in {@code staff} channel reached via {@code /staffchat}. Each
 * channel carries its own MiniMessage template and cooldown — {@link com.hanielcota.essentials
 * .modules.chat.service.ChatFormatter ChatFormatter} caches the legacy-to-MiniMessage normalisation
 * per template, so even with three formats the hot path runs a single map lookup.
 *
 * <p>{@link #antiSpam} groups the cross-channel guards (repeated-message blocking + cooldown
 * messages). Per-channel cooldown duration lives on each channel config; the {@code AntiSpamConfig}
 * holds the warning strings and the global on/off for repeat detection.
 *
 * <p>The player's typed message is always inserted via {@link
 * net.kyori.adventure.text.minimessage.tag.Tag#inserting Tag.inserting}, so tags typed into chat
 * stay literal — players cannot inject {@code <click>} or {@code <hover>} regardless of channel.
 */
@ConfigSerializable
public record ChatConfig(
    GlobalChannelConfig global,
    LocalChannelConfig local,
    StaffChannelConfig staff,
    AntiSpamConfig antiSpam,
    ChatMessages messages) {

  public static ChatConfig defaults() {
    return new ChatConfig(
        GlobalChannelConfig.defaults(),
        LocalChannelConfig.defaults(),
        StaffChannelConfig.defaults(),
        AntiSpamConfig.defaults(),
        ChatMessages.defaults());
  }
}
