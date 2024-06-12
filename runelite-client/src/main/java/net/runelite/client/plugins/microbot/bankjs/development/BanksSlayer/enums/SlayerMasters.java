package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SlayerMasters {
    TURAEL("Turael"),
    NIEVE("Nieve"),
    STEVE("Steve");

    private final String slayerMaster;

    @Override
    public String toString() {
        return slayerMaster;
    }
}
