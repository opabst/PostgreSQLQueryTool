package de.oliverpabst.pqt.db.metadata.model.table;

public class Trigger extends TableObject {
    private final String triggerName;
    private final Boolean isConstraint;
    private final String fires;
    private final String triggerEvent;
    private final String forEach;
    private final String function;
    private final Boolean isActive;

    public Trigger(String _triggerName, Boolean _isConstraint, String _fires, String _triggerEvent, String _forEach, String _function, Boolean _isActive) {
        triggerName = _triggerName;
        isConstraint = _isConstraint;
        fires = _fires;
        triggerEvent = _triggerEvent;
        forEach = _forEach;
        function = _function;
        isActive = _isActive;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public Boolean getIsConstraint() {
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

    public Boolean getActive() {
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
