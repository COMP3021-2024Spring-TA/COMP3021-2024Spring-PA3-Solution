package hk.ust.comp3021.parallel;

import hk.ust.comp3021.query.*;
import hk.ust.comp3021.utils.*;

import java.util.*;
import java.util.Map.*;

public class QueryWorker implements Runnable {
    public HashMap<String, ASTModule> id2ASTModules;
    public String queryID;
    public String astID;
    public String queryName;
    public Object[] args;
    public int mode;
    private Object result;
    private Integer preds = 0;  // TODO: REMOVE for skeleton
    private final List<QueryWorker> successors = new ArrayList<>(); // TODO: REMOVE for skeleton

    public QueryWorker(HashMap<String, ASTModule> id2ASTModules,
                       String queryID, String astID,
                       String queryName, Object[] args, int mode) {
        this.id2ASTModules = id2ASTModules;
        this.queryID = queryID;
        this.astID = astID;
        this.queryName = queryName;
        this.args = args;
        this.mode = mode;
    }

    public Object getResult() {
        return result;
    }

    public void run() {
        if (mode == 0) {
            runSerial();
        } else if (mode == 1) {
            runParallel();
        } else if (mode == 2) {
            runParallelWithOrder();
        }
    }

    /**
     * TODO: Implement `runSerial` to process current query command and store the results in `result`
     *
     * Hint1: you must invoke the methods in {@link QueryOnNode}, {@link QueryOnMethod} and {@link QueryOnClass}
     * to achieve the query
     */
    private void runSerial() {
        switch (queryName) {
            case "findFuncWithArgGtN": {
                QueryOnNode query = new QueryOnNode(id2ASTModules);
                this.result = query.findFuncWithArgGtN.apply((Integer) args[0]);
                break;
            }
            case "calculateOp2Nums": {
                QueryOnNode query = new QueryOnNode(id2ASTModules);
                this.result = query.calculateOp2Nums.get();
                break;
            }
            case "processNodeFreq": {
                QueryOnNode query = new QueryOnNode(id2ASTModules);
                this.result = query.processNodeFreq.get();
                break;
            }
            case "calculateNode2Nums": {
                QueryOnNode query = new QueryOnNode(id2ASTModules);
                this.result = query.calculateNode2Nums.apply((String) args[0]);
                break;
            }

            case "findEqualCompareInFunc": {
                QueryOnMethod query = new QueryOnMethod(id2ASTModules.get(astID));
                this.result = query.findEqualCompareInFunc.apply((String) args[0]);
                break;
            }
            case "findFuncWithBoolParam": {
                QueryOnMethod query = new QueryOnMethod(id2ASTModules.get(astID));
                this.result = query.findFuncWithBoolParam.get();
                break;
            }
            case "findUnusedParamInFunc": {
                QueryOnMethod query = new QueryOnMethod(id2ASTModules.get(astID));
                this.result = query.findUnusedParamInFunc.apply((String) args[0]);
                break;
            }
            case "findDirectCalledOtherB": {
                QueryOnMethod query = new QueryOnMethod(id2ASTModules.get(astID));
                this.result = query.findDirectCalledOtherB.apply((String) args[0]);
                break;
            }
            case "answerIfACalledB": {
                QueryOnMethod query = new QueryOnMethod(id2ASTModules.get(astID));
                this.result = query.answerIfACalledB.test((String) args[0], (String) args[1]);
                break;
            }

            case "findSuperClasses": {
                QueryOnClass query = new QueryOnClass(id2ASTModules.get(astID));
                this.result = query.findSuperClasses.apply((String) args[0]);
                break;
            }
            case "haveSuperClass": {
                QueryOnClass query = new QueryOnClass(id2ASTModules.get(astID));
                this.result = query.haveSuperClass.apply((String) args[0], (String) args[1]);
                break;
            }
            case "findOverridingMethods": {
                QueryOnClass query = new QueryOnClass(id2ASTModules.get(astID));
                this.result = query.findOverridingMethods.get();
                break;
            }
            case "findAllMethods": {
                QueryOnClass query = new QueryOnClass(id2ASTModules.get(astID));
                this.result = query.findAllMethods.apply((String) args[0]);
                break;
            }
            case "findClassesWithMain": {
                QueryOnClass query = new QueryOnClass(id2ASTModules.get(astID));
                this.result = query.findClassesWithMain.get();
                break;
            }
        }
    }

    /**
     * TODO: Implement `runParallel` to process current query command and store the results in `result` where
     * queryOnNode should be conducted with multiple threads
     *
     * Hint1: you must invoke the methods in {@link QueryOnNode}, {@link QueryOnMethod} and {@link QueryOnClass}
     * to achieve the query
     * Hint2: you can let methods in queryOnNode to work on single AST by changing the arguments when creating 
     * {@link QueryOnNode} object
     * Hint3: please use {@link Thread} to achieve multi-threading
     * Hint4: you can invoke {@link QueryWorker#runSerial()} to reuse its logic
     */
    private void runParallel() {
        // TODO: parallel the query on node 
        switch (queryName) {
            case "findFuncWithArgGtN": {
                HashMap<String, List<String>> id2PartialResults = new HashMap<>();
                List<Thread> subThreads = new ArrayList<>();
                for (String id : id2ASTModules.keySet()) {
                    HashMap<String, ASTModule> partialInput = new HashMap<>();
                    partialInput.put(id, id2ASTModules.get(id));
                    Thread subThread = new Thread(() -> {
                        QueryOnNode query = new QueryOnNode(partialInput);
                        List<String> partialResult = query.findFuncWithArgGtN.apply((Integer) args[0]);
                        id2PartialResults.put(id, partialResult);
                    });
                    subThreads.add(subThread);
                    subThread.start();
                }
                try {
                    for (Thread subThread: subThreads) {
                        subThread.join();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List<String> result = new ArrayList<>();
                for (List<String> partialRes: id2PartialResults.values()) {
                    result.addAll(partialRes);
                }
                this.result = result;
                break;
            }
            case "calculateOp2Nums": {
                HashMap<String, HashMap<String, Integer>> id2PartialResults = new HashMap<>();
                List<Thread> subThreads = new ArrayList<>();
                for (String id : id2ASTModules.keySet()) {
                    HashMap<String, ASTModule> partialInput = new HashMap<>();
                    partialInput.put(id, id2ASTModules.get(id));
                    Thread subThread = new Thread(() -> {
                        QueryOnNode query = new QueryOnNode(partialInput);
                        HashMap<String, Integer> partialResult = query.calculateOp2Nums.get();
                        id2PartialResults.put(id, partialResult);
                    });
                    subThreads.add(subThread);
                    subThread.start();
                }
                try {
                    for (Thread subThread : subThreads) {
                        subThread.join();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HashMap<String, Integer> result = new HashMap<>();
                for (HashMap<String, Integer> partialRes : id2PartialResults.values()) {
                    for (String key : partialRes.keySet()) {
                        Integer initVal = 0;
                        if (result.containsKey(key)) {
                            initVal = result.get(key);
                        }
                        result.put(key, initVal + partialRes.get(key));
                    }
                }
                this.result = result;
                break;
            }
            case "processNodeFreq": {
                HashMap<String, List<Entry<String, Integer>>> id2PartialResults = new HashMap<>();
                List<Thread> subThreads = new ArrayList<Thread>();
                for (String id : id2ASTModules.keySet()) {
                    HashMap<String, ASTModule> partialInput = new HashMap<>();
                    partialInput.put(id, id2ASTModules.get(id));
                    Thread subThread = new Thread(() -> {
                        QueryOnNode query = new QueryOnNode(partialInput);
                        List<Entry<String, Integer>> partialResult = query.processNodeFreq.get();
                        id2PartialResults.put(id, partialResult);

                    });
                    subThreads.add(subThread);
                    subThread.start();
                }
                try {
                    for (Thread subThread : subThreads) {
                        subThread.join();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                HashMap<String, Integer> counter = new HashMap<>();

                for (List<Entry<String, Integer>> partialRes : id2PartialResults.values()) {
                    for (Entry<String, Integer> entry : partialRes) {
                        String key = entry.getKey();
                        Integer initValue = 0;
                        if (counter.containsKey(key)) {
                            initValue = counter.get(key);
                        }
                        counter.put(key, initValue + entry.getValue());
                    }
                }
                Set<Entry<String, Integer>> entrySet = counter.entrySet();
                this.result = new ArrayList<Entry<String, Integer>>(entrySet);
                break;
            }
            case "calculateNode2Nums": {
                HashMap<String, Map<String, Long>> id2PartialResults = new HashMap<>();
                List<Thread> subThreads = new ArrayList<Thread>();
                for (String id : id2ASTModules.keySet()) {
                    HashMap<String, ASTModule> partialInput = new HashMap<>();
                    partialInput.put(id, id2ASTModules.get(id));
                    Thread subThread = new Thread(() -> {
                        QueryOnNode query = new QueryOnNode(partialInput);
                        Map<String, Long> partialResult = query.calculateNode2Nums.apply((String) args[0]);
                        id2PartialResults.put(id, partialResult);

                    });
                    subThreads.add(subThread);
                    subThread.start();
                }
                try {
                    for (Thread subThread : subThreads) {
                        subThread.join();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Map<String, Long> result = new HashMap<>();
                for (Map<String, Long> partialRes : id2PartialResults.values()) {
                    for (String key : partialRes.keySet()) {
                        Long initVal = 0L;
                        if (result.containsKey(key)) {
                            initVal = result.get(key);
                        }
                        result.put(key, initVal + partialRes.get(key));
                    }
                }
                this.result = result;
                break;
            }
            default: {
                runSerial();
            }
        }
    }


    public void addPred() {  // REMOVE
        this.preds += 1;
    }

    public void addSucc(QueryWorker o) { // REMOVE
        this.successors.add(o);
    }

    public synchronized void finishPred() {  // REMOVE
        this.preds -= 1;
    }

    /**
     * TODO: Implement `runParallelWithOrder` to process current query command and store the results in `result` where
     * the current query should wait until the prerequisite has been computed
     *
     * Hint1: you must invoke the methods in {@link QueryOnNode}, {@link QueryOnMethod} and {@link QueryOnClass}
     * to achieve the query
     * Hint2: you can invoke {@link QueryWorker#runParallel()} to reuse its logic
     * Hint3: please use {@link Thread} to achieve multi-threading
     */
    private void runParallelWithOrder() {
        // TODO: determine the order of query on class to reduce redundant execution
        while (true) {
            // System.out.println("Waiting..." + this.queryName + this.preds);
            if (preds <= 0) {
                break;
            }
        }

        runParallel();
        for (QueryWorker succ : this.successors) {
            System.out.println(this.queryID + " notify " + succ.queryID);
            succ.finishPred();
        }

    }

}
