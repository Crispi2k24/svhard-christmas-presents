package pl.svhard.christmas.presents.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Particle;

public class PluginConfig extends OkaeriConfig {

    @Comment("Ustawienia główki prezentu")
    public PresentItemSettings presentItem = new PresentItemSettings();

    @Comment("Ustawienia animacji")
    public AnimationSettings animation = new AnimationSettings();

    @Comment("Nagrody")
    public RewardSettings rewards = new RewardSettings();

    @Comment("Ustawienia efektów fajerwerków")
    public FireworkEffectsSettings fireworkEffects = new FireworkEffectsSettings();

    public static class PresentItemSettings extends OkaeriConfig {
        @Comment("URL tekstury główki (base64)")
        public String texture =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJkOTg3OTJkZDkyZDk3MTk4OTQzNDFhYzkwMTJhNTg0YzQ0Mjg1NThmZDJjNzEyZjc4ZTVmMGQ0ZGE4NTQ3MCJ9fX0=";

        @Comment("Nazwa wyświetlana główki")
        public String displayName = "<b><gradient:#D41F1F:#FF0000:#D41F1F>🎁 Świąteczny Prezent</gradient></b>";
    }

    public static class AnimationSettings extends OkaeriConfig {
        @Comment("Czy animacja jest włączona")
        public boolean enabled = true;

        @Comment("Typ cząsteczki (np. ENCHANTED_HIT, FLAME, HAPPY_VILLAGER, HEART)")
        public String particleType = "HAPPY_VILLAGER";

        @Comment("Ilość cząsteczek na jedno odświeżenie")
        public int particleCount = 2;

        @Comment("Prędkość cząsteczek (0 = stoją w miejscu/bardzo wolne, 1 = szybkie)")
        public double particleSpeed = 0.05;

        @Comment("Promień orbity cząsteczek (w blokach)")
        public double orbitRadius = 0.6;

        @Comment("Prędkość rotacji (stopnie na tick)")
        public double rotationSpeed = 3.0;

        @Comment("Interwał ticków (jak często odświeżać cząsteczki)")
        public long tickInterval = 5;

        @Comment("Dystans widoczności animacji (w blokach)")
        public double viewDistance = 32.0;

        public Particle getParticleType() {
            try {
                return Particle.valueOf(this.particleType.toUpperCase());
            }
            catch (Exception exception) {
                return Particle.HAPPY_VILLAGER;
            }
        }
    }

    public static class RewardSettings extends OkaeriConfig {
        @Comment({
            "Komendy wykonywane za każdy zebrany prezent",
            "Placeholder: {player} - nick gracza, {uuid} - UUID gracza"
        })
        public List<String> perPresentCommands = new ArrayList<>(List.of(
            "give {player} diamond 1",
            "eco give {player} 100"
        ));

        @Comment({
            "Komendy wykonywane po zebraniu wszystkich prezentów",
            "Placeholder: {player} - nick gracza, {uuid} - UUID gracza"
        })
        public List<String> grandPrizeCommands = new ArrayList<>(List.of(
            "give {player} diamond_block 10",
            "eco give {player} 5000"
        ));
    }

    public static class FireworkEffectsSettings extends OkaeriConfig {
        @Comment("Czy efekty fajerwerków są włączone")
        public boolean enabled = true;

        @Comment("Typ efektu (BALL, BALL_LARGE, BURST, CREEPER, STAR)")
        public String effectType = "BALL";

        @Comment({
            "Kolory fajerwerków (RGB w formacie: R,G,B)",
            "Przykład: 255,0,0 (czerwony), 0,255,0 (zielony), 255,255,255 (biały)"
        })
        public List<String> colors = new ArrayList<>(List.of(
            "212,31,31",
            "255,0,0",
            "0,255,0",
            "255,255,255"
        ));

        @Comment({
            "Kolory zanikania (RGB w formacie: R,G,B)",
            "Puste = brak zanikania"
        })
        public List<String> fadeColors = new ArrayList<>(List.of(
            "255,255,0"
        ));

        @Comment("Czy fajerwerk ma zostawić ślad")
        public boolean trail = true;

        @Comment("Czy fajerwerk ma efekt migotania/iskier")
        public boolean flicker = false;

        @Comment("Moc fajerwerku (0-2, 0 = natychmiastowa eksplozja)")
        public int power = 0;

        @Comment("Liczba fajerwerków do wystrzelenia jednocześnie")
        public int count = 1;

        public Type getEffectType() {
            try {
                return Type.valueOf(this.effectType.toUpperCase());
            }
            catch (Exception e) {
                return Type.BALL;
            }
        }

        public List<Color> getColors() {
            List<Color> result = new ArrayList<>();
            for (String colorStr : this.colors) {
                parseColor(colorStr).ifPresent(result::add);
            }
            return result.isEmpty() ? List.of(Color.RED) : result;
        }

        public List<Color> getFadeColors() {
            List<Color> result = new ArrayList<>();
            for (String colorStr : this.fadeColors) {
                parseColor(colorStr).ifPresent(result::add);
            }
            return result;
        }

        private Optional<Color> parseColor(String rgb) {
            try {
                String[] parts = rgb.split(",");
                if (parts.length != 3) {
                    return Optional.empty();
                }

                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());

                return Optional.of(Color.fromRGB(r, g, b));
            }
            catch (Exception exception) {
                return Optional.empty();
            }
        }
    }
}
