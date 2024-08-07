package net.runelite.client.plugins.microbot.roguesden.steps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.plugins.microbot.roguesden.model.BotApi;

@Getter
@AllArgsConstructor
public abstract class Step {
    protected BotApi botApi;
    protected String name;

    public abstract void execute();
}
