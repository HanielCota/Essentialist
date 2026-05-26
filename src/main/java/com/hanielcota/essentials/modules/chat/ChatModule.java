package com.hanielcota.essentials.modules.chat;

import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.chat.command.ChatCommand;
import com.hanielcota.essentials.modules.chat.command.ChatNotifier;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.listener.AsyncChatListener;
import com.hanielcota.essentials.modules.chat.service.ChatFormatter;
import com.hanielcota.essentials.paper.ActorFactory;
import lombok.NonNull;

/**
 * Chat formatting and admin commands.
 *
 * <p>This is the first PR of a phased rollout: it wires a {@code ChatRenderer} into {@link
 * io.papermc.paper.event.player.AsyncChatEvent} so every message goes through the configured
 * MiniMessage template, with legacy {@code &} colour codes normalised once per reload. Channels
 * (global/local/staff), cooldowns, anti-spam, and PlaceholderAPI integration land in later PRs.
 */
public final class ChatModule extends AbstractModule {

  public ChatModule() {
    super("chat");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("chat", ChatConfig.class, ChatConfig::defaults);
    var configs = env.service(ConfigService.class);
    var actors = env.service(ActorFactory.class);

    var formatter = new ChatFormatter(config);
    var notifier = new ChatNotifier(config, actors);

    registrar.command(new ChatCommand(configs, notifier));
    registrar.listener(new AsyncChatListener(formatter));
  }
}
