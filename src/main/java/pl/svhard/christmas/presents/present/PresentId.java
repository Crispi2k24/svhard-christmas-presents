package pl.svhard.christmas.presents.present;

import java.util.Objects;
import java.util.UUID;

public final class PresentId {

    private final UUID value;

    private PresentId(UUID value) {
        this.value = value;
    }

    public static PresentId generate() {
        return new PresentId(UUID.randomUUID());
    }

    public static PresentId of(UUID value) {
        return new PresentId(value);
    }

    public static PresentId fromString(String value) {
        try {
            return new PresentId(UUID.fromString(value));
        }
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid present ID format: " + value, exception);
        }
    }

    public UUID toUUID() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PresentId presentId = (PresentId) o;
        return Objects.equals(this.value, presentId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
