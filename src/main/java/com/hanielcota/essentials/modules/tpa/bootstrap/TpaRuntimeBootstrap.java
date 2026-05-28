package com.hanielcota.essentials.modules.tpa.bootstrap;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.tpa.command.accept.TpAcceptOutcomeHandler;
import com.hanielcota.essentials.modules.tpa.command.accept.TpAcceptTeleportNotifier;
import com.hanielcota.essentials.modules.tpa.command.favorites.TpaFavoriteAddNotifier;
import com.hanielcota.essentials.modules.tpa.command.favorites.TpaFavoriteNotifier;
import com.hanielcota.essentials.modules.tpa.command.favorites.TpaFavoritePromptOrchestrator;
import com.hanielcota.essentials.modules.tpa.command.send.TpaIncomingResolver;
import com.hanielcota.essentials.modules.tpa.command.send.TpaNotifier;
import com.hanielcota.essentials.modules.tpa.command.send.TpaRequestReplyNotifier;
import com.hanielcota.essentials.modules.tpa.command.send.TpaSendOrchestrator;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.AsyncTpaHistory;
import com.hanielcota.essentials.modules.tpa.listener.TpaFavoriteChatListener;
import com.hanielcota.essentials.modules.tpa.listener.TpaFavoriteSessionCleanupListener;
import com.hanielcota.essentials.modules.tpa.listener.TpaQuitListener;
import com.hanielcota.essentials.modules.tpa.repository.InMemoryRequestRepository;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.modules.tpa.service.TpaContactService;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.modules.tpa.service.favorites.TpaFavoriteSelections;
import com.hanielcota.essentials.modules.tpa.service.favorites.TpaFavoriteService;
import com.hanielcota.essentials.modules.tpa.service.favorites.TpaFavoriteSessions;
import com.hanielcota.essentials.modules.tpa.service.request.TeleportRequestExecutor;
import com.hanielcota.essentials.modules.tpa.service.request.TeleportRequestExpiry;
import com.hanielcota.essentials.modules.tpa.service.request.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.request.TpaRequestPolicy;
import com.hanielcota.essentials.modules.tpa.service.request.TpaRequestRecorder;
import com.hanielcota.essentials.modules.tpa.service.selection.TpaPendingSelections;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import com.hanielcota.essentials.scheduler.Scheduler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TpaRuntimeBootstrap {

  private final @NonNull ModuleEnvironment env;
  private final @NonNull ModuleRegistrar registrar;
  private final @NonNull ConfigHandle<TpaConfig> config;

  public TpaRuntime requestRuntime(
      @NonNull AsyncTpaHistory history,
      @NonNull TpaProfileService profiles,
      @NonNull TpaBlockService blocks,
      @NonNull TpaContactService contacts,
      @NonNull TpaFavoriteService favorites) {
    var store = new InMemoryRequestRepository();
    var players = this.env.service(PlayerProvider.class);
    var notifier = new TpaNotifier(this.config, players, profiles, favorites);
    var policy = new TpaRequestPolicy(profiles, blocks);
    var recorder = new TpaRequestRecorder(history, profiles, contacts);
    var executor = new TeleportRequestExecutor(players);
    var requestService =
        new TeleportRequestService(this.config, store, players, policy, recorder, executor);

    var scheduler = this.env.service(Scheduler.class);
    var expiry = new TeleportRequestExpiry(scheduler, store, requestService, notifier);
    expiry.start();
    this.registrar.closeable(expiry::stop);

    return new TpaRuntime(requestService, notifier);
  }

  public TpaShared sharedHelpers(@NonNull TeleportRequestService requestService) {
    var actors = this.env.service(ActorFactory.class);
    var players = this.env.service(PlayerProvider.class);
    var callbacks = this.env.service(MainThreadCallbacks.class);
    var replyNotifier = new TpaRequestReplyNotifier(actors, players);
    var acceptHandler = new TpAcceptOutcomeHandler(this.config, replyNotifier);
    var teleportNotifier = new TpAcceptTeleportNotifier(this.config);
    var incomingResolver = new TpaIncomingResolver(this.config, requestService);

    return new TpaShared(
        actors,
        players,
        callbacks,
        replyNotifier,
        acceptHandler,
        teleportNotifier,
        incomingResolver);
  }

  public FavoriteRuntime favoriteRuntime(
      @NonNull TpaFavoriteService favorites, @NonNull TpaProfileService profiles) {
    var sessions = new TpaFavoriteSessions();
    var selections = new TpaFavoriteSelections();
    var notifier = new TpaFavoriteNotifier(this.config);
    var players = this.env.service(PlayerProvider.class);
    var addNotifier = new TpaFavoriteAddNotifier(notifier, profiles, players);
    var scheduler = this.env.service(Scheduler.class);
    var orchestrator =
        new TpaFavoritePromptOrchestrator(
            this.config, favorites, sessions, notifier, players, scheduler, addNotifier);

    var chatListener = new TpaFavoriteChatListener(orchestrator, sessions);
    this.registrar.listener(chatListener);

    var cleanupListener = new TpaFavoriteSessionCleanupListener(sessions, selections);
    this.registrar.listener(cleanupListener);

    return new FavoriteRuntime(selections, orchestrator, addNotifier);
  }

  public TpaSendOrchestrator sendDispatcher(
      @NonNull TeleportRequestService requests,
      @NonNull TpaFavoriteService favorites,
      @NonNull TpaProfileService profiles,
      @NonNull TpaShared shared,
      @NonNull TpaNotifier notifier) {
    return new TpaSendOrchestrator(
        this.config,
        requests,
        favorites,
        profiles,
        shared.acceptHandler(),
        shared.teleportNotifier(),
        shared.callbacks(),
        shared.actors(),
        notifier);
  }

  public void registerQuitListener(
      @NonNull TpaRuntime runtime, @NonNull TpaPendingSelections pendingSelections) {
    var requestService = runtime.requestService();
    var notifier = runtime.notifier();
    var quitListener = new TpaQuitListener(requestService, notifier, pendingSelections);

    this.registrar.listener(quitListener);
  }

  public record TpaRuntime(TeleportRequestService requestService, TpaNotifier notifier) {}

  public record FavoriteRuntime(
      TpaFavoriteSelections selections,
      TpaFavoritePromptOrchestrator orchestrator,
      TpaFavoriteAddNotifier addNotifier) {}

  public record TpaShared(
      ActorFactory actors,
      PlayerProvider players,
      MainThreadCallbacks callbacks,
      TpaRequestReplyNotifier replyNotifier,
      TpAcceptOutcomeHandler acceptHandler,
      TpAcceptTeleportNotifier teleportNotifier,
      TpaIncomingResolver incomingResolver) {}
}
