package hk.ust.comp3021;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    public void processXMLParsingPool(String xmlDirPath, List<String> xmlIDs, int numThread) {
        // TODO: use ParserWorkers and thread pool.
        ExecutorService executor = Executors.newFixedThreadPool(numThread);
        for (String xmlID : xmlIDs) {
            ParserWorker worker = new ParserWorker(xmlID, xmlDirPath, id2ASTModules);
            executor.execute(worker);
        }
        executor.shutdown();
        while (true) {
            if (executor.isTerminated()) {
                break;
            }
        }

    }

    public void processXMLParsingDivide(String xmlDirPath, List<String> xmlIDs, int numThread) {
        // TODO: use ParserWorkers and divide tasks manually.
        List<Thread> threads = new ArrayList<>(numThread);

        for (int i = 0; i < numThread; i++) {
            int finalI = i;
            Thread thread = new Thread(() -> {
                for (int j = finalI; j < xmlIDs.size(); j += numThread) {
                    ParserWorker worker = new ParserWorker(xmlIDs.get(j), xmlDirPath, id2ASTModules);
                    worker.run();
                }
            });
            threads.add(thread);
            thread.start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public List<Object> processCommands(List<Object[]> commands, int executionMode) {
        List<QueryWorker> workers = new ArrayList<>();

        for (Object[] command : commands) {
            QueryWorker worker = new QueryWorker(id2ASTModules, (String) command[0],
                    (String) command[1], (String) command[2], (Object[]) command[3], executionMode);
            workers.add(worker);
        }

        if (executionMode == 0) {
            executeCommandsSerial(workers);
        } else if (executionMode == 1) {
            executeCommandsParallel(workers);
        } else if (executionMode == 2) {
            executeCommandsParallelWithOrder(workers);
        }
        return allResults;
    }

    private void executeCommandsSerial(List<QueryWorker> workers) {
        for (QueryWorker worker : workers) {
            worker.run();
            Object result = worker.getResult();
            allResults.add(result);
        }
    }

    private void executeCommandsParallel(List<QueryWorker> workers) {
        // TODO
        List<Thread> threads = new ArrayList<>();

        for (QueryWorker worker : workers) {
            threads.add(new Thread(worker));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (QueryWorker worker : workers) {
            allResults.add(worker.getResult());
        }
    }

    private void executeCommandsParallelWithOrder(List<QueryWorker> workers) {
        // TODO

    }

    public List<Object> processCommandsInterLeaved(List<Object[]> commands) {
        // TODO
        List<QueryWorker> workers = new ArrayList<>();
        List<ParserWorker> parsers = new ArrayList<>();

        for (Object[] command : commands) {
            if (command[2].equals("processXMLParsing")) {
                ParserWorker parser = new ParserWorker((String) command[1], (String) ((Object[]) command[3])[0], id2ASTModules);
                parsers.add(parser);
            } else {
                QueryWorker worker = new QueryWorker(id2ASTModules, (String) command[0],
                        (String) command[1], (String) command[2], (Object[]) command[3], 0);
                workers.add(worker);
            }
        }
        
        // unordered XML loading
        List<Thread> parserThreads = new ArrayList<>();
        List<Thread> queryThreads = new ArrayList<>();
        
        for (ParserWorker parser : parsers) {
            Thread thread = new Thread(()-> {
                parser.run();
                System.out.println("AST ID Loaded " + parser.getXmlID());
            });
            parserThreads.add(thread);
            thread.start();
        }
        
        for (QueryWorker worker: workers) {
            Thread thread = new Thread(() -> {
                synchronized (id2ASTModules) {
                    if (!id2ASTModules.containsKey(worker.astID) || id2ASTModules.get(worker.astID) == null) {
                        try {
                            System.out.println("AST ID Waiting " + worker.astID);
                            id2ASTModules.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("AST ID Ready " + worker.astID);
                    }
                }
                worker.run();
                System.out.println("Query ID Finish " + worker.queryID);
            });
            queryThreads.add(thread);
            thread.start();
        }

        try {
            for (Thread thread : parserThreads) {
                thread.join();
            }
            for (Thread thread: queryThreads) {
                thread.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (QueryWorker worker : workers) {
            allResults.add(worker.getResult());
        }
        return allResults;
    }
}
