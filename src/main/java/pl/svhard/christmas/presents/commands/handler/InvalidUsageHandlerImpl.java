package pl.svhard.christmas.presents.commands.handler;

import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;
import pl.svhard.christmas.presents.notification.NoticeService;

public class InvalidUsageHandlerImpl implements InvalidUsageHandler<CommandSender> {

    private final NoticeService noticeService;

    public InvalidUsageHandlerImpl(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Override
    public void handle(
        Invocation<CommandSender> invocation,
        InvalidUsage<CommandSender> result,
        ResultHandlerChain<CommandSender> chain
    ) {
        Schematic schematic = result.getSchematic();

        if (schematic.isOnlyFirst()) {
            this.noticeService
                .create()
                .notice(notice -> notice.correctUsage)
                .placeholder("{USAGE}", schematic.first())
                .viewer(invocation.sender())
                .send();

            return;
        }

        this.noticeService
            .create()
            .notice(notice -> notice.correctUsageHead)
            .placeholder("{USAGE}", schematic.first())
            .viewer(invocation.sender())
            .send();

        for (String usage : schematic.all()) {
            this.noticeService
                .create()
                .notice(notice -> notice.correctUsageEntry)
                .placeholder("{USAGE}", usage)
                .viewer(invocation.sender())
                .send();
        }
    }
}
