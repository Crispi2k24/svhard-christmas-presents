package pl.svhard.christmas.presents.reward;

import java.util.List;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class RewardExecutor {

    private final Server server;

    public RewardExecutor(Server server) {
        this.server = server;
    }

    public void executePerPresentRewards(Player player, List<String> commands) {
        this.executeCommands(player, commands);
    }

    public void executeGrandPrizeRewards(Player player, List<String> commands) {
        this.executeCommands(player, commands);
    }

    private void executeCommands(Player player, List<String> commands) {
        for (String command : commands) {
            String processedCommand = command
                .replace("{player}", player.getName())
                .replace("{uuid}", player.getUniqueId().toString());

            this.server.dispatchCommand(this.server.getConsoleSender(), processedCommand);
        }
    }
}
