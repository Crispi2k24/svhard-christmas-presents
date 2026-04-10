package pl.svhard.christmas.presents.present.animation;

import com.eternalcode.commons.bukkit.position.PositionAdapter;
import com.eternalcode.commons.scheduler.Scheduler;
import com.eternalcode.commons.scheduler.Task;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.svhard.christmas.presents.present.Present;
import pl.svhard.christmas.presents.present.PresentService;

public class PresentIdleAnimationService {

    private static final int CHUNK_SIZE = 50;
    private static final int PLAYER_CACHE_REFRESH_TICKS = 20;
    private final Server server;
    private final Scheduler scheduler;
    private final AnimationConfig config;
    private final PresentService presentService;

    private final Map<String, Task> animationTasks = new ConcurrentHashMap<>();
    private final Map<World, List<Player>> cachedPlayers = new ConcurrentHashMap<>();
    private final Map<Present, CachedLocationData> locationCache = new HashMap<>();
    private List<Present> cachedPresents = new ArrayList<>();

    private int playerCacheTicks = 0;
    private int currentChunk = 0;

    public PresentIdleAnimationService(
        Server server,
        Scheduler scheduler,
        AnimationConfig config,
        PresentService presentService
    ) {
        this.server = server;
        this.scheduler = scheduler;
        this.config = config;
        this.presentService = presentService;
    }

    public void startAll(Collection<Present> presents) {
        if (!this.config.enabled()) {
            return;
        }

        this.stopAll();

        this.cachedPresents = new ArrayList<>(presents);
        this.precomputeLocations();

        Duration interval = Duration.ofMillis(this.config.tickInterval() * 50);

        Task task = this.scheduler.timer(
            () -> {
                if (++this.playerCacheTicks >= PLAYER_CACHE_REFRESH_TICKS) {
                    this.refreshPlayerCache();
                    this.playerCacheTicks = 0;
                }

                int totalPresents = this.cachedPresents.size();
                if (totalPresents == 0) {
                    return;
                }

                int start = this.currentChunk * CHUNK_SIZE;
                int end = Math.min(start + CHUNK_SIZE, totalPresents);

                for (int i = start; i < end; i++) {
                    Present present = this.cachedPresents.get(i);
                    try {
                        this.spawnOrbitalParticles(present);
                    }
                    catch (Exception exception) {
                        // Ignore errors
                    }
                }
                int totalChunks = (totalPresents + CHUNK_SIZE - 1) / CHUNK_SIZE;
                this.currentChunk = (this.currentChunk + 1) % Math.max(1, totalChunks);
            },
            Duration.ZERO,
            interval
        );

        this.animationTasks.put("global_animation", task);
    }

    public void stopAll() {
        this.animationTasks.values().forEach(Task::cancel);
        this.animationTasks.clear();
        this.cachedPlayers.clear();
        this.locationCache.clear();
        this.currentChunk = 0;
        this.playerCacheTicks = 0;
    }

    private void precomputeLocations() {
        this.locationCache.clear();
        for (Present present : this.cachedPresents) {
            try {
                Location location = PositionAdapter.convert(present.getLocation());
                if (location.getWorld() != null) {
                    double centerX = location.getX() + 0.5;
                    double centerY = location.getY() + 0.5;
                    double centerZ = location.getZ() + 0.5;

                    this.locationCache.put(
                        present, new CachedLocationData(
                            location.getWorld(),
                            centerX,
                            centerY,
                            centerZ
                        ));
                }
            }
            catch (Exception ignored) {
            }
        }
    }

    private void refreshPlayerCache() {
        this.cachedPlayers.clear();
        for (World world : this.server.getWorlds()) {
            this.cachedPlayers.put(world, world.getPlayers());
        }
    }

    private void spawnOrbitalParticles(Present present) {
        CachedLocationData locData = this.locationCache.get(present);

        if (locData == null) {
            try {
                Location location = PositionAdapter.convert(present.getLocation());
                if (location.getWorld() != null) {
                    double centerX = location.getX() + 0.5;
                    double centerY = location.getY() + 0.5;
                    double centerZ = location.getZ() + 0.5;

                    locData = new CachedLocationData(
                        location.getWorld(),
                        centerX,
                        centerY,
                        centerZ
                    );
                    this.locationCache.put(present, locData);
                }
            }
            catch (Exception ignored) {
            }
        }

        if (locData == null || locData.world == null || this.config.particleType() == null) {
            return;
        }

        try {
            org.bukkit.Particle particle = this.config.particleType();
            double radius = this.config.orbitRadius();

            double viewDistanceSq = this.config.viewDistance() * this.config.viewDistance();

            List<Player> playersInWorld = this.cachedPlayers.get(locData.world);
            if (playersInWorld != null) {
                for (Player player : playersInWorld) {
                    if (this.presentService.hasCollectedCached(player.getUniqueId(), present.getId())) {
                        continue;
                    }

                    Location orbitalLocation = new Location(
                        locData.world,
                        locData.centerX,
                        locData.centerY,
                        locData.centerZ
                    );
                    if (player.getLocation().distanceSquared(orbitalLocation) <= viewDistanceSq) {
                        player.spawnParticle(
                            particle,
                            locData.centerX,
                            locData.centerY,
                            locData.centerZ,
                            this.config.particleCount(),
                            radius, radius, radius,
                            this.config.particleSpeed()
                        );
                    }
                }
            }
        }
        catch (Exception exception) {
            // Ignore errors
        }
    }

    private record CachedLocationData(
        World world,
        double centerX,
        double centerY,
        double centerZ
    ) {}

    public record AnimationConfig(
        boolean enabled,
        Particle particleType,
        int particleCount,
        double particleSpeed,
        double orbitRadius,
        double rotationSpeed,
        long tickInterval,
        double viewDistance
    ) {
    }
}
