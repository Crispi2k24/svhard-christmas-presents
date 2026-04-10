package pl.svhard.christmas.presents.controller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.svhard.christmas.presents.config.PluginConfig;
import pl.svhard.christmas.presents.event.AllPresentsCollectedEvent;
import pl.svhard.christmas.presents.event.PresentCollectedEvent;
import pl.svhard.christmas.presents.reward.RewardExecutor;

public class PresentRewardController implements Listener {

    private final RewardExecutor rewardExecutor;
    private final PluginConfig config;

    public PresentRewardController(RewardExecutor rewardExecutor, PluginConfig config) {
        this.rewardExecutor = rewardExecutor;
        this.config = config;
    }

    @EventHandler
    public void onPresentCollected(PresentCollectedEvent event) {
        this.rewardExecutor.executePerPresentRewards(
            event.getPlayer(),
            this.config.rewards.perPresentCommands
        );
    }

    @EventHandler
    public void onAllPresentsCompleted(AllPresentsCollectedEvent event) {
        this.rewardExecutor.executeGrandPrizeRewards(
            event.getPlayer(),
            this.config.rewards.grandPrizeCommands
        );
    }
}
