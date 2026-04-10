package pl.svhard.christmas.presents.commands.handler;

import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.command.CommandSender;
import pl.svhard.christmas.presents.notification.NoticeService;

public class MissingPermissionHandlerImpl implements MissingPermissionsHandler<CommandSender> {

    private final NoticeService noticeService;

    public MissingPermissionHandlerImpl(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Override
    public void handle(
        Invocation<CommandSender> invocation,
        MissingPermissions missingPermissions,
        ResultHandlerChain<CommandSender> chain
    ) {

        String joinedText = missingPermissions.asJoinedText();

        this.noticeService
            .create()
            .notice(notice -> notice.noPermission)
            .placeholder("{PERMISSION}", joinedText)
            .viewer(invocation.sender())
            .send();
    }
}
