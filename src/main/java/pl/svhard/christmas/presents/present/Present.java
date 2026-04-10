package pl.svhard.christmas.presents.present;

import com.eternalcode.commons.bukkit.position.Position;
import java.time.Instant;
import java.util.Objects;

public final class Present {

    private final PresentId id;
    private final Position location;
    private final Instant placedAt;

    public Present(PresentId id, Position location, Instant placedAt) {
        this.id = Objects.requireNonNull(id, "Present ID cannot be null");
        this.location = Objects.requireNonNull(location, "Present location cannot be null");
        this.placedAt = Objects.requireNonNull(placedAt, "Placed time cannot be null");
    }

    public static Present create(Position location) {
        return new Present(PresentId.generate(), location, Instant.now());
    }

    public PresentId getId() {
        return this.id;
    }

    public Position getLocation() {
        return this.location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Present present = (Present) o;
        return Objects.equals(this.id, present.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return String.format(
            "Present{id=%s, location=%s, placedAt=%s}",
            this.id, this.location, this.placedAt);
    }
}
