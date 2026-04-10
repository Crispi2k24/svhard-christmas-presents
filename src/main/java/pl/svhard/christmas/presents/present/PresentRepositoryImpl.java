package pl.svhard.christmas.presents.present;

import com.eternalcode.commons.bukkit.position.Position;
import com.eternalcode.commons.scheduler.Scheduler;
import com.j256.ormlite.table.TableUtils;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import pl.svhard.christmas.presents.database.AbstractRepositoryOrmLite;
import pl.svhard.christmas.presents.database.DatabaseManager;

public class PresentRepositoryImpl extends AbstractRepositoryOrmLite implements PresentRepository {

    public PresentRepositoryImpl(DatabaseManager databaseManager, Scheduler scheduler) {
        super(databaseManager, scheduler);
        this.createTable();
    }

    private void createTable() {
        try {
            TableUtils.createTableIfNotExists(this.databaseManager.connectionSource(), PresentTable.class);
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to create presents table", exception);
        }
    }

    @Override
    public CompletableFuture<Void> saveAsync(Present present) {
        PresentTable table = new PresentTable(present);
        return this.save(PresentTable.class, table).thenApply(v -> null);
    }

    @Override
    public CompletableFuture<Void> removeAsync(PresentId presentId) {
        return this.deleteById(PresentTable.class, presentId.toString()).thenApply(v -> null);
    }

    @Override
    public CompletableFuture<Optional<Present>> findByIdAsync(PresentId presentId) {
        return this.selectSafe(PresentTable.class, presentId.toString())
            .thenApply(optional -> optional.map(PresentTable::toDomain));
    }

    @Override
    public CompletableFuture<Optional<Present>> findByLocationAsync(Position location) {
        return this.action(
            PresentTable.class, dao -> {
                PresentTable result = dao.queryBuilder()
                    .where()
                    .eq("worldName", location.world())
                    .and()
                    .eq("x", location.x())
                    .and()
                    .eq("y", location.y())
                    .and()
                    .eq("z", location.z())
                    .queryForFirst();

                return result != null ? Optional.of(result.toDomain()) : Optional.empty();
            });
    }

    @Override
    public CompletableFuture<Collection<Present>> findAllAsync() {
        return this.action(
            PresentTable.class, dao -> {
                List<PresentTable> tables = dao.queryForAll();
                return tables.stream()
                    .map(PresentTable::toDomain)
                    .collect(Collectors.toList());
            });
    }

    @Override
    public CompletableFuture<Integer> countAsync() {
        return this.action(PresentTable.class, dao -> (int) dao.countOf());
    }
}
