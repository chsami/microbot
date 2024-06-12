package net.runelite.client.plugins.ChinBreakHandler.ui;

public enum LoginMode {
    MANUAL,
    PROFILES,
    LAUNCHER;

    public static LoginMode parse(String s) {
        if (s == null || s.isEmpty()) {
            return LoginMode.MANUAL;
        }

        for (LoginMode mode : LoginMode.values()) {
            if (s.equalsIgnoreCase(mode.name())) {
                return mode;
            }
        }

        return LoginMode.MANUAL;
    }
}
