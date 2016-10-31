package com.mrmq.uyoutube.beans;

public class HandleEvent implements Event {
    private int totalTask = 0;
    private int completedTask = 0;

    public HandleEvent(int totalTask, int completedTask) {
        this.totalTask = totalTask;
        this.completedTask = completedTask;
    }

    public int getTotalTask() {
        return totalTask;
    }

    public void setTotalTask(int totalTask) {
        this.totalTask = totalTask;
    }

    public int getCompletedTask() {
        return completedTask;
    }

    public void setCompletedTask(int completedTask) {
        this.completedTask = completedTask;
    }
}