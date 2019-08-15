package de.uni_hannover.dbs.PostgreSQL.db.metadata.model;

public class Sequence extends DatabaseObject {
    private final String dataType;
    private final Integer currentValue;
    private final Integer nextValue;
    private final Integer minimumValue;
    private final Integer maximumValue;
    private final Integer incrementValue;
    private final Boolean isCyclic;
    private final Boolean wasCalled;

    public Sequence(String _objectName, String _owner, String _acl, Integer _currentValue, Integer _nextValue,
                    Integer _minValue, Integer _maxValue, Integer _incrementValue,
                    Boolean _isCyclic, Boolean _wasCalled) {
        super(_objectName, _owner, _acl);

        dataType = ""; //TODO: Fix this!!
        currentValue = _currentValue;
        nextValue = _nextValue;
        minimumValue = _minValue;
        maximumValue = _maxValue;
        incrementValue = _incrementValue;
        isCyclic = _isCyclic;
        wasCalled = _wasCalled;
    }

    public Sequence(String _objectName, String _dataType, Integer _currentValue, Integer _nextValue,
                    Integer _minValue, Integer _maxValue, Integer _incrementValue) {
        super(_objectName, "", "");

        dataType = _dataType;
        currentValue = _currentValue;
        nextValue = _nextValue;
        minimumValue = _minValue;
        maximumValue = _maxValue;
        incrementValue = _incrementValue;
        isCyclic = false;
        wasCalled = false;
    }

    public Integer getCurrentValue() {
        return currentValue;
    }

    public Integer getNextValue() {
        return nextValue;
    }

    public Integer getMinimumValue() {
        return minimumValue;
    }

    public Integer getMaximumValue() {
        return maximumValue;
    }

    public Integer getIncrementValue() {
        return incrementValue;
    }

    public Boolean getIsCyclic() {
        return isCyclic;
    }

    public Boolean getWasCalled() {
        return wasCalled;
    }
}
