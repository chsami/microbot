package net.runelite.client.plugins.microbot.bankjs.development.BanksShopper;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Quantities {
    ONE("1"),
    FIVE("5"),
    TEN("10"),
    FIFTY("50");

    private final String action;

    @Override
    public String toString() {
        return action;
    }
}
