package pl.svhard.christmas.presents.progress;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.UUID;

@DatabaseTable(tableName = "player_progress")
public class PlayerProgressTable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private UUID playerUuid;

    @DatabaseField(canBeNull = false)
    private UUID presentId;

    @DatabaseField(canBeNull = false)
    private long collectedAt;

    public PlayerProgressTable() {
        // OrmLite requires no-arg constructor
    }

    public PlayerProgressTable(UUID playerUuid, UUID presentId, long collectedAt) {
        this.playerUuid = playerUuid;
        this.presentId = presentId;
        this.collectedAt = collectedAt;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public UUID getPresentId() {
        return presentId;
    }

    public long getCollectedAt() {
        return collectedAt;
    }
}
