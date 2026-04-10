package pl.svhard.christmas.presents.present;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class PresentItemFactory {

    private static final NamespacedKey PRESENT_KEY = new NamespacedKey("svhard", "christmas_present");
    private static final byte PRESENT_MARKER = 1;

    private final MiniMessage miniMessage;
    private final String textureUrl;
    private final String displayName;
    private ItemStack cachedHead;

    public PresentItemFactory(MiniMessage miniMessage, String textureUrl, String displayName) {
        this.miniMessage = miniMessage;
        this.textureUrl = textureUrl;
        this.displayName = displayName;
    }

    public CompletableFuture<ItemStack> createPresentHead() {
        if (this.cachedHead != null) {
            return CompletableFuture.completedFuture(this.cachedHead.clone());
        }

        return CompletableFuture.supplyAsync(() -> {
            ItemStack item = ItemBuilder.skull()
                .texture(this.textureUrl)
                .name(this.miniMessage.deserialize(this.displayName))
                .build();

            item.editMeta(
                org.bukkit.inventory.meta.SkullMeta.class, meta -> {
                    meta.getPersistentDataContainer().set(PRESENT_KEY, PersistentDataType.BYTE, PRESENT_MARKER);
                });

            this.cachedHead = item.clone();
            return item;
        });
    }

    public boolean isPresentHead(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        return item.getItemMeta()
            .getPersistentDataContainer()
            .has(PRESENT_KEY, PersistentDataType.BYTE);
    }
}
