package com.company.tasks;

public enum TaskType {
    UPDATE_NAMES("updateNames", new NamesUpdater());

    private String command;
    private BotTask task;

    TaskType(String command, BotTask task) {
        this.command = command;
        this.task = task;
    }

    public String getCommand() {
        return command;
    }

    public void runTask() {
        task.run();
    }
}
