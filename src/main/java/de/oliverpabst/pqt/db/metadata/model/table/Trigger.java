package de.oliverpabst.pqt.db.metadata.model.table;

public class Trigger extends TableObject {
    private final String triggerName;
    private final boolean isConstraint;
    private final String fires;
    private final String triggerEvent;
    private final String forEach;
    private final String function;
    private final boolean isActive;

    public Trigger(String triggerName, boolean isConstraint, String fires, String triggerEvent, String forEach, String function, boolean isActive) {
        this.triggerName = triggerName;
        this.isConstraint = isConstraint;
        this.fires = fires;
        this.triggerEvent = triggerEvent;
        this.forEach = forEach;
        this.function = function;
        this.isActive = isActive;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public boolean getIsConstraint() {
        return isConstraint;
    }

    public String getFires() {
        return fires;
    }

    public String getTriggerEvent() {
        return triggerEvent;
    }

    public String getForEach() {
        return forEach;
    }

    public String getFunction() {
        return function;
    }

    public boolean getActive() {
        return isActive;
    }

    @Override
    public String getTableObjectName() {
        return triggerName;
    }

    @Override
    public TableObjectTypes getTableObjectType() {
        return TableObjectTypes.TRIGGER;
    }
}
