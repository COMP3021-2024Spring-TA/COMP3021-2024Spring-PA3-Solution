package hk.ust.comp3021;

import java.util.List;
import java.util.stream.Stream;

import hk.ust.comp3021.query.*;
import hk.ust.comp3021.utils.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;


public class RapidASTManagerEngine {
    private final HashMap<String, ASTModule> id2ASTModules = new HashMap<>();
    private final List<Object> allResults = new ArrayList<>();


    public void processXMLParsing(String xmlDirPath, List<String> xmlIDs) {
        // TODO
    }

    public List<Object> processCommands(List<Object[]> commands, int executionMode) {
        List<Worker> workers = new ArrayList<>();

        for(Object[] command : commands) {
            Worker worker = new Worker((String)command[0], (String)command[1], (String)command[2], (Object[])command[3], executionMode);
            workers.add(worker);
        }

        if(executionMode == 0) {
            executeCommandsSerial(workers);
        } else if(executionMode == 1) {
            executeCommandsParallelNoDep(workers);
        } else if(executionMode == 2) {
            executeCommandsParallelFileDep(workers);
        } else {
            executeCommandsParallelQueryDep(workers);
        }
        return allResults;
    }

    private void executeCommandsSerial(List<Worker> workers) {
        for(Worker worker : workers) {
            worker.run();
        }
    }

    private void executeCommandsParallelNoDep(List<Worker> workers) {
        // TODO
    }

    private void executeCommandsParallelFileDep(List<Worker> workers) {
        // TODO
    }

    private void executeCommandsParallelQueryDep(List<Worker> workers) {
        // TODO
    }

}
