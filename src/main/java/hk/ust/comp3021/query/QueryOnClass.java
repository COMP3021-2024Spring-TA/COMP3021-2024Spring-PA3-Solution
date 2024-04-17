package hk.ust.comp3021.query;

import hk.ust.comp3021.expr.*;
import hk.ust.comp3021.misc.*;
import hk.ust.comp3021.stmt.*;
import hk.ust.comp3021.utils.*;

import java.util.*;
import java.util.function.*;

public class QueryOnClass{
    ASTModule module = null;



    public QueryOnClass(ASTModule module) {
        this.module = module;
    }

    // Helper function
    private BiFunction<String, ASTModule, Optional<ASTElement>> findClassInModule = (name, curModule) ->
        curModule.filter(node-> node instanceof ClassDefStmt)
            .stream()
            .filter(clazz -> name.equals(((ClassDefStmt) clazz).getName()))
            .findFirst();
    
    /**
     * TODO Given class name `className`, `findSuperClasses` finds all the super classes of
     * it in the current module {@link QueryOnClass#module}
     * @param className the name of class 
     * @return results List of strings where each represents the name of a class that satisfy the requirement
     * Hint1: you can implement a helper function which receives the class name and 
     * returns the ClassDefStmt object.
     * Hint2: You can first find the direct super classes, and then RECURSIVELY finds the
     * super classes of the direct super classes.
     */
    public Function<String, List<String>> findSuperClasses = (className) -> {
        List<String> results = new ArrayList<>();
        if(findClassInModule.apply(className, module).isPresent()) {
            ClassDefStmt clazz = (ClassDefStmt) findClassInModule.apply(className, module).get();
            clazz.getChildren().forEach(node -> {
                if(node instanceof NameExpr) {
                    String superClass = ((NameExpr) node).getId();
                    results.add(superClass);
                    List<String> recSuperClasses = this.findSuperClasses.apply(superClass);
                    results.addAll(recSuperClasses);
                }
            }); 
        }
        return results;
    };

     /**
     * TODO Given class name `classA` and `classB` representing two classes A and B,
     *  `haveSuperClass` checks whether B is a super class of A in the current module.
     *  {@link QueryOnClass#module}
     * @param classA the name of class A.
     * @param classB the name of class B
     * @return returns true if B is A's super class, otherwise false.
     * Hint1: you can just reuse {@link QueryOnClass#findSuperClasses}
     */
    public BiFunction<String, String, Boolean> haveSuperClass = (classA, classB) -> {
        return findSuperClasses.apply(classA).contains(classB);
    };


    // Helper function
    private Function<String, List<String>> findDirectMethods = (classA) -> {
        List<String> results = new ArrayList<String>();
        ClassDefStmt clazz = (ClassDefStmt) findClassInModule.apply(classA, module).get();
        clazz.getChildren().forEach(node -> {
            if(node instanceof FunctionDefStmt) {
                results.add(((FunctionDefStmt) node).getName());
            }
        });
        return results;
    };

    /**
     * TODO Returns all the overriding methods within the current module 
     * {@link QueryOnClass#module}
     * @return results List of strings of the names of overriding methods. 
     * Note: If there are multiple overriding functions with the same name, please include name
     * in the result list for MULTIPLE times. You can refer to the test case.
     * Hint1: you can implement a helper function that first finds the methods that a class
     *  directly contains.
     * Hint2: you can reuse the results of {@link QueryOnClass#findSuperClasses}
     */
    public Supplier<List<String>> findOverridingMethods = () -> {
        List<String> results = new ArrayList<String>();
        module.filter(node-> node instanceof ClassDefStmt).forEach(clazz -> { 
            String className = ((ClassDefStmt) clazz).getName();
            List<String> directMethods = findDirectMethods.apply(className);
            List<String> superMethods = new ArrayList<String>();
            findSuperClasses.apply(className).forEach(superClassName -> {
                superMethods.addAll(findDirectMethods.apply(superClassName));
            });
            directMethods.forEach(methodName -> {
                if(superMethods.contains(methodName)) {
                    results.add(methodName);
                }
            });
        });
        return results;
    };

    /**
     * TODO Returns all the methods that a class possesses in the current module
     * {@link QueryOnClass#module}
     * @param className the name of the class
     * @return results List of strings of names of the methods it possesses
     * Note: the same function name should appear in the list only once, due to overriding.
     * Hint1: you can implement a helper function that first finds the methods that a class
     *  directly contains.
     * Hint2: you can reuse the results of {@link QueryOnClass#findSuperClasses}
     */
    public Function<String, List<String>> findAllMethods = (className) -> {
        HashSet<String> results = new HashSet<String>();
        results.addAll(findDirectMethods.apply(className));
        findSuperClasses.apply(className).forEach(superClass -> {
            results.addAll(findDirectMethods.apply(superClass));
        });
        return new ArrayList<String>(results);
    };

     /**
     * TODO Returns all the classes that possesses a main function in the current module
     * {@link QueryOnClass#module}
     * @return results List of strings of names of the classes
     * Hint1: You can reuse the results of {@link QueryOnClass#findAllMethods}
     */
    public Supplier<List<String>> findClassesWithMain = () -> {
        List<String> results = new ArrayList<String>();
        module.filter(node-> node instanceof ClassDefStmt).forEach(clazz -> {
            String className = ((ClassDefStmt) clazz).getName();
            List<String> allMethods = findAllMethods.apply(className);
            if(allMethods.contains("main")) {
                results.add(className);
            }
        });
        return results;
    };


}

