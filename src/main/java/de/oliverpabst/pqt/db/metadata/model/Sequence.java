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

    public Sequence(String _objectName, String _owner, String _acl, String _dataType, Long _currentValue, Long _nextValue,
                    Long _minValue, Long _maxValue, Long _incrementValue,
                    Boolean _isCyclic, Boolean _wasCalled) {
        super(_objectName, _owner, _acl);

        dataType = _dataType;
        currentValue = _currentValue;
        nextValue = _nextValue;
        minimumValue = _minValue;
        maximumValue = _maxValue;
        incrementValue = _incrementValue;
        isCyclic = _isCyclic;
        wasCalled = _wasCalled;
    }

    public Sequence(String _objectName, String _dataType, Long _currentValue,
                    Long _minValue, Long _maxValue, Long _incrementValue) {
        super(_objectName, "", "");

        dataType = _dataType;
        currentValue = _currentValue;
        nextValue = Long.MIN_VALUE;
        minimumValue = _minValue;
        maximumValue = _maxValue;
        incrementValue = _incrementValue;
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
