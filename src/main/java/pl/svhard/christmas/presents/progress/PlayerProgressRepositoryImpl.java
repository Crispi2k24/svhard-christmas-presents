package pl.svhard.christmas.presents.progress;

import com.eternalcode.commons.scheduler.Scheduler;
import com.j256.ormlite.table.TableUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import pl.svhard.christmas.presents.database.AbstractRepositoryOrmLite;
import pl.svhard.christmas.presents.database.DatabaseManager;
import pl.svhard.christmas.presents.present.PresentId;

public class PlayerProgressRepositoryImpl extends AbstractRepositoryOrmLite implements PlayerProgressRepository {

    public PlayerProgressRepositoryImpl(DatabaseManager databaseManager, Scheduler scheduler) {
        super(databaseManager, scheduler);
        this.createTable();
    }

    private void createTable() {
        try {
            TableUtils.createTableIfNotExists(this.databaseManager.connectionSource(), PlayerProgressTable.class);
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to create player_progress table", exception);
        }
    }

    @Override
    public CompletableFuture<Void> saveAsync(PlayerProgress progress) {
        return this.action(
            PlayerProgressTable.class, dao -> {
                List<PlayerProgressTable> existing = dao.queryBuilder()
                    .where()
                    .eq("playerUuid", progress.playerUuid())
                    .query();

                if (!existing.isEmpty()) {
                    dao.delete(existing);
                }

                for (PresentId presentId : progress.collectedPresentIds()) {
                    PlayerProgressTable table = new PlayerProgressTable(
                        progress.playerUuid(),
                        presentId.toUUID(),
                        Instant.now().getEpochSecond()
                    );
                    dao.create(table);
                }
                return null;
            });
    }

    @Override
    public CompletableFuture<Optional<PlayerProgress>> findByPlayerAsync(UUID playerUuid) {
        return this.action(
            PlayerProgressTable.class, dao -> {
                List<PlayerProgressTable> results = dao.queryBuilder()
                    .where()
                    .eq("playerUuid", playerUuid)
                    .query();

                if (results.isEmpty()) {
                    return Optional.empty();
                }

                Set<PresentId> collectedIds = new HashSet<>();
                for (PlayerProgressTable table : results) {
                    collectedIds.add(PresentId.of(table.getPresentId()));
                }

                return Optional.of(new PlayerProgress(playerUuid, collectedIds));
            });
    }

    @Override
    public CompletableFuture<java.util.Collection<PlayerProgress>> findAllAsync() {
        return this.action(
            PlayerProgressTable.class, dao -> {
                List<PlayerProgressTable> allRecords = dao.queryForAll();

                Map<UUID, Set<PresentId>> playerProgressMap = new java.util.HashMap<>();

                for (PlayerProgressTable record : allRecords) {
                    UUID playerUuid = record.getPlayerUuid();
                    PresentId presentId = PresentId.of(record.getPresentId());

                    playerProgressMap
                        .computeIfAbsent(playerUuid, k -> new HashSet<>())
                        .add(presentId);
                }

                List<PlayerProgress> result = new ArrayList<>();
                for (Entry<UUID, Set<PresentId>> entry : playerProgressMap.entrySet()) {
                    result.add(new PlayerProgress(entry.getKey(), entry.getValue()));
                }

                return result;
            });
    }

    @Override
    public CompletableFuture<Void> deleteAllAsync() {
        return this.deleteAll(PlayerProgressTable.class).thenApply(v -> null);
    }
}
