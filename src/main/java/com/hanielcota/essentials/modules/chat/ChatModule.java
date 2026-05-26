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
import com.hanielcota.essentials.modules.chat.listener.ChatPlayerCleanupListener;
import com.hanielcota.essentials.modules.chat.placeholder.PlaceholderApiBridge;
import com.hanielcota.essentials.modules.chat.service.AntiSpamService;
import com.hanielcota.essentials.modules.chat.service.ChatFormatter;
import com.hanielcota.essentials.modules.chat.service.CooldownService;
import com.hanielcota.essentials.modules.chat.service.PlayerMessageStyler;
import com.hanielcota.essentials.modules.chat.service.StaffChatToggleService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.AudienceProvider;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;

/**
 * Chat formatting + channels (PR 2) + cooldown / anti-spam (PR 3) + PlaceholderAPI integration and
 * permission-based player message styling (PR 4).
 *
 * <p>PR 4 wires three new pieces:
 *
 * <ul>
 *   <li>{@link PlaceholderApiBridge} — reflective adapter; reports {@code isAvailable() == false}
 *       when PAPI is missing so callers cheaply skip placeholder work.
 *   <li>{@link PlayerMessageStyler} — converts the player's chat input into a styled {@link
 *       net.kyori.adventure.text.Component} gated by {@code chat.color} / {@code chat.format}.
 *   <li>{@link ChatFormatter} now consumes the bridge to PAPI-expand the cached template and
 *       resolve {@code <prefix>} / {@code <suffix>} through the configurable placeholder keys.
 * </ul>
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

    var papi = new PlaceholderApiBridge();
    var formatter = new ChatFormatter(config, papi);
    var styler = new PlayerMessageStyler();
    var toggleService = new StaffChatToggleService();
    var cooldowns = new CooldownService();
    var antiSpam = new AntiSpamService();

    var globalChannel = new GlobalChannel();
    var localChannel = new LocalChannel(config);
    var staffChannel = new StaffChannel();

    var router =
        new ChannelRouter(config, toggleService, globalChannel, localChannel, staffChannel);

    var chatNotifier = new ChatNotifier(config, actors);
    var staffNotifier = new StaffChatNotifier(config, formatter, players, audiences);

    registrar.command(new ChatCommand(configs, chatNotifier));
    registrar.command(new StaffChatCommand(toggleService, staffNotifier));

    registrar.listener(
        new AsyncChatListener(config, router, formatter, cooldowns, antiSpam, styler));
    registrar.listener(new ChatPlayerCleanupListener(toggleService, cooldowns, antiSpam));
  }
}
