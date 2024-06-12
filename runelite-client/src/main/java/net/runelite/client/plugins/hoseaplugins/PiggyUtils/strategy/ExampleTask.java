package net.runelite.client.plugins.hoseaplugins.PiggyUtils.strategy;

import net.runelite.client.config.Config;
import net.runelite.client.plugins.Plugin;

//do something like this to pass in your specific plugin and config
//extends AbstractTask<StrategySmithPlugin, StrategySmithConfig>
public class ExampleTask extends AbstractTask {
    public ExampleTask( Plugin plugin, Config config) {
        super( plugin, config);
    }

    /**
     * If this returns true, this task will execute
     * @return
     */
    @Override
    public boolean validate() {
        return false;
    }

    /**
     * This is the code that will be executed when validate returns true
     */
    @Override
    public void execute() {

        interactNpc("Goblin", "Attack", true);

    }
}
