package hk.ust.comp3021.parallel;

import hk.ust.comp3021.RapidASTManagerEngine;
import hk.ust.comp3021.query.QueryOnClass;
import hk.ust.comp3021.utils.TestKind;
import hk.ust.comp3021.utils.TestUtil;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Task2HiddenTest {

    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testParallelExecutionWithOrder1() {
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxml/", List.of("18", "19"), 4);

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        List<Integer> expectedCounts = List.of(2, 2, 0, 0,0);

        commands.add(new Object[] {"1", "18", "haveSuperClass", new Object[] {"B", "H"}});
        commands.add(new Object[] {"2", "18", "haveSuperClass", new Object[] {"B", "A"}});
        commands.add(new Object[] {"3", "18", "findSuperClasses", new Object[] {"B"}});

        expectedResults.add(false);
        expectedResults.add(true);
        expectedResults.add(Set.of("A"));

        QueryOnClass.clearCounts();
        engine.processCommands(commands, 2);
        List<Object> allResults = engine.getAllResults();
        List<Integer> allCounts = QueryOnClass.getCounts();
        TestUtil.checkResults(expectedResults, allResults, commands);
        assertEquals(expectedCounts, allCounts);
        
    }

    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testParallelExecutionWithOrderDupCommand() {
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxml/", List.of("18", "19"), 4);

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        List<Integer> expectedCounts = List.of(2, 2, 0, 0,0);

        commands.add(new Object[] {"1", "18", "haveSuperClass", new Object[] {"B", "H"}});
        commands.add(new Object[] {"2", "18", "haveSuperClass", new Object[] {"B", "A"}});
        commands.add(new Object[] {"3", "18", "findSuperClasses", new Object[] {"B"}});
        commands.add(new Object[] {"4", "18", "haveSuperClass", new Object[] {"B", "H"}});


        expectedResults.add(false);
        expectedResults.add(true);
        expectedResults.add(Set.of("A"));
        expectedResults.add(false);
        
        QueryOnClass.clearCounts();
        engine.processCommands(commands, 2);
        List<Object> allResults = engine.getAllResults();
        List<Integer> allCounts = QueryOnClass.getCounts();
        TestUtil.checkResults(expectedResults, allResults, commands);
        assertEquals(expectedCounts, allCounts);

    }

    @Tag(TestKind.PUBLIC)
    @RepeatedTest(50)
    public void testParallelExecutionWithOrder() {
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxml/", List.of("11", "16"), 4);

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        List<Integer> expectedCounts = List.of(90, 5, 1, 17, 1);
        commands.add(new Object[]{"1", "16", "findOverridingMethods", new Object[]{}}); // 1 -> 2, 1 -> 5
        commands.add(new Object[]{"2", "16", "findAllMethods", new Object[]{"H"}}); // 2 -> 6
        commands.add(new Object[]{"3", "16", "findAllMethods", new Object[]{"I"}});  // 3 -> 5
        commands.add(new Object[]{"4", "16", "haveSuperClass", new Object[]{"I", "H"}}); // 4 -> 5
        commands.add(new Object[]{"5", "16", "findSuperClasses", new Object[]{"I"}});
        commands.add(new Object[]{"6", "16", "findSuperClasses", new Object[]{"H"}});
        
        commands.add(new Object[]{"7", "11", "findClassesWithMain", new Object[]{}}); //  7 -> 8
        commands.add(new Object[]{"8", "11", "findAllMethods", new Object[]{"H"}});
        commands.add(new Object[]{"9", "11", "haveSuperClass", new Object[]{"D", "B"}});
        commands.add(new Object[]{"10", "11", "haveSuperClass", new Object[]{"M", "K"}});
        commands.add(new Object[]{"11", "11", "haveSuperClass", new Object[]{"L", "I"}});
        commands.add(new Object[]{"12", "11", "haveSuperClass", new Object[]{"H", "C"}});  // 12 -> 14 
        commands.add(new Object[]{"13", "11", "findSuperClasses", new Object[]{"D"}});  // 13 -> 9, 13 -> 7
        commands.add(new Object[]{"14", "11", "findSuperClasses", new Object[]{"H"}}); // 8 -> 14 
        commands.add(new Object[]{"15", "11", "findSuperClasses", new Object[]{"M"}}); // 11 -> 15
        commands.add(new Object[]{"16", "11", "findSuperClasses", new Object[]{"L"}});
 
        
        expectedResults.add(new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g")));
        expectedResults.add(new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g")));
        expectedResults.add(new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g")));
        expectedResults.add(true);
        expectedResults.add(Set.of("A", "B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("F", "A", "B", "C", "G", "D", "E"));
        expectedResults.add(Set.of());
        expectedResults.add(new ArrayList<>(Arrays.asList("a", "b", "c", "e", "f", "g", "h")));
        expectedResults.add(true);
        expectedResults.add(false);
        expectedResults.add(true);
        expectedResults.add(true);

        expectedResults.add(Set.of("C", "B", "A"));
        expectedResults.add(Set.of("E", "C", "B", "A", "G", "F"));
        expectedResults.add(Set.of("J", "I", "D", "C", "B", "A"));
        expectedResults.add(Set.of("K", "I", "J", "F", "B", "A")) ;
        QueryOnClass.clearCounts();
        engine.processCommands(commands, 2);
        List<Object> allResults = engine.getAllResults();
        System.out.println(allResults);
        List<Integer> allCounts = QueryOnClass.getCounts();
        TestUtil.checkResults(expectedResults, allResults, commands);
        assertEquals(expectedCounts, allCounts);
    }

}
