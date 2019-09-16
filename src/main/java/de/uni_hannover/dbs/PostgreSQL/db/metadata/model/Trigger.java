package de.uni_hannover.dbs.PostgreSQL.db.metadata.model;

import java.util.ArrayList;

public class Trigger extends DatabaseObject {

    private final Integer argumentCount;
    private final ArrayList<String> arguments;
    private final ArrayList<String> signatureArguments;
    private final String returnValue;
    private final String language;
    private final String triggerDefinition;

    public Trigger(String _objectName, String _owner, String _acl, Integer _argument_count, ArrayList<String> _arguments,
                   ArrayList<String> _signatureArguments, String _returnValue, String _language, String _triggerDefinition) {
        super(_objectName, _owner, _acl);
        argumentCount = _argument_count;
        arguments = _arguments;
        signatureArguments = _signatureArguments;
        returnValue = _returnValue;
        language = _language;
        triggerDefinition = _triggerDefinition;
    }

    public Integer getArgumentCount() {
        return argumentCount;
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }

    public ArrayList<String> getSignatureArguments() {
        return signatureArguments;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public String getLanguage() {
        return language;
    }

    public String getTriggerDefinition() {
        return triggerDefinition;
    }

    @Override
    public DatabaseObjectTypes getObjectType() {
        return DatabaseObjectTypes.TRIGGER;
    }
}
