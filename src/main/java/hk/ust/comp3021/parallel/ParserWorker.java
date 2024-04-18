package hk.ust.comp3021.parallel;

import java.nio.file.Paths;
import java.util.HashMap;

import hk.ust.comp3021.utils.ASTModule;
import hk.ust.comp3021.utils.ASTParser;

public class ParserWorker implements Runnable {
    private String xmlID; 
    private String xmlDirPath; 
    
    private HashMap<String, ASTModule> id2ASTModules;

    public ParserWorker(String xmlID, String xmlDirPath, HashMap<String, ASTModule> id2ASTModules) {
        this.xmlID = xmlID;
        this.xmlDirPath = xmlDirPath;
        this.id2ASTModules = id2ASTModules;
    }

    public void run() {
        // TODO:
        ASTParser parser = new ASTParser(Paths.get(xmlDirPath).resolve("python_" + xmlID + ".xml").toString());
        parser.parse();
        if(!parser.isErr()) {
            synchronized(id2ASTModules) {
                id2ASTModules.put(xmlID, parser.getASTModule());
            }
            System.out.println("Parse " + xmlID + " Succeed! The XML file is loaded!");
        } else {
            System.out.println("Parse " + xmlID + " Failed! ");
        }
    }
}
