package pl.svhard.christmas.presents.present;

import java.util.List;
import java.util.Objects;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import pl.svhard.christmas.presents.config.PluginConfig;

public final class PresentEffectsService {

    private final PluginConfig config;

    public PresentEffectsService(PluginConfig config) {
        this.config = config;
    }

    public void spawnCollectionFirework(Location location) {
        if (!this.config.fireworkEffects.enabled) {
            return;
        }

        Location spawnLocation = location.clone().add(0.5, 0.5, 0.5);

        for (int i = 0; i < this.config.fireworkEffects.count; i++) {
            Firework firework = (Firework) Objects.requireNonNull(spawnLocation.getWorld())
                .spawnEntity(spawnLocation, EntityType.FIREWORK_ROCKET);

            FireworkMeta meta = firework.getFireworkMeta();

            FireworkEffect.Builder effectBuilder = FireworkEffect.builder()
                .with(this.config.fireworkEffects.getEffectType())
                .trail(this.config.fireworkEffects.trail)
                .flicker(this.config.fireworkEffects.flicker);

            List<Color> colors = this.config.fireworkEffects.getColors();
            if (!colors.isEmpty()) {
                effectBuilder.withColor(colors);
            }

            List<Color> fadeColors = this.config.fireworkEffects.getFadeColors();
            if (!fadeColors.isEmpty()) {
                effectBuilder.withFade(fadeColors);
            }

            meta.addEffect(effectBuilder.build());
            meta.setPower(this.config.fireworkEffects.power);

            firework.setFireworkMeta(meta);

            firework.setSilent(false);

            if (this.config.fireworkEffects.power == 0) {
                firework.detonate();
            }

            try {
                firework.setPersistent(false);
                firework.setTicksToDetonate(0);
            }
            catch (Exception ignored) {
                // Fallback for non-Paper servers
            }
        }
    }
}
