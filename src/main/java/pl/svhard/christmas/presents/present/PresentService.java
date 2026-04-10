package pl.svhard.christmas.presents.present;

import com.eternalcode.commons.bukkit.position.Position;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.plugin.PluginManager;
import pl.svhard.christmas.presents.event.PresentRemovedEvent;
import pl.svhard.christmas.presents.progress.PlayerProgress;
import pl.svhard.christmas.presents.progress.PlayerProgressRepository;
import pl.svhard.christmas.presents.reward.CollectionResult;

public class PresentService {

    private final PresentRepository presentRepository;
    private final PlayerProgressRepository progressRepository;
    private final PluginManager pluginManager;
    private final Map<PresentId, Present> presentCache = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerProgress> playerProgressCache = new ConcurrentHashMap<>();

    public PresentService(
        PresentRepository presentRepository,
        PlayerProgressRepository progressRepository,
        PluginManager pluginManager
    ) {
        this.presentRepository = presentRepository;
        this.progressRepository = progressRepository;
        this.pluginManager = pluginManager;
    }

    public CompletableFuture<Void> loadPresents() {
        return this.presentRepository.findAllAsync().thenAccept(presents -> {
            this.presentCache.clear();
            presents.forEach(p -> this.presentCache.put(p.getId(), p));
        });
    }

    public CompletableFuture<Void> loadSession(UUID playerUuid) {
        return this.getProgressAsync(playerUuid).thenAccept(progress -> {
            this.playerProgressCache.put(playerUuid, progress);
        });
    }

    public void unloadSession(UUID playerUuid) {
        this.playerProgressCache.remove(playerUuid);
    }

    public boolean hasCollectedCached(UUID playerUuid, PresentId presentId) {
        PlayerProgress progress = this.playerProgressCache.get(playerUuid);
        return progress != null && progress.hasCollected(presentId);
    }

    public CompletableFuture<Present> placePresentAsync(Position location) {
        if (this.presentCache.values().stream().anyMatch(p -> p.getLocation().equals(location))) {
            CompletableFuture<Present> future = new CompletableFuture<>();
            future.completeExceptionally(new PresentAlreadyExistsException(location));
            return future;
        }

        Present present = Present.create(location);
        this.presentCache.put(present.getId(), present);

        return this.presentRepository.saveAsync(present).thenApply(v -> {
            this.pluginManager.callEvent(new pl.svhard.christmas.presents.event.PresentAddedEvent(present));
            return present;
        });
    }

    public CompletableFuture<Void> removePresentAsync(PresentId presentId) {
        Present removed = this.presentCache.remove(presentId);
        if (removed != null) {
            this.pluginManager.callEvent(new PresentRemovedEvent(removed));
        }

        return this.presentRepository.removeAsync(presentId)
            .thenCompose(v -> this.cleanupInvalidProgressAsync());
    }

    private CompletableFuture<Void> cleanupInvalidProgressAsync() {
        Set<PresentId> validIds = this.presentCache.keySet();

        return this.progressRepository.findAllAsync().thenCompose(allProgress -> {
            java.util.List<CompletableFuture<Void>> updates = new java.util.ArrayList<>();

            for (PlayerProgress progress : allProgress) {
                PlayerProgress filtered = progress.filterByValidIds(validIds);

                if (filtered.getCollectedCount() != progress.getCollectedCount()) {
                    this.playerProgressCache.put(progress.playerUuid(), filtered);
                    updates.add(this.progressRepository.saveAsync(filtered));
                }
            }

            return CompletableFuture.allOf(updates.toArray(new CompletableFuture[0]));
        });
    }

    public Optional<Present> getPresent(PresentId presentId) {
        return Optional.ofNullable(this.presentCache.get(presentId));
    }

    public Optional<Present> getPresentAtLocation(Position location) {
        return this.presentCache.values().stream()
            .filter(p -> p.getLocation().equals(location))
            .findFirst();
    }

    public Collection<Present> getAllPresents() {
        return List.copyOf(this.presentCache.values());
    }

    public int getTotalPresentCount() {
        return this.presentCache.size();
    }

    public CompletableFuture<CollectionResult> collectPresentAsync(UUID playerUuid, PresentId presentId) {
        if (!this.presentCache.containsKey(presentId)) {
            return CompletableFuture.completedFuture(CollectionResult.alreadyCollected());
        }

        CompletableFuture<PlayerProgress> progressFuture;
        if (this.playerProgressCache.containsKey(playerUuid)) {
            progressFuture = CompletableFuture.completedFuture(this.playerProgressCache.get(playerUuid));
        }
        else {
            progressFuture = this.getProgressAsync(playerUuid);
        }

        return progressFuture.thenCompose(progress -> {
            if (progress.hasCollected(presentId)) {
                return CompletableFuture.completedFuture(CollectionResult.alreadyCollected());
            }

            PlayerProgress updatedProgress = progress.withCollected(presentId);

            this.playerProgressCache.put(playerUuid, updatedProgress);

            return this.progressRepository.saveAsync(updatedProgress)
                .thenApply(v -> {
                    int totalPresents = this.getTotalPresentCount();
                    boolean completedAll = updatedProgress.hasCollectedAll(totalPresents);

                    return CollectionResult.success(
                        updatedProgress.getCollectedCount(),
                        totalPresents,
                        completedAll
                    );
                });
        });
    }

    public CompletableFuture<PlayerProgress> getProgressAsync(UUID playerUuid) {
        return this.progressRepository.findByPlayerAsync(playerUuid)
            .thenApply(opt -> {
                PlayerProgress progress = opt.orElse(PlayerProgress.empty(playerUuid));
                return progress.filterByValidIds(this.presentCache.keySet());
            });
    }
}
