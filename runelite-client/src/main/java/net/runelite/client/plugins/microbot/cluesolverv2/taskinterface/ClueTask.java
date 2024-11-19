package net.runelite.client.plugins.microbot.cluesolverv2.taskinterface;

public interface ClueTask {
    void start();
    boolean execute(); // Executes the next step in the task, returning true if complete
    void stop();
    String getTaskDescription(); // Provides a description of the current task state
}
