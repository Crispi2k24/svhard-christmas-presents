package pl.svhard.christmas.presents.config;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class PresentMessages extends OkaeriConfig {

    @Comment("Wiadomości dla graczy")
    public Notice presentCollected = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <green>Zebrałeś prezent!");

    public Notice alreadyCollected = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <red>Ten prezent został już zebrany!");

    public Notice noPermission = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <red>Nie masz uprawnień do wykonania tej komendy! <gray>({PERMISSION})");

    public Notice correctUsage = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <white>Poprawne użycie: {USAGE}");

    public Notice correctUsageHead = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <white>Poprawne użycie:");

    public Notice correctUsageEntry = Notice.chat("<dark_gray>➤</dark_gray> <white>{USAGE}");

    public Notice completedAll = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <gold>⭐ Gratulacje! Zebrałeś wszystkie prezenty!");

    @Comment("Wiadomość pomocy")
    public Notice helpMessage = Notice.chat(
        "<gradient:#FF0000:#00FF00><b>🎁 Christmas Presents</b></gradient>\n" +
            "<gray>Użycie:\n" +
            "<white>/prezent give <gracz> <gray>- Daj prezent graczowi\n" +
            "<white>/prezent remove <id> <gray>- Usuń prezent\n" +
            "<white>/prezent list <gray>- Lista prezentów\n" +
            "<white>/prezent reload <gray>- Przeładuj config");

    @Comment("Wiadomości dla adminów")
    public Notice presentGiven = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <green>✓ Dano prezent graczowi <white>{PLAYER}");

    public Notice presentGivenReceiver = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <green>🎁 Otrzymałeś świąteczny prezent! Postaw go gdzieś w świecie.");

    public Notice presentPlaced = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <green>✓ Postawiono prezent z ID: <white>{ID}");

    public Notice presentAlreadyExists = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <red>W tym miejscu już znajduje się prezent!");

    public Notice presentRemoved = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <green>✓ Usunięto prezent z ID: <white>{ID}");

    public Notice presentInvalidId = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <red>Nieprawidłowy format ID!");

    public Notice presentListEmpty = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <yellow>Brak aktywnych prezentów.");

    @Comment("Format Action Bar")
    public Notice actionBarFormat = Notice.actionbar("<green>Znalazłeś prezent <white>({COLLECTED}/{TOTAL})");

    @Comment({
        "Format listy prezentów",
        "Dostępne placeholdery: {ID}, {WORLD}, {X}, {Y}, {Z}"
    })
    public String presentListHeader =
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>🎁 ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>┃</dark_gray> <gray>Znaleziono: <white>{COUNT}";

    public String presentListEntry =
        "<dark_gray>▪</dark_gray> <white>ID: <b><gradient:#FF0000:#00FF00>{ID}</gradient></b> <dark_gray>┃</dark_gray> <white>Lokalizacja: <aqua>{WORLD}</aqua> <gray>({X}, {Y}, {Z})";

    public Notice playerNotFound = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <red>Gracz <white>{PLAYER} <red>nie jest online!");

    @Comment("Potwierdzenie usunięcia prezentu przez shift+klik")
    public Notice confirmRemoval = Notice.title(
        "<red><b>⚠ Usunąć prezent?</b>",
        "<gray>Kliknij ponownie (shift+klik), aby potwierdzić",
        java.time.Duration.ofMillis(500),
        java.time.Duration.ofMillis(2000),
        java.time.Duration.ofMillis(500));

    public Notice presentRemovedByInteraction = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <green>✓ Usunięto prezent poprzez interakcję");

    public Notice presentNotFound = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <red>Nie znaleziono prezentu o podanym ID!");

    public Notice reloaded = Notice.chat(
        "<b><gradient:#D41F1F:#FF0000:#D41F1F>ᴘʀᴇᴢᴇɴᴛʏ</gradient></b> <dark_gray>➤</dark_gray> <green>✓ Przeładowano konfigurację w <white>{TIME}ms");
}
