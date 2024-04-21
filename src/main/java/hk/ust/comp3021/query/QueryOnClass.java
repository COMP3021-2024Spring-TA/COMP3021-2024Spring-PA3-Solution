package hk.ust.comp3021.query;

import hk.ust.comp3021.expr.*;
import hk.ust.comp3021.misc.*;
import hk.ust.comp3021.stmt.*;
import hk.ust.comp3021.utils.*;

import java.util.*;
import java.util.function.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class QueryOnClass {
    ASTModule module = null;

    public QueryOnClass(ASTModule module) {
        this.module = module;
    }
    private static HashMap<String, Object> memo = new HashMap<>();
    private static Lock countLock = new ReentrantLock();
    private static Lock orderLock = new ReentrantLock();
    private static List<String> orderLists = new ArrayList<>();

    public static void clearCounts() {
        findSuperClassesCount = haveSuperClassCount = findOverridingMethodsCount = findAllMethodsCount = findClassesWithMainCount = 0;
        memo.clear();
    }

    public static List<Integer> getCounts() {
        return List.of(findSuperClassesCount, haveSuperClassCount, findOverridingMethodsCount, findAllMethodsCount, findClassesWithMainCount);
    }

    public static List<String> getOrderLists() {
        return orderLists;
    }

    // Helper function
    private BiFunction<String, ASTModule, Optional<ASTElement>> findClassInModule = (name, curModule) ->
            curModule.filter(node -> node instanceof ClassDefStmt)
                    .stream()
                    .filter(clazz -> name.equals(((ClassDefStmt) clazz).getName()))
                    .findFirst();

    /**
     * TODO Given class name `className`, `findSuperClasses` finds all the super classes of
     * it in the current module {@link QueryOnClass#module}
     *
     * @param className the name of class
     * @return results List of strings where each represents the name of a class that satisfy the requirement
     * Hint1: you can implement a helper function which receives the class name and
     * returns the ClassDefStmt object.
     * Hint2: You can first find the direct super classes, and then RECURSIVELY finds the
     * super classes of the direct super classes.
     */

    private static Integer findSuperClassesCount = 0;
    private Function<String, List<String>> findSuperClassesImpl = (className) -> {
        String key = module.getASTID() +  "@" + "findSuperClasses" + "@" + className;
        if(memo.containsKey(key)) {
            return (List<String>) memo.get(key);
        }

        countLock.lock();
        findSuperClassesCount += 1;
        countLock.unlock();

        List<String> results = new ArrayList<>();
        if (findClassInModule.apply(className, module).isPresent()) {
            ClassDefStmt clazz = (ClassDefStmt) findClassInModule.apply(className, module).get();
            clazz.getBases().forEach(base -> base.forEach(node -> {
                        if (node instanceof NameExpr) {
                            String superClass = ((NameExpr) node).getId();
                            results.add(superClass);
                            List<String> recSuperClasses = this.findSuperClassesImpl.apply(superClass);
                            results.addAll(recSuperClasses);
                        }
                    })
            );
        }
        return results;
    };
    public Function<String, List<String>> findSuperClasses = (className) -> {
        String key = module.getASTID() +  "@" + "findSuperClasses" + "@" + className;
        System.out.println("[LOG FROM QueryOnClass] Querying findSuperClasses on AST " + this.module.getASTID());
        
        orderLock.lock();
        orderLists.add(key);
        orderLock.unlock();
        
        List<String> result = findSuperClassesImpl.apply(className);
        memo.put(key, result);
        return result;
    };


    /**
     * TODO Given class name `classA` and `classB` representing two classes A and B,
     *  `haveSuperClass` checks whether B is a super class of A in the current module.
     *  {@link QueryOnClass#module}
     *
     * @param classA the name of class A.
     * @param classB the name of class B
     * @return returns true if B is A's super class, otherwise false.
     * Hint1: you can just reuse {@link QueryOnClass#findSuperClasses}
     */

    private static Integer haveSuperClassCount = 0;
    public BiFunction<String, String, Boolean> haveSuperClassImpl = (classA, classB) -> {
        String key = module.getASTID() +  "@" + "haveSuperClass" + "@" + classA + "@" +classB;
        if(memo.containsKey(key)) {
            return (Boolean) memo.get(key);
        }
        countLock.lock();
        haveSuperClassCount += 1;
        countLock.unlock();
        return findSuperClassesImpl.apply(classA).contains(classB);
    };

    public BiFunction<String, String, Boolean> haveSuperClass = (classA, classB) -> {
        String key = module.getASTID() +  "@" + "haveSuperClass" + "@" + classA + "@" +classB;
        System.out.println("[LOG FROM QueryOnClass] Querying haveSuperClass on AST " + this.module.getASTID());

        orderLock.lock();
        orderLists.add(key);
        orderLock.unlock();
        
        Boolean result = haveSuperClassImpl.apply(classA, classB);
        memo.put(key, result);
        return result;

    };


    // Helper function
    private final Function<String, List<String>> findDirectMethods = (classA) -> {
        List<String> results = new ArrayList<>();
        if (findClassInModule.apply(classA, module).isPresent()) {
            ClassDefStmt clazz = (ClassDefStmt) findClassInModule.apply(classA, module).get();
            clazz.forEach(node -> {
                if (node instanceof FunctionDefStmt) {
                    results.add(((FunctionDefStmt) node).getName());
                }
            });
        }
        return results;
    };

    /**
     * TODO Returns all the overriding methods within the current module
     * {@link QueryOnClass#module}
     *
     * @return results List of strings of the names of overriding methods.
     * Note: If there are multiple overriding functions with the same name, please include name
     * in the result list for MULTIPLE times. You can refer to the test case.
     * Hint1: you can implement a helper function that first finds the methods that a class
     * directly contains.
     * Hint2: you can reuse the results of {@link QueryOnClass#findSuperClasses}
     */
    private static Integer findOverridingMethodsCount = 0;
    public Supplier<List<String>> findOverridingMethodsImpl = () -> {
        String key = module.getASTID() +  "@" + "findOverridingMethods";
        if(memo.containsKey(key)) {
            return (List<String>) memo.get(key);
        }
        countLock.lock();
        findOverridingMethodsCount += 1;
        countLock.unlock();
        List<String> results = new ArrayList<>();
        module.filter(node -> node instanceof ClassDefStmt).forEach(clazz -> {
            String className = ((ClassDefStmt) clazz).getName();
            List<String> directMethods = findDirectMethods.apply(className);
            List<String> superMethods = new ArrayList<>();
            findSuperClassesImpl.apply(className).forEach(superClassName -> {
                superMethods.addAll(findDirectMethods.apply(superClassName));
            });
            directMethods.forEach(methodName -> {
                if (superMethods.contains(methodName)) {
                    results.add(methodName);
                }
            });
        });
        return results;
    };

    public Supplier<List<String>> findOverridingMethods = () -> {
        System.out.println("[LOG FROM QueryOnClass] Querying findOverridingMethods on AST " + this.module.getASTID());

        String key = module.getASTID() +  "@" + "findOverridingMethods";
        orderLock.lock();
        orderLists.add(key);
        orderLock.unlock();
        
        List<String> result = findOverridingMethodsImpl.get();
        memo.put(key, result);
        return result;
    };

    /**
     * TODO Returns all the methods that a class possesses in the current module
     * {@link QueryOnClass#module}
     *
     * @param className the name of the class
     * @return results List of strings of names of the methods it possesses
     * Note: the same function name should appear in the list only once, due to overriding.
     * Hint1: you can implement a helper function that first finds the methods that a class
     * directly contains.
     * Hint2: you can reuse the results of {@link QueryOnClass#findSuperClasses}
     */
    private static Integer findAllMethodsCount = 0;
    public Function<String, List<String>> findAllMethodsImpl = (className) -> {
        
        String key = module.getASTID() +  "@" + "findAllMethods" + "@" + className;
        if(memo.containsKey(key)) {
            return (List<String>) memo.get(key);
        }
        countLock.lock();
        findAllMethodsCount += 1;
        countLock.unlock();
        HashSet<String> results = new HashSet<>(findDirectMethods.apply(className));
        findSuperClassesImpl.apply(className).forEach(superClass -> {
            results.addAll(findDirectMethods.apply(superClass));
        });
        return new ArrayList<>(results);
    };

    public Function<String, List<String>> findAllMethods = (className) -> {
        String key = module.getASTID() +  "@" + "findAllMethods" + "@" + className;
        System.out.println("[LOG FROM QueryOnClass] Querying findAllMethods on AST " + this.module.getASTID());
        orderLock.lock();
        orderLists.add(key);
        orderLock.unlock();
        
        List<String> result = findAllMethodsImpl.apply(className);
        memo.put(key, result);
        return result;
    };

    /**
     * TODO Returns all the classes that possesses a main function in the current module
     * {@link QueryOnClass#module}
     *
     * @return results List of strings of names of the classes
     * Hint1: You can reuse the results of {@link QueryOnClass#findAllMethods}
     */
    private static Integer findClassesWithMainCount = 0;
    public Supplier<List<String>> findClassesWithMainImpl = () -> {
        String key = module.getASTID() +  "@" + "findClassesWithMain";
        if(memo.containsKey(key)) {
            return (List<String>) memo.get(key);
        }
        countLock.lock();
        findClassesWithMainCount += 1;
        countLock.unlock();
        List<String> results = new ArrayList<>();
        module.filter(node -> node instanceof ClassDefStmt)
                .forEach(clazz -> {
                    String className = ((ClassDefStmt) clazz).getName();
                    List<String> allMethods = findAllMethodsImpl.apply(className);
                    if (allMethods.contains("main")) {
                        results.add(className);
                    }
                });
        return results;
    };

    public Supplier<List<String>> findClassesWithMain = () -> {
        String key = module.getASTID() +  "@" + "findClassesWithMain";
        System.out.println("[LOG FROM QueryOnClass] Querying findClassesWithMain on AST " + this.module.getASTID());
        orderLock.lock();
        orderLists.add(key);
        orderLock.unlock();

        List<String> result = findClassesWithMainImpl.get();
        memo.put(key, result);
        return result;
    };
}

