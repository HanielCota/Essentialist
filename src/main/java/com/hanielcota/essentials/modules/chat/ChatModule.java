package com.hanielcota.essentials.modules.chat;

import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.chat.channel.ChannelRouter;
import com.hanielcota.essentials.modules.chat.channel.GlobalChannel;
import com.hanielcota.essentials.modules.chat.channel.LocalChannel;
import com.hanielcota.essentials.modules.chat.channel.StaffChannel;
import com.hanielcota.essentials.modules.chat.command.ChatCommand;
import com.hanielcota.essentials.modules.chat.command.ChatNotifier;
import com.hanielcota.essentials.modules.chat.command.StaffChatCommand;
import com.hanielcota.essentials.modules.chat.command.StaffChatNotifier;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.listener.AsyncChatListener;
import com.hanielcota.essentials.modules.chat.listener.StaffChatQuitListener;
import com.hanielcota.essentials.modules.chat.service.ChatFormatter;
import com.hanielcota.essentials.modules.chat.service.StaffChatToggleService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.AudienceProvider;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;

/**
 * Chat formatting plus the three channels of PR 2 — global (prefix-routed), local (proximity), and
 * staff (permission-gated + persistent toggle via {@code /staffchat toggle}). Cooldowns/anti-spam,
 * PlaceholderAPI, and permission-based formatting arrive in PR 3 and PR 4.
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
    var audiences = env.service(AudienceProvider.class);
    var players = env.service(PlayerProvider.class);

    var formatter = new ChatFormatter();
    var toggleService = new StaffChatToggleService();

    var globalChannel = new GlobalChannel();
    var localChannel = new LocalChannel(config);
    var staffChannel = new StaffChannel();

    var router =
        new ChannelRouter(config, toggleService, globalChannel, localChannel, staffChannel);

    var chatNotifier = new ChatNotifier(config, actors);
    var staffNotifier = new StaffChatNotifier(config, formatter, players, audiences);

    registrar.command(new ChatCommand(configs, chatNotifier));
    registrar.command(new StaffChatCommand(toggleService, staffNotifier));

    registrar.listener(new AsyncChatListener(config, router, formatter));
    registrar.listener(new StaffChatQuitListener(toggleService));
  }
}
