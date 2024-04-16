package hk.ust.comp3021.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

import hk.ust.comp3021.ASTManagerEngine;
import hk.ust.comp3021.utils.TestKind;

public class QueryOnClassTest {
    @Tag(TestKind.PUBLIC)
    @Test
    public void testFindSuperClasses() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", String.valueOf(7));
        QueryOnClass queryOnClass = new QueryOnClass(engine.getId2ASTModules().get("7"));
        List<String> superClasses = queryOnClass.findSuperClasses.apply("Bar");

        // should have the two super classes
        Set<String> expectedOutput = Set.of("Foo", "Baz");
        assertEquals(expectedOutput, new HashSet<>(superClasses));
    }

    @Tag(TestKind.PUBLIC)
    @Test
    public void testHaveSuperClass() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", String.valueOf(7));
        QueryOnClass queryOnClass = new QueryOnClass(engine.getId2ASTModules().get("7"));
        Boolean hasSuper = queryOnClass.haveSuperClass.apply("Bar", "Baz");
        
        assertEquals(true, hasSuper);
    }

    @Tag(TestKind.PUBLIC)
    @Test
    public void testFindOverrideMethods() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", String.valueOf(8));
        QueryOnClass queryOnClass = new QueryOnClass(engine.getId2ASTModules().get("8"));
        List<String> superClasses = queryOnClass.findOverridingMethods.get();

        // there are two overriding of `foo`
        List<String> expectedOutput = new ArrayList<>(Arrays.asList("baz", "foo", "foo"));
        assertEquals(expectedOutput.size(), superClasses.size());
        for (String superClass : superClasses) {
            int index = expectedOutput.indexOf(superClass);
            if(index != -1) {
                expectedOutput.remove(index);
            }
        }
        assertEquals(expectedOutput.size(), 0);
    }

    @Tag(TestKind.PUBLIC)
    @Test
    public void testFindAllMethods() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", String.valueOf(8));
        QueryOnClass queryOnClass = new QueryOnClass(engine.getId2ASTModules().get("8"));
        List<String> allMethods = queryOnClass.findAllMethods.apply("Bar");

        Set<String> expectedOutput = Set.of("foo", "bar", "baz");
        assertEquals(expectedOutput, new HashSet<String>(allMethods));
    }

    @Tag(TestKind.PUBLIC)
    @Test
    public void testFindClassesWithMain() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", String.valueOf(9));
        QueryOnClass queryOnClass = new QueryOnClass(engine.getId2ASTModules().get("9"));
        List<String> classesWithMain = queryOnClass.findClassesWithMain.get();

        Set<String> expectedOutput = Set.of("Baz", "Bar", "Foo");
        assertEquals(expectedOutput, new HashSet<String>(classesWithMain));
    }

    @Tag(TestKind.HIDDEN)
    @ParameterizedTest
    @MethodSource("dataFindSuperClasses")
    public void testFindSuperClassesHidden(String caseID, String inputFunc, Set<String> expectedOutput) {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", caseID);
        QueryOnClass queryOnClass = new QueryOnClass(engine.getId2ASTModules().get(caseID));
        List<String> superClasses = queryOnClass.findSuperClasses.apply(inputFunc);

        assertEquals(expectedOutput, new HashSet<>(superClasses));
    }

    private static Stream<Object[]> dataFindSuperClasses() {
        return Stream.of(
            new Object[] {"7", "Bar", Set.of("Foo", "Baz")},
            new Object[] {"7", "Foo", Set.of()},
            new Object[] {"11", "D", Set.of("A", "B", "C")},
            new Object[] {"11", "E", Set.of("A", "B", "C")},
            new Object[] {"11", "H", Set.of("A", "B", "C", "E", "G", "F")},
            new Object[] {"11", "K", Set.of("I", "J")},
            new Object[] {"11", "L", Set.of("A", "B", "F", "I", "J", "K")},
            new Object[] {"11", "M", Set.of("A", "B", "C", "D", "I", "J")},
            new Object[] {"11", "N", Set.of("A", "B", "C", "E", "F", "G", "H", "I", "J", "K")},
            new Object[] {"11", "O", Set.of("A", "B", "C", "F", "G", "I")}

        );
    }

    @Tag(TestKind.HIDDEN)
    @ParameterizedTest
    @MethodSource("dataHaveSuperClass")
    public void testHaveSuperClassHidden(String caseID, String FuncA, String FuncB, Boolean expectedOutput) {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", caseID);
        QueryOnClass queryOnClass = new QueryOnClass(engine.getId2ASTModules().get(caseID));
        Boolean hasSuper = queryOnClass.haveSuperClass.apply(FuncA, FuncB);
        assertEquals(expectedOutput, hasSuper);
    }

    private static Stream<Object[]> dataHaveSuperClass() {
        return Stream.of(
            new Object[] {"7","Bar", "Baz", true},
            new Object[] {"7", "Foo", "Bar", false},
            new Object[] {"11", "D", "B", true},
            new Object[] {"11", "E", "B", true},
            new Object[] {"11", "C", "C", false},
            new Object[] {"11", "L", "I", true},
            new Object[] {"11", "L", "A", true},
            new Object[] {"11", "M", "K", false},
            new Object[] {"11", "M", "B", true},
            new Object[] {"11", "O", "F", true}
        );
    }

    @Tag(TestKind.HIDDEN)
    @ParameterizedTest
    @MethodSource("dataFindOverrideMethods")
    public void testFindOverrideMethodsHidden(String caseID, List<String> expectedOutput) {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", caseID);
        QueryOnClass queryOnClass = new QueryOnClass(engine.getId2ASTModules().get(caseID));
        List<String> superClasses = queryOnClass.findOverridingMethods.get();
        System.out.println(superClasses);
        assertEquals(expectedOutput.size(), superClasses.size());
        
        for (String superClass : superClasses) {
            int index = expectedOutput.indexOf(superClass);
            if(index != -1) {
                expectedOutput.remove(index);
            }
        }
        assertEquals(expectedOutput.size(), 0);
    }

    private static Stream<Object[]> dataFindOverrideMethods() {
        return Stream.of(
            new Object[] {"8", new ArrayList<>(Arrays.asList("baz", "foo", "foo"))},
            new Object[] {"9", new ArrayList<>(Arrays.asList())},
            new Object[] {"12", new ArrayList<>(Arrays.asList("a", "a", "a", "b", "b", "c", "c", "e", "f"))},
            new Object[] {"13", new ArrayList<>(Arrays.asList("a", "a", "a", "b", "c", "c", "c"))},
            new Object[] {"14", new ArrayList<>(Arrays.asList("a", "b"))},
            new Object[] {"15", new ArrayList<>(Arrays.asList("c"))},
            new Object[] {"16", new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g"))},
            new Object[] {"17", new ArrayList<>(Arrays.asList("a", "a", "a", "a", "a", "a", "a"))},
            new Object[] {"18", new ArrayList<>(Arrays.asList("a", "a", "a", "a", "a", "a", "a", "main", "main"))},
            new Object[] {"19", new ArrayList<>(Arrays.asList("a", "a", "a", "a", "a", "a", "a", "main"))}
        );
    }

    @Tag(TestKind.HIDDEN)
    @ParameterizedTest
    @MethodSource("dataFindAllMethods")
    public void testFindAllMethodsHidden(String caseID, String inputFunc, Set<String> expectedOutput) {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", caseID);
        QueryOnClass queryOnClass = new QueryOnClass(engine.getId2ASTModules().get(caseID));
        List<String> allMethods = queryOnClass.findAllMethods.apply(inputFunc);

        assertEquals(expectedOutput, new HashSet<String>(allMethods));
    }

    private static Stream<Object[]> dataFindAllMethods() {
        return Stream.of(
            new Object[] {"7", "Bar", Set.of("foo", "bar", "baz")},
            new Object[] {"8", "Bar", Set.of("foo", "bar", "baz")},
            new Object[] {"11", "H", Set.of("h", "e", "g", "f", "c", "b", "a")},
            new Object[] {"11", "L", Set.of("a", "b", "f", "i", "j", "k", "l")},
            new Object[] {"11", "O", Set.of("a", "b", "c", "f", "g", "i", "o")},
            new Object[] {"12", "H", Set.of("a", "b", "c", "e", "f", "g", "h")},
            new Object[] {"16", "H", Set.of("a", "b", "c", "d", "e", "f", "g")}, 
            new Object[] {"16", "I", Set.of("a", "b", "c", "d", "e", "f", "g")},
            new Object[] {"17", "H", Set.of("a")},
            new Object[] {"19", "G", Set.of("a", "main")}
        );
    }

    @Tag(TestKind.HIDDEN)
    @ParameterizedTest
    @MethodSource("dataFindClassesWithMain")
    public void testFindClassesWithMainHidden(String caseID,  Set<String> expectedOutput) {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", caseID);
        QueryOnClass queryOnClass = new QueryOnClass(engine.getId2ASTModules().get(caseID));
        List<String> classesWithMain = queryOnClass.findClassesWithMain.get();

        assertEquals(expectedOutput, new HashSet<String>(classesWithMain));
    }

    private static Stream<Object[]> dataFindClassesWithMain() {
        return Stream.of(
            new Object[] {"9", Set.of("Baz", "Bar", "Foo")},
            new Object[] {"9", Set.of("Baz", "Bar", "Foo")},
            new Object[] {"13", Set.of()},
            new Object[] {"14", Set.of()},
            new Object[] {"15", Set.of()},
            new Object[] {"16", Set.of()},
            new Object[] {"17", Set.of()},
            new Object[] {"18", Set.of("B", "C", "D", "E", "F", "G", "H")},
            new Object[] {"19", Set.of("C", "D", "F", "G", "H")},
            new Object[] {"20", Set.of("B", "D")},
            new Object[] {"21", Set.of("C", "D", "E")}
        );
    }

}
