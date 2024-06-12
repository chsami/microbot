package net.runelite.client.plugins.hoseaplugins.PiggyUtils.strategy;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TaskManager {

    private List<AbstractTask> tasks = new LinkedList<>();

    public void addTask(AbstractTask task) {
        tasks.add(task);
    }

    public void removeTask(AbstractTask task) {
        tasks.remove(task);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public boolean hasTasks() {
        return !tasks.isEmpty();
    }
    public List<AbstractTask> getTasks() {
        return this.tasks;
    }

    public void runTasks() {
        for (AbstractTask task : tasks) {
            if (task.validate()) {
                task.execute();
                break;
            }
        }
    }
}
