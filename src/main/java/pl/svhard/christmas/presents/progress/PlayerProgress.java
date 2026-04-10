package pl.svhard.christmas.presents.progress;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import pl.svhard.christmas.presents.present.PresentId;

public record PlayerProgress(UUID playerUuid, Set<PresentId> collectedPresentIds) {

    public PlayerProgress(UUID playerUuid, Set<PresentId> collectedPresentIds) {
        this.playerUuid = Objects.requireNonNull(playerUuid, "Player UUID cannot be null");
        this.collectedPresentIds =
            new HashSet<>(Objects.requireNonNull(collectedPresentIds, "Collected presents cannot be null"));
    }

    public static PlayerProgress empty(UUID playerUuid) {
        return new PlayerProgress(playerUuid, new HashSet<>());
    }

    @Override
    public Set<PresentId> collectedPresentIds() {
        return Set.copyOf(this.collectedPresentIds);
    }

    public boolean hasCollected(PresentId presentId) {
        return this.collectedPresentIds.contains(presentId);
    }

    public int getCollectedCount() {
        return this.collectedPresentIds.size();
    }

    public PlayerProgress withCollected(PresentId presentId) {
        Set<PresentId> newCollection = new HashSet<>(this.collectedPresentIds);
        newCollection.add(presentId);
        return new PlayerProgress(this.playerUuid, newCollection);
    }

    public boolean hasCollectedAll(int totalPresents) {
        return this.collectedPresentIds.size() >= totalPresents;
    }

    public PlayerProgress filterByValidIds(Set<PresentId> validIds) {
        if (this.collectedPresentIds.isEmpty()) {
            return this;
        }

        if (validIds.containsAll(this.collectedPresentIds)) {
            return this;
        }

        Set<PresentId> filtered = new HashSet<>(this.collectedPresentIds);
        filtered.retainAll(validIds);
        return new PlayerProgress(this.playerUuid, filtered);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlayerProgress that = (PlayerProgress) o;
        return Objects.equals(this.playerUuid, that.playerUuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.playerUuid);
    }

    @Override
    public @NotNull String toString() {
        return String.format(
            "PlayerProgress{playerUuid=%s, collected=%d}",
            this.playerUuid, this.collectedPresentIds.size());
    }
}
