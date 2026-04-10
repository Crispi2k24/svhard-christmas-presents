package pl.svhard.christmas.presents.progress;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerProgressRepository {

    CompletableFuture<Void> saveAsync(PlayerProgress progress);

    CompletableFuture<Optional<PlayerProgress>> findByPlayerAsync(UUID playerUuid);

    CompletableFuture<Collection<PlayerProgress>> findAllAsync();

    CompletableFuture<Void> deleteAllAsync();
}
