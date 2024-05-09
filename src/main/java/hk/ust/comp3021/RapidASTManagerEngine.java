package hk.ust.comp3021;

import hk.ust.comp3021.parallel.*;
import hk.ust.comp3021.utils.*;
import java.util.concurrent.*;
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

    /**
     * TODO: Implement `processXMLParsingPool` to load a list of XML files in parallel
     * 
     * @param xmlDirPath the directory of XML files to be loaded
     * @param xmlIDs a list of XML file IDs
     * @param numThread the number of threads you are allowed to use
     *
     * Hint1: you can use thread pool {@link ExecutorService} to implement the method
     * Hint2: you can use {@link ParserWorker#run()} 
     */
    
    public void processXMLParsingPool(String xmlDirPath, List<String> xmlIDs, int numThread) {
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
    
    /**
     * TODO: Implement `processXMLParsingDivide` to load a list of XML files in parallel
     *
     * @param xmlDirPath the directory of XML files to be loaded
     * @param xmlIDs a list of XML file IDs
     * @param numThread the number of threads you are allowed to use
     *
     * Hint1: you can **only** use {@link Thread} to implement the method
     * Hint2: you can use {@link ParserWorker#run()}
     * Hint3: please distribute the files to be loaded for each thread manually and try to achieve high efficiency
     */
    public void processXMLParsingDivide(String xmlDirPath, List<String> xmlIDs, int numThread) {
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

    /**
     * TODO: Implement `processCommands` to conduct a list of queries on ASTs based on execution mode
     *
     * @param commands a list of queries, you can refer to test cases to learn its format
     * @param executionMode mode 0 to mode 2
     *
     * Hint1: you need to invoke {@link RapidASTManagerEngine#executeCommandsSerial(List)}
     *                           {@link RapidASTManagerEngine#executeCommandsParallel(List)} 
     *                      and {@link RapidASTManagerEngine#executeCommandsParallelWithOrder(List)}
     */
    public List<Object> processCommands(List<Object[]> commands, int executionMode) {
        // TODO 3: handle different execution modes
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

    /**
     * TODO: Implement `executeCommandsSerial` to handle a list of `QueryWorker`
     *
     * @param workers a list of workers that should be executed sequentially
     */
    private void executeCommandsSerial(List<QueryWorker> workers) {
        // TODO 4: sequential execution
        for (QueryWorker worker : workers) {
            worker.run();
            Object result = worker.getResult();
            allResults.add(result);
        }
    }

    /**
     * TODO: Implement `executeCommandsParallel` to handle a list of `QueryWorker`
     *
     * @param workers a list of workers that should be executed in parallel
     * 
     * Hint1: you can **only** use {@link Thread} to implement the method
     * Hint2: you can use unlimited number of threads
     */
    private void executeCommandsParallel(List<QueryWorker> workers) {
        // TODO 5: parallel execution
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

    /**
     * TODO: Implement `executeCommandsParallelWithOrder` to handle a list of `QueryWorker`
     *
     * @param workers a list of workers that should be executed in parallel with correct order
     *
     * Hint1: you can invoke {@link RapidASTManagerEngine#executeCommandsParallel(List)} to reuse its logic
     * Hint2: you can use unlimited number of threads
     * Hint3: please design the order of queries running in parallel based on the calling dependence of method
     *                in queryOnClass
     */
    private void executeCommandsParallelWithOrder(List<QueryWorker> workers) {
        // TODO 6: parallel execution with order
        for (QueryWorker worker : workers) {
            for (QueryWorker worker2 : workers) {
                if (worker.queryID.equals(worker2.queryID))
                    continue;
                if (!worker.astID.equals(worker2.astID)) {
                    continue;
                }  
                boolean runsBefore = false; // whether worker runs before worker2
                if (worker2.queryName.equals("findClassesWithMain")) {
                    if (worker.queryName.equals("findAllMethods")
                            || worker.queryName.equals("findSuperClasses")) {
                        runsBefore = true;
                    }
                } else if (worker.queryName.equals("findSuperClasses")) {
                    if ((worker2.queryName.equals("haveSuperClass") && worker.args[0] == worker2.args[0])
                            || worker2.queryName.equals("findOverridingMethods")
                            || (worker2.queryName.equals("findAllMethods") && worker.args[0] == worker2.args[0])) {
                        runsBefore = true;
                    }
                }
                if (runsBefore) {
                    worker2.addPred();
                    worker.addSucc(worker2);
                }
            }
        }
        executeCommandsParallel(workers);
    }

    /**
     * TODO: Implement `processCommandsInterLeaved` to handle a list of commands
     *
     * @param commands a list of import and query commands that should be executed in parallel 
     *
     * Hint1: you can **only** use {@link Thread} to create threads
     * Hint2: you can use unlimited number of threads
     * Hint3: please design the order of commands, where for specific ID, AST load should be executed before query
     * Hint4: threads would write into/read from {@link RapidASTManagerEngine#id2ASTModules} at the same time, please
     *                 synchronize them carefully
     * Hint5: you can invoke {@link QueryWorker#run()} and {@link ParserWorker#run()}
     * Hint6: order of queries should be consistent to that in given commands, no need to consider 
     *                 redundant computation now
     */
    public List<Object> processCommandsInterLeaved(List<Object[]> commands) {
        // TODO 7: interleaved parsing and query with unlimited threads
        List<QueryWorker> workers = new ArrayList<>();
        List<ParserWorker> parsers = new ArrayList<>();

        for (Object[] command : commands) {
            if (command[2].equals("processXMLParsing")) {
                ParserWorker parser = new ParserWorker((String) command[1], 
                        (String) ((Object[]) command[3])[0], id2ASTModules);
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
            Thread thread = new Thread(() -> {
                parser.run();
//                System.out.println("AST ID Loaded " + parser.getXmlID());
            });
            parserThreads.add(thread);
            thread.start();
        }

        for (QueryWorker worker : workers) {
            Thread thread = new Thread(() -> {
                synchronized (id2ASTModules) {
                    while (!id2ASTModules.containsKey(worker.astID) || id2ASTModules.get(worker.astID) == null) {
                        try {
//                            System.out.println("AST ID Waiting " + worker.astID);
                            id2ASTModules.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
//                    System.out.println("AST ID Ready " + worker.astID);
                }
                worker.run();
//                System.out.println("Query ID Finish " + worker.queryID);
            });
            queryThreads.add(thread);
            thread.start();
        }

        try {
            for (Thread thread : parserThreads) {
                thread.join();
            }
            for (Thread thread : queryThreads) {
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


    /**
     * TODO: Implement `processCommandsInterLeavedTwoThread` to handle a list of commands
     *
     * @param commands a list of import and query commands that should be executed in parallel 
     *
     * Hint1: you can **only** use {@link Thread} to create threads
     * Hint2: you can only use two threads, one for AST load, another for query
     * Hint3: please design the order of commands, where for specific ID, AST load should be executed before query
     * Hint4: threads would write into/read from {@link RapidASTManagerEngine#id2ASTModules} at the same time, please
     *                 synchronize them carefully
     * Hint5: you can invoke {@link QueryWorker#run()} and {@link ParserWorker#run()}
     * Hint6: order of queries should be consistent to that in given commands, no need to consider 
     *                      redundant computation now
     */
    public List<Object> processCommandsInterLeavedTwoThread(List<Object[]> commands) {
        // TODO 8: interleaved parsing and query with two threads
        List<QueryWorker> workers = new ArrayList<>();
        List<ParserWorker> parsers = new ArrayList<>();

        for (Object[] command : commands) {
            if (command[2].equals("processXMLParsing")) {
                ParserWorker parser = new ParserWorker((String) command[1], 
                        (String) ((Object[]) command[3])[0], id2ASTModules);
                parsers.add(parser);
            } else {
                QueryWorker worker = new QueryWorker(id2ASTModules, (String) command[0],
                        (String) command[1], (String) command[2], (Object[]) command[3], 0);
                workers.add(worker);
            }
        }
        
        List<QueryWorker> workerQueue = new ArrayList<>(workers);
        
        Thread parserThread = new Thread(() -> {
            for (ParserWorker parser : parsers) {
                parser.run();
//                System.out.println("AST ID Loaded " + parser.getXmlID());
            }
        });

        Thread queryThread = new Thread(() -> {
            while (!workerQueue.isEmpty()) {
                QueryWorker worker = workerQueue.remove(0);
                if (!id2ASTModules.containsKey(worker.astID)) {
//                    System.out.println("Query ID " + worker.queryID + " Waiting " + worker.astID);
                    workerQueue.add(worker);
                } else {
                    worker.run();
//                    System.out.println("Query ID Finish " + worker.queryID);
                }
            }
        });
        
        parserThread.start();
        queryThread.start();
        try {
            parserThread.join();
            queryThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (QueryWorker worker : workers) {
            allResults.add(worker.getResult());
        }
        return allResults;
    }

    /**
     * TODO: (Bonus) Implement `processCommandsInterLeavedTwoThread` to handle a list of commands
     *
     * @param commands a list of import and query commands that should be executed in parallel 
     * @param numThread number of threads you are allowed to use
     *                 
     * Hint1: you can only distribute commands on your need
     * Hint2: please design the order of commands, where for specific ID, AST load should be executed before query
     * Hint3: threads would write into/read from {@link RapidASTManagerEngine#id2ASTModules} at the same time, please
     *                 synchronize them carefully
     * Hint4: you can invoke {@link QueryWorker#run()} and {@link ParserWorker#run()}
     */
    public List<Object> processCommandsInterLeavedFixedThread(List<Object[]> commands, int numThread) {
        // TODO: Bonus: interleaved parsing and query with given number of threads
        // TODO: separate parser tasks and query tasks with the goal of efficiency
        return allResults;
    }
}
