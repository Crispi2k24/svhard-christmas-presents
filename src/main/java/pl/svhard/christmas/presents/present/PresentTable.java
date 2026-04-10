package pl.svhard.christmas.presents.present;

import com.eternalcode.commons.bukkit.position.Position;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.Instant;

@DatabaseTable(tableName = "presents")
public class PresentTable {

    @DatabaseField(id = true, columnName = "id")
    private String id;

    @DatabaseField(columnName = "world_name")
    private String worldName;

    @DatabaseField(columnName = "x")
    private double x;

    @DatabaseField(columnName = "y")
    private double y;

    @DatabaseField(columnName = "z")
    private double z;

    @DatabaseField(columnName = "placed_at")
    private long placedAt;

    public PresentTable() {
    }

    public PresentTable(Present present) {
        this.id = present.getId().toString();
        Position position = present.getLocation();
        this.worldName = position.world();
        this.x = position.x();
        this.y = position.y();
        this.z = position.z();
        this.placedAt = Instant.now().getEpochSecond();
    }

    public Present toDomain() {
        PresentId presentId = PresentId.fromString(this.id);
        Position location = new Position(this.x, this.y, this.z, this.worldName);
        Instant instant = Instant.ofEpochSecond(this.placedAt);

        return new Present(presentId, location, instant);
    }

    public String getId() {
        return id;
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public long getPlacedAt() {
        return placedAt;
    }
}
