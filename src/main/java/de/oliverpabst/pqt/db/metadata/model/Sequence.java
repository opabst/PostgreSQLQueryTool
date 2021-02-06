package de.oliverpabst.pqt.db.metadata.model;

public class Sequence extends DatabaseObject {
    private final String dataType;
    private final Long currentValue;
    private final Long nextValue;
    private final Long minimumValue;
    private final Long maximumValue;
    private final Long incrementValue;
    private final Boolean isCyclic;
    private final Boolean wasCalled;

    public Sequence(final String objectName, final String owner, final String acl, final String dataType,
                    final Long currentValue, final Long nextValue, final Long minValue, final Long maxValue,
                    final Long incrementValue, final Boolean isCyclic, final Boolean wasCalled) {
        super(objectName, owner, acl);

        this.dataType = dataType;
        this.currentValue = currentValue;
        this.nextValue = nextValue;
        minimumValue = minValue;
        maximumValue = maxValue;
        this.incrementValue = incrementValue;
        this.isCyclic = isCyclic;
        this.wasCalled = wasCalled;
    }

    public Sequence(final String objectName, final String dataType, final Long currentValue,
                    final Long minValue, final Long maxValue, final Long incrementValue) {
        super(objectName, "", "");

        this.dataType = dataType;
        this.currentValue = currentValue;
        nextValue = Long.MIN_VALUE;
        minimumValue = minValue;
        maximumValue = maxValue;
        this.incrementValue = incrementValue;
        isCyclic = false;
        wasCalled = false;
    }

    public Long getCurrentValue() {
        return currentValue;
    }

    public Long getNextValue() {
        return nextValue;
    }

    public Long getMinimumValue() {
        return minimumValue;
    }

    public Long getMaximumValue() {
        return maximumValue;
    }

    public Long getIncrementValue() {
        return incrementValue;
    }

    public Boolean getIsCyclic() {
        return isCyclic;
    }

    public Boolean getWasCalled() {
        return wasCalled;
    }

    public String getDataType() {
        return dataType;
    }

    public DatabaseObjectTypes getObjectType() {
        return DatabaseObjectTypes.SEQUENCE;
    }
}
