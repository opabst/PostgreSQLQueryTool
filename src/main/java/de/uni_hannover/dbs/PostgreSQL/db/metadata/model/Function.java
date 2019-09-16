package de.uni_hannover.dbs.PostgreSQL.db.metadata.model;


import java.util.ArrayList;

public class Function extends DatabaseObject {
    private final Integer argumentCount;
    private final ArrayList<String> arguments;
    private final ArrayList<String> signatureArguments;
    private final String returnType;
    private final String language;
    private final String functionDefinition;

    public Function(String _objectName, String _returnType, String _language, String _functionDefinition) {
        super(_objectName, "", "");

        argumentCount = -1;
        arguments = new ArrayList<>();
        signatureArguments = new ArrayList<>();
        returnType = _returnType;
        language = _language;
        functionDefinition = _functionDefinition;
    }

    public Function(String _objectName, String _owner, String _acl, Integer _argumentCount, ArrayList<String> _arguments,
                    ArrayList<String> _signatureArguments, String _returnType, String _language, String _functionDefinition) {
        super(_objectName, _owner, _acl);

        argumentCount = _argumentCount;
        arguments = _arguments;
        signatureArguments = _signatureArguments;
        returnType = _returnType;
        language = _language;
        functionDefinition = _functionDefinition;
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

    public String getReturnType() {
        return returnType;
    }

    public String getFunctionDefinition() {
        return functionDefinition;
    }

    public String getLanguage() {
        return language;
    }

    public DatabaseObjectTypes getObjectType() {
        return DatabaseObjectTypes.FUNCTION;
    }
}
