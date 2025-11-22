package com.realtime.alert_service.model;

public class LastAction {
    private String nameAction;
    private int countAction;

    public LastAction(String nameAction, int countAction) {
        this.nameAction = nameAction;
        this.countAction = countAction;
    }

    public String getNameAction() {
        return nameAction;
    }

    public void setNameAction(String nameAction) {
        this.nameAction = nameAction;
    }

    public int getCountAction() {
        return countAction;
    }

    public void setCountAction(int countAction) {
        this.countAction = countAction;
    }

    public void incrementCountAction(){
        this.countAction++;
    }
}
