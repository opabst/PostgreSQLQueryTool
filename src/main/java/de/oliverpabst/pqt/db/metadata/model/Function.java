package de.oliverpabst.pqt.db.metadata.model;


import java.util.ArrayList;

public class Function extends DatabaseObject {
    private final int argumentCount;
    private final ArrayList<String> arguments;
    private final ArrayList<String> signatureArguments;
    private final String returnType;
    private final String language;
    private final String functionDefinition;

    public Function(String objectName, String returnType, String language, String functionDefinition) {
        super(objectName, "", "");

        argumentCount = -1;
        arguments = new ArrayList<>();
        signatureArguments = new ArrayList<>();
        this.returnType = returnType;
        this.language = language;
        this.functionDefinition = functionDefinition;
    }

    public Function(String objectName, String owner, String acl, int argumentCount, ArrayList<String> arguments,
                    ArrayList<String> signatureArguments, String returnType, String language, String functionDefinition) {
        super(objectName, owner, acl);

        this.argumentCount = argumentCount;
        this.arguments = arguments;
        this.signatureArguments = signatureArguments;
        this.returnType = returnType;
        this.language = language;
        this.functionDefinition = functionDefinition;
    }

    public int getArgumentCount() {
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
