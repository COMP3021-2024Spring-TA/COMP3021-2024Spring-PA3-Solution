package hk.ust.comp3021.query;

import hk.ust.comp3021.expr.*;
import hk.ust.comp3021.misc.*;
import hk.ust.comp3021.stmt.*;
import hk.ust.comp3021.utils.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class QueryOnNode {

    private HashMap<String, ASTModule> id2ASTModules;

    public QueryOnNode(HashMap<String, ASTModule> id2ASTModules) {
        this.id2ASTModules = id2ASTModules;
    }

    /**
     * TODO `findFuncWithArgGtN` find all functions whose # arguments > given `paramN` in all modules
     * {@link QueryOnNode#id2ASTModules}
     *
     * @param paramN the number of arguments user expects
     * @return null as PA1, simply print out all functions that satisfy the requirements with format ModuleID_FuncName_LineNo
     * Hints1: use {@link ASTElement#filter(Predicate)} method to implement the function
     */
    public Function<Integer, List<String>> findFuncWithArgGtN = paramN -> {
        List<String> finalResult = new ArrayList<>();
        id2ASTModules.values().forEach(module -> {
            module.filter(node -> node instanceof FunctionDefStmt)
                    .stream()
                    .filter(func -> ((FunctionDefStmt) func).getParamNum() >= paramN)
                    .map(func -> module.getASTID() + "_" + ((FunctionDefStmt) func).getName() + "_" + func.getLineNo())
                    .forEach(finalResult::add);
        });
        return finalResult;
    };


    /**
     * TODO `calculateOp2Nums` count the frequency of each operator in all modules {@link QueryOnNode#id2ASTModules}
     *
     * @param null
     * @return op2Num as PA1,the key is operator name, and value is the frequency
     * Hints1: use {@link ASTElement#forEach(Consumer)} method to implement the function
     */
    public Supplier<HashMap<String, Integer>> calculateOp2Nums = () -> {
        HashMap<String, Integer> op2Num = new HashMap<>();

        Consumer<ASTElement> binOp = node -> {
            if (node instanceof BinOpExpr) {
                op2Num.merge(((BinOpExpr) node).getOp().getOperatorName(), 1, Integer::sum);
            }
        };

        Consumer<ASTElement> boolOp = node -> {
            if (node instanceof BoolOpExpr) {
                op2Num.merge(((BoolOpExpr) node).getOp().getOperatorName(), 1, Integer::sum);
            }
        };

        Consumer<ASTElement> unaryOp = node -> {
            if (node instanceof UnaryOpExpr) {
                op2Num.merge(((UnaryOpExpr) node).getOp().getOperatorName(), 1, Integer::sum);
            }
        };

        Consumer<ASTElement> compOp = node -> {
            if (node instanceof CompareExpr) {
                ((CompareExpr) node).getOps().forEach(op -> op2Num.merge(op.getOperatorName(), 1, Integer::sum));
                ;
            }
        };

        Consumer<ASTElement> augAssignOp = node -> {
            if (node instanceof AugAssignStmt) {
                op2Num.merge(((AugAssignStmt) node).getOp().getOperatorName(), 1, Integer::sum);
            }
        };

        id2ASTModules.values().forEach(module -> {
            module.forEach(binOp.andThen(boolOp).andThen(unaryOp).andThen(compOp).andThen(augAssignOp));
        });
        return op2Num;
    };

    /**
     * TODO `calculateNode2Nums` count the frequency of each node in all modules {@link QueryOnNode#id2ASTModules}
     *
     * @param astID, a number to represent a specific AST or -1 for all
     * @return node2Nums as PA1,the key is node type, and value is the frequency
     * Hints1: use {@link ASTElement#groupingBy(Function, Collector)} method to implement the function
     * Hints2: if astID is invalid, return empty map
     */
    public Function<String, Map<String, Long>> calculateNode2Nums = astID -> {
        Map<String, Long> node2Nums = new HashMap<>();
        if (!astID.equals("-1") && id2ASTModules.containsKey(astID)) {
            id2ASTModules.get(astID)
                    .groupingBy(ASTElement::getNodeType, Collectors.counting())
                    .forEach((key, value) -> node2Nums.merge(key, value, Long::sum));
        } else if (astID.equals("-1")) {
            id2ASTModules.keySet().forEach(key -> id2ASTModules.get(key)
                    .groupingBy(ASTElement::getNodeType, Collectors.counting())
                    .forEach((nodeKey, nodeValue) -> node2Nums.merge(nodeKey, nodeValue, Long::sum)));
        }
        return node2Nums;
    };

    /**
     * TODO `processNodeFreq` sort all functions in all modules {@link QueryOnNode#id2ASTModules} based
     * on the number of nodes in FunctionDefStmt subtree
     *
     * @param null
     * @return a list of entries sorted in descending order where the key is function name
     * with format ModuleID_FuncName_LineNo, and value is the # nodes
     * Hints1: use {@link ASTElement#forEach(Consumer)} method to implement the function
     * Hint2: note that `countChildren` method is removed, please do not use this method
     */
    public Supplier<List<Map.Entry<String, Integer>>> processNodeFreq = () -> {
        HashMap<String, Integer> funcName2NodeNum = new HashMap<>();

        id2ASTModules.values().forEach(module -> {
            module.filter(node -> node instanceof FunctionDefStmt)
                    .stream()
                    .map(func -> {
                        final int[] nodeCount = {0};
                        func.forEach(node -> nodeCount[0]++);
                        String uniqueFuncName = module.getASTID() + "_" +
                                ((FunctionDefStmt) func).getName() + "_" + func.getLineNo();
                        return new AbstractMap.SimpleEntry<>(uniqueFuncName, nodeCount[0]);
                    })
                    .forEach(entry -> funcName2NodeNum.merge(
                            entry.getKey(),
                            entry.getValue(),
                            (value1, value2) -> value1));
        });

        return funcName2NodeNum
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .toList();
    };
    
}