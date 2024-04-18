package hk.ust.comp3021.parallel;
import hk.ust.comp3021.query.QueryOnMethod;
import hk.ust.comp3021.query.QueryOnClass;
import hk.ust.comp3021.query.QueryOnNode;
import hk.ust.comp3021.utils.ASTModule;
import java.util.HashMap;

public class QueryWorker implements Runnable {
    private HashMap<String, ASTModule> id2ASTModules;
    private String queryID;
    private String astID;
    private String queryName;
    private Object[] args;
    private int mode;

    private Object result;

    public QueryWorker(HashMap<String, ASTModule> id2ASTModules, String queryID, String astID, String queryName, Object[] args, int mode) {
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
        if(mode == 0) {
            runSerial();
        } else if(mode == 1) {
            runParallel();
        } else if(mode == 2) {
            runParallelWithOrder();
        } 
    }

    private void runSerial() {
        // TODO
        switch(queryName) {
            case "findFuncWithArgGtN": {
                QueryOnNode query = new QueryOnNode(id2ASTModules);
                query.findFuncWithArgGtN.accept((Integer) args[0]);
                this.result = null;
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
    private void runParallel() {
        // TODO:
        runSerial();
    }

    private void runParallelWithOrder() {
        //TODO:

    }

}
