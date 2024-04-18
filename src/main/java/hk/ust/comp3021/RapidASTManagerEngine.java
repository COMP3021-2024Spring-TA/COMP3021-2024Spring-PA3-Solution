package hk.ust.comp3021;

import java.util.List;
import java.util.stream.Stream;

import hk.ust.comp3021.parallel.ParserWorker;
import hk.ust.comp3021.parallel.QueryWorker;
import hk.ust.comp3021.query.*;
import hk.ust.comp3021.utils.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;


public class RapidASTManagerEngine {
    private final HashMap<String, ASTModule> id2ASTModules = new HashMap<>();
    private final List<Object> allResults = new ArrayList<>();

    public HashMap<String, ASTModule> getId2ASTModule() {
        return id2ASTModules;
    }

    public List<Object> getAllResults() {
        return allResults;
    }

    public void processXMLParsing(String xmlDirPath, List<String> xmlIDs) {
        // TODO: use ParserWorkers.
        List<Thread> threads = new ArrayList<>();
        for(String xmlID: xmlIDs) {
            ParserWorker worker = new ParserWorker(xmlID, xmlDirPath, id2ASTModules);
            threads.add(new Thread(worker));
        }

        for(Thread thread : threads) {
            thread.start();
        }

        try {
            for(Thread thread : threads) {
                thread.join();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public List<Object> processCommands(List<Object[]> commands, int executionMode) {
        List<QueryWorker> workers = new ArrayList<>();

        for(Object[] command : commands) {
            QueryWorker worker = new QueryWorker(id2ASTModules, (String)command[0], (String)command[1], (String) command[2], (Object[])command[3], executionMode);
            workers.add(worker);
        }

        if(executionMode == 0) {
            executeCommandsSerial(workers);
        } else if(executionMode == 1) {
            executeCommandsParallel(workers);
        } else if(executionMode == 2)  {
            executeCommandsParallelWithOrder(workers);
        }
        return allResults;
    }

    private void executeCommandsSerial(List<QueryWorker> workers) {
        for(QueryWorker worker : workers) {
            worker.run();
            Object result = worker.getResult();
            allResults.add(result);
        }
    }

    private void executeCommandsParallel(List<QueryWorker> workers) {
        // TODO
        List<Thread> threads = new ArrayList<>();

        for(QueryWorker worker : workers) {
            threads.add(new Thread(worker));
        }

        for(Thread thread : threads) {
            thread.start();
        }

        try {
            for(Thread thread : threads) {
                thread.join();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        for(QueryWorker worker : workers) {
            allResults.add(worker.getResult());
        }

    }

    private void executeCommandsParallelWithOrder(List<QueryWorker> workers) {
        // TODO

    }

}
