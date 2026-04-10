package pl.svhard.christmas.presents.present;

import com.eternalcode.commons.bukkit.position.PositionAdapter;
import com.eternalcode.commons.scheduler.Scheduler;
import java.util.Collection;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.meta.SkullMeta;

public class MissingPresentBlockRestorer {

    private final Scheduler scheduler;
    private final Logger logger;

    public MissingPresentBlockRestorer(Scheduler scheduler, Logger logger) {
        this.scheduler = scheduler;
        this.logger = logger;
    }

    public void restoreMissingBlocks(Collection<Present> presents, SkullMeta headMeta) {
        if (headMeta == null) {
            this.logger.warning("Cannot restore missing blocks: head meta is null");
            return;
        }

        this.scheduler.run(() -> {
            int restoredCount = 0;

            for (Present present : presents) {
                try {
                    Location location = PositionAdapter.convert(present.getLocation());
                    if (location.getWorld() == null) {
                        continue;
                    }

                    Block block = location.getBlock();

                    if (block.getType() != Material.PLAYER_HEAD && block.getType() != Material.PLAYER_WALL_HEAD) {
                        block.setType(Material.PLAYER_HEAD);

                        if (block.getState() instanceof Skull skull) {
                            skull.setOwnerProfile(headMeta.getOwnerProfile());
                            skull.update(true);
                            restoredCount++;
                        }
                    }
                }
                catch (Exception exception) {
                    // World not loaded or other issue - silently skip
                }
            }

            if (restoredCount > 0) {
                this.logger.info("Restored " + restoredCount + " missing present blocks.");
            }
        });
    }
}
