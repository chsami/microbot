package net.runelite.client.plugins.microbot.bankjs.BanksShopper;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Actions {
    BUY("Buy"),
    SELL("Sell");

    private final String action;

    @Override
    public String toString() {
        return action;
    }
}
