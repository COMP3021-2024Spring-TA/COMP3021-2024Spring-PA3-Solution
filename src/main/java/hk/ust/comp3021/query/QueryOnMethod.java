package hk.ust.comp3021.query;

import hk.ust.comp3021.expr.*;
import hk.ust.comp3021.misc.*;
import hk.ust.comp3021.stmt.*;
import hk.ust.comp3021.utils.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class QueryOnMethod {
    /**
     * IMPORTANT: for all test cases for QueryOnMethod, we would not involve class
     */
    ASTModule module = null;

    public QueryOnMethod(ASTModule module) {
        this.module = module;
    }

    public Function<String, Optional<ASTElement>> findFuncInModule = name ->
            module.filter(node -> node instanceof FunctionDefStmt)
                    .stream()
                    .filter(func -> name.equals(((FunctionDefStmt) func).getName()))
                    .findFirst();


    /**
     * TODO `findEqualCompareInFunc` find all comparison expression with operator \"==\" in current module {@link QueryOnMethod#module}
     *
     * @param funcName the name of the function to be queried
     * @return results List of strings where each represents a comparison expression, in format, lineNo:colOffset-endLineNo:endColOffset
     * Hints1: if func does not exist in current module, return empty list
     * Hints2: use {@link ASTElement#filter(Predicate)} method to implement the function
     */
    public Function<String, List<String>> findEqualCompareInFunc = funcName -> {
        List<String> results = new ArrayList<>();

        if (findFuncInModule.apply(funcName).isPresent()) {
            ASTElement func = findFuncInModule.apply(funcName).get();
            results.addAll(func.filter(node -> node instanceof CompareExpr)
                    .stream()
                    .map(expr -> (CompareExpr) expr)
                    .filter(expr -> expr.getOps().stream().anyMatch(op -> op.getOperatorName().equals("Eq")))
                    .map(expr -> expr.getLineNo() + ":" + expr.getColOffset() + "-" +
                            expr.getEndLineNo() + ":" + expr.getEndColOffset())
                    .toList());
        }
        return results;
    };

    /**
     * TODO `findFuncWithBoolParam` find all functions that use boolean parameter as if condition in current module {@link QueryOnMethod#module}
     *
     * @param null
     * @return List of strings where each represents the name of function that satisfy the requirements
     * Hints1: the boolean parameter is annotated with type bool
     * Hints2: as long as the boolean parameter shown in the {@link IfStmt#getTest()} expression, we say it's used
     * Hints3: use {@link ASTElement#filter(Predicate)} method to implement the function
     */
    public Supplier<List<String>> findFuncWithBoolParam = () -> {
        Predicate<ASTElement> hasBoolName = annotation -> annotation instanceof NameExpr
                && ((NameExpr) annotation).getId().equals("bool");

        Function<ASTElement, List<String>> findAstArg = func -> func
                .filter(node -> node instanceof ASTArguments.ASTArg)
                .stream()
                .map(arg -> (ASTArguments.ASTArg) arg)
                .filter(arg -> arg.getAnnotation() != null)
                .filter(arg -> hasBoolName.test(arg.getAnnotation()))
                .map(ASTArguments.ASTArg::getArg)
                .collect(Collectors.toList());

        BiPredicate<ASTElement, List<String>> hasUsedInIf = (func, arrayList) -> arrayList
                .stream()
                .anyMatch(argName -> func.filter(node -> node instanceof IfStmt)
                        .stream()
                        .map(ifStmt -> (IfStmt) ifStmt)
                        .map(IfStmt::getTest)
                        .anyMatch(test -> test.filter(condVar -> condVar instanceof NameExpr)
                                .stream()
                                .map(condVar -> (NameExpr) condVar)
                                .anyMatch(condVar -> condVar.getId().equals(argName)
                                        && condVar.getCtx().getOp() == ASTEnumOp.ASTOperator.Ctx_Load)
                        )
                );


        return module.filter(node -> node instanceof FunctionDefStmt)
                .stream()
                .filter(func -> hasUsedInIf.test(func, findAstArg.apply(func)))
                .map(func -> (FunctionDefStmt) func)
                .map(FunctionDefStmt::getName)
                .collect(Collectors.toList());
    };


    /**
     * TODO Given func name `funcName`, `findUnusedParamInFunc` find all unused parameter in current module {@link QueryOnMethod#module}
     *
     * @param funcName to be queried function name
     * @return results List of strings where each represents the name of an unused parameter
     * Hints1: if a variable is read, the ctx is `Load`, otherwise `Store` if written
     * Hints2: for the case where variable is written before read, we use line number and col offset to
     * check if the write operation is conducted before the first place where the parameter is read
     * Hints3: use {@link ASTElement#filter(Predicate)} method to implement the function
     * Hints4: if func does not exist in current module, return empty list
     */
    public Function<String, List<String>> findUnusedParamInFunc = funcName -> {
        List<String> results = new ArrayList<>();

        // find all functions whose name matches funcName
        if (findFuncInModule.apply(funcName).isPresent()) {
            ASTElement func = findFuncInModule.apply(funcName).get();
            Map<String, ASTElement> arg2FirstReadLoc = new HashMap<>();

            // parameter name to read locations
            Map<String, List<ASTElement>> readVariables = func
                    .filter(node -> node instanceof ASTArguments.ASTArg)
                    .stream()
                    .map(arg -> ((ASTArguments.ASTArg) arg).getArg())
                    .collect(Collectors.toMap(argName -> argName,
                            argName -> new ArrayList<>(func
                                    .filter(readVar -> readVar instanceof NameExpr
                                            && ((NameExpr) readVar).getId().equals(argName)
                                            && ((NameExpr) readVar).getCtx().getOp() == ASTEnumOp.ASTOperator.Ctx_Load)
                            ))
                    );

            // find the first place where param is read
            readVariables.forEach((argName, readLocs) -> {
                if (readLocs.isEmpty()) {
                    results.add(argName);
                } else {
                    readLocs.stream()
                            .min((loc1, loc2) -> {
                                int lineComparison = Integer.compare(loc1.getLineNo(), loc2.getLineNo());
                                if (lineComparison == 0) {
                                    return Integer.compare(loc1.getColOffset(), loc2.getColOffset());
                                }
                                return lineComparison;
                            }).ifPresent(firstRead -> arg2FirstReadLoc.put(argName, firstRead));
                }
            });

            arg2FirstReadLoc.entrySet()
                    .stream()
                    .filter(entry -> func
                            .filter(writeVar -> writeVar instanceof NameExpr)  // there is write before first read
                            .stream()
                            .map(writeVar -> (NameExpr) writeVar)
                            .anyMatch(writeVar -> writeVar.getCtx().getOp() == ASTEnumOp.ASTOperator.Ctx_Store
                                    && writeVar.getId().equals(((NameExpr) entry.getValue()).getId())
                                    && writeVar.getLineNo() < entry.getValue().getLineNo()
                                    && writeVar.getColOffset() < entry.getValue().getColOffset())
                    )
                    .forEach(entry -> results.add(entry.getKey()));
        }

        return results;
    };


    public Function<ASTElement, Optional<String>> getCallExprName = callexpr ->
            callexpr.filter(node -> node instanceof NameExpr)
                    .stream()
                    .map(node -> (NameExpr) node)
                    .map(NameExpr::getId).findFirst();

    public Function<ASTElement, List<ASTElement>> findAllCalledFuncs = func ->
            new ArrayList<>(func.filter(expr -> expr instanceof CallExpr));

    /**
     * TODO Given func name `funcName`, `findDirectCalledOtherB` find all functions being direct called by functions other than B in current module {@link QueryOnMethod#module}
     *
     * @param funcName the name of function B
     * @return results List of strings where each represents the name of a function that satisfy the requirement
     * Hints1: there is no class in the test cases for this code pattern, thus, no function names such as a.b()
     * Hints2: for a call expr foo(), we can directly use the called function name foo to location the implementation
     * Hints3: use {@link ASTElement#filter(Predicate)} method to implement the function
     * Hints4: if func does not exist in current module, return empty list
     */
    public Function<String, List<String>> findDirectCalledOtherB = funcName -> {
        List<String> results = new ArrayList<>();

        Map<FunctionDefStmt, List<ASTElement>> func2CalledFuncs = module
                .filter(func -> func instanceof FunctionDefStmt)
                .stream()
                .map(func -> (FunctionDefStmt) func)
                .collect(Collectors.toMap(func -> func,
                        func -> findAllCalledFuncs.apply(func)));

        Map<String, List<String>> callee2AllCallers = new HashMap<>();
        func2CalledFuncs.forEach((key, value) -> value.forEach(callee -> {
            if (getCallExprName.apply(callee).isPresent()) {
                String calleeName = getCallExprName.apply(callee).get();
                if (findFuncInModule.apply(calleeName).isPresent()) {
                    if (!callee2AllCallers.containsKey(calleeName)) {
                        callee2AllCallers.put(calleeName, new ArrayList<>());
                    }
                    callee2AllCallers.get(calleeName).add(key.getName());
                }
            }
        }));
        
        if (findFuncInModule.apply(funcName).isPresent()) {
            results.addAll(callee2AllCallers
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue()
                            .stream()
                            .anyMatch(callerName -> !callerName.equals(funcName)))
                    .map(Map.Entry::getKey).toList());
        } else {
            results.addAll(callee2AllCallers
                    .entrySet()
                    .stream()
                    .filter(entry -> !entry.getValue().isEmpty())
                    .map(Map.Entry::getKey).toList());
        }
        return results;
    };

    /**
     * TODO Given func name `funcNameA` and `funcNameB`, `answerIfACalledB` checks if A calls B directly or transitively in current module {@link QueryOnMethod#module}
     *
     * @param funcNameA the name of function A
     * @param funcNameB the name of function B
     * @return a boolean return value to answer yes or no
     * Hints1: there is no class in the test cases for this code pattern, thus, no function names such as a.b()
     * Hints2: for a call expr foo(), we can directly use the called function name foo to location the implementation
     * Hints3: use {@link ASTElement#filter(Predicate)} method to implement the function
     */

    public BiPredicate<String, String> answerIfACalledB = (funcNameA, funcNameB) -> {
        if (findFuncInModule.apply(funcNameA).isPresent() && findFuncInModule.apply(funcNameB).isPresent()) {
            List<String> tobeProcessed = new ArrayList<>();
            tobeProcessed.add(funcNameA);

            while (!tobeProcessed.isEmpty()) {
                String curFuncName = tobeProcessed.get(0);
                tobeProcessed.remove(0);

                if (curFuncName.equals(funcNameB)) {
                    return true;
                }
                if (findFuncInModule.apply(curFuncName).isEmpty()) {
                    continue;
                }

                ASTElement curFuncNode = findFuncInModule.apply(curFuncName).get();
                for (ASTElement called : findAllCalledFuncs.apply(curFuncNode)) {
                    if (getCallExprName.apply(called).isEmpty()) {
                        continue;
                    }
                    String calledFuncName = getCallExprName.apply(called).get();
                    tobeProcessed.add(calledFuncName);
                }
            }
        }
        return false;
    };


}