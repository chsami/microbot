package net.runelite.client.plugins.microbot.dashboard;

public class PluginRequestModel {
    String name;
    boolean active;


    public PluginRequestModel(String name, boolean active) {
        this.name = name;
        this.active = active;
    }
}
