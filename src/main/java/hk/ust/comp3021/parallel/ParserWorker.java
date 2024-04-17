package hk.ust.comp3021.parallel;

import java.util.HashMap;

import hk.ust.comp3021.utils.ASTModule;

public class ParserWorker {
    private String xmlID; 
    
    private HashMap<String, ASTModule> id2ASTModules;

    public ParserWorker(String xmlID, HashMap<String, ASTModule> id2ASTModules) {
        this.xmlID = xmlID;
        this.id2ASTModules = id2ASTModules;
    }

    public void run() {
        // TODO
    }
}
