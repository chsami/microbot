package net.runelite.client.plugins.microbot.util.shop;

public enum Actions {
    VALUE("Value"),
    SELL_ONE("Sell 1"),
    SELL_FIVE("Sell 5"),
    SELL_TEN("Sell 10"),
    SELL_FIFTY("Sell 50");

    private final String action;

    Actions(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}

