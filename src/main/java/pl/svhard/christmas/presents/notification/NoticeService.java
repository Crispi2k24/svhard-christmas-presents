package pl.svhard.christmas.presents.notification;

import com.eternalcode.multification.adventure.AudienceConverter;
import com.eternalcode.multification.bukkit.BukkitMultification;
import com.eternalcode.multification.translation.TranslationProvider;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.svhard.christmas.presents.config.PresentMessages;

public class NoticeService extends BukkitMultification<PresentMessages> {

    private final AudienceProvider audienceProvider;
    private final PresentMessages presentMessages;
    private final MiniMessage miniMessage;

    public NoticeService(AudienceProvider audienceProvider, PresentMessages presentMessages, MiniMessage miniMessage) {
        this.audienceProvider = audienceProvider;
        this.presentMessages = presentMessages;
        this.miniMessage = miniMessage;
    }

    @Override
    protected @NotNull TranslationProvider<PresentMessages> translationProvider() {
        return locale -> this.presentMessages;
    }

    @Override
    protected @NotNull ComponentSerializer<Component, Component, String> serializer() {
        return this.miniMessage;
    }

    @Override
    protected @NotNull AudienceConverter<CommandSender> audienceConverter() {
        return commandSender -> {
            if (commandSender instanceof Player player) {
                return this.audienceProvider.player(player.getUniqueId());
            }

            return this.audienceProvider.console();
        };
    }
}
