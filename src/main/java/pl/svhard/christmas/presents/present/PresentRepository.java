package pl.svhard.christmas.presents.present;

import com.eternalcode.commons.bukkit.position.Position;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PresentRepository {

    CompletableFuture<Void> saveAsync(Present present);

    CompletableFuture<Void> removeAsync(PresentId presentId);

    CompletableFuture<Optional<Present>> findByIdAsync(PresentId presentId);

    CompletableFuture<Optional<Present>> findByLocationAsync(Position location);

    CompletableFuture<Collection<Present>> findAllAsync();

    CompletableFuture<Integer> countAsync();
}
