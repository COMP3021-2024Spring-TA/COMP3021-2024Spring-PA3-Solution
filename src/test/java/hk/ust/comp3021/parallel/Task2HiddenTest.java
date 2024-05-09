package hk.ust.comp3021.parallel;

import hk.ust.comp3021.RapidASTManagerEngine;
import hk.ust.comp3021.query.QueryOnClass;
import hk.ust.comp3021.query.QueryOnNode;
import hk.ust.comp3021.utils.TestKind;
import hk.ust.comp3021.utils.TestUtil;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class Task2HiddenTest {

    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testParallelExecutionWithOrder1() {
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxml/", List.of("18", "19"), 4);

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        List<Integer> expectedCounts = List.of(2, 2, 0, 0, 0);

        commands.add(new Object[]{"1", "18", "haveSuperClass", new Object[]{"B", "H"}});
        commands.add(new Object[]{"2", "18", "haveSuperClass", new Object[]{"B", "A"}});
        commands.add(new Object[]{"3", "18", "findSuperClasses", new Object[]{"B"}});

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
        List<Integer> expectedCounts = List.of(2, 2, 0, 0, 0);

        commands.add(new Object[]{"1", "18", "haveSuperClass", new Object[]{"B", "H"}});
        commands.add(new Object[]{"2", "18", "haveSuperClass", new Object[]{"B", "A"}});
        commands.add(new Object[]{"3", "18", "findSuperClasses", new Object[]{"B"}});
        commands.add(new Object[]{"4", "18", "haveSuperClass", new Object[]{"B", "H"}});


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
    public void testParallelExecutionMode0() {
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxml/", List.of("11", "16"), 4);

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        List<Integer> expectedCounts = List.of(190, 5, 1, 18, 1);
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
        expectedResults.add(Set.of("K", "I", "J", "F", "B", "A"));
        QueryOnClass.clearCounts();
        engine.processCommands(commands, 0);
        List<Object> allResults = engine.getAllResults();
        System.out.println(allResults);
        List<Integer> allCounts = QueryOnClass.getCounts();
        TestUtil.checkResults(expectedResults, allResults, commands);
        assertEquals(expectedCounts, allCounts);
    }


    @Tag(TestKind.PUBLIC)
    @Test
    public void testParallelExecutionMode0CmdNode() {
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxmlPA1/",
                IntStream.rangeClosed(0, 33)
                        .boxed()
                        .map(Objects::toString)
                        .collect(Collectors.toList()),
                4);

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        commands.add(new Object[]{"1", "0", "findFuncWithArgGtN", new Object[]{4}});
        commands.add(new Object[]{"2", "0", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"3", "0", "processNodeFreq", new Object[]{}});
        commands.add(new Object[]{"4", "0", "calculateNode2Nums", new Object[]{"2"}});

        QueryOnNode query = new QueryOnNode(engine.getId2ASTModule());
        expectedResults.add(query.findFuncWithArgGtN.apply(4));
        expectedResults.add(query.calculateOp2Nums.get());
        expectedResults.add(query.processNodeFreq.get());
        expectedResults.add(query.calculateNode2Nums.apply("2"));
        
        QueryOnClass.clearCounts();
        engine.processCommands(commands, 0);
        List<Object> allResults = engine.getAllResults();
        System.out.println(allResults);
        TestUtil.checkResults(expectedResults, allResults, commands);
    }

    @Tag(TestKind.PUBLIC)
    @Test
    public void testParallelExecutionMode0CmdMethod() {
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxml/",
                IntStream.rangeClosed(0, 33)
                        .boxed()
                        .map(Objects::toString)
                        .collect(Collectors.toList()),
                4);

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        commands.add(new Object[]{"1", "1", "findEqualCompareInFunc", new Object[]{"foo"}});
        commands.add(new Object[]{"2", "2", "findFuncWithBoolParam", new Object[]{}});
        commands.add(new Object[]{"3", "3", "findUnusedParamInFunc", new Object[]{"foo"}});
        commands.add(new Object[]{"4", "4", "findDirectCalledOtherB", new Object[]{"B"}});
        commands.add(new Object[]{"5", "5", "answerIfACalledB", new Object[]{"foo", "bar"}});

        expectedResults.add(Set.of(
                "2:7-2:21",
                "3:12-3:26",
                "5:12-5:26"));
        expectedResults.add(Set.of("toggle_light"));
        expectedResults.add(Set.of("param2", "param3"));
        expectedResults.add(Set.of("A"));
        expectedResults.add(true);
        
        QueryOnClass.clearCounts();
        engine.processCommands(commands, 0);
        List<Object> allResults = engine.getAllResults();
        System.out.println(allResults);
        TestUtil.checkResults(expectedResults, allResults, commands);
    }

    @Tag(TestKind.PUBLIC)
    @RepeatedTest(50)
    public void testParallelExecutionWithMode1() {
        List<Integer> expectedCountsMin = List.of(90, 5, 1, 17, 1);
        List<Integer> expectedCountsMax = List.of(190, 5, 1, 18, 1);
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxml/", List.of("11", "16"), 4);

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();

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
        expectedResults.add(Set.of("K", "I", "J", "F", "B", "A"));
        QueryOnClass.clearCounts();
        engine.processCommands(commands, 1);
        List<Object> allResults = engine.getAllResults();
        System.out.println(allResults);
        List<Integer> allCounts = QueryOnClass.getCounts();
        TestUtil.checkResults(expectedResults, allResults, commands);
        for (int i = 0; i < allCounts.size(); i++) {
            assertTrue(allCounts.get(i) <= expectedCountsMax.get(i));
            assertTrue(allCounts.get(i) >= expectedCountsMin.get(i));
        }
    }


    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testParallelExecutionMode2() {
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
        expectedResults.add(Set.of("K", "I", "J", "F", "B", "A"));
        QueryOnClass.clearCounts();
        engine.processCommands(commands, 2);
        List<Object> allResults = engine.getAllResults();
        System.out.println(allResults);
        List<Integer> allCounts = QueryOnClass.getCounts();
        TestUtil.checkResults(expectedResults, allResults, commands);
        assertEquals(expectedCounts, allCounts);
    }

    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testParallelExecutionModeTime() {
        // may not succeed
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxml/",
                IntStream.rangeClosed(0, 33)
                        .boxed()
                        .map(Objects::toString)
                        .collect(Collectors.toList()),
                4);

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
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

        commands.add(new Object[]{"17", "0", "findFuncWithArgGtN", new Object[]{4}});
        commands.add(new Object[]{"18", "0", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"19", "0", "processNodeFreq", new Object[]{}});
        commands.add(new Object[]{"20", "0", "calculateNode2Nums", new Object[]{"2"}});

        commands.add(new Object[]{"21", "1", "findEqualCompareInFunc", new Object[]{"foo"}});
        commands.add(new Object[]{"22", "2", "findFuncWithBoolParam", new Object[]{}});
        commands.add(new Object[]{"23", "3", "findUnusedParamInFunc", new Object[]{"foo"}});
        commands.add(new Object[]{"24", "4", "findDirectCalledOtherB", new Object[]{"B"}});
        commands.add(new Object[]{"25", "5", "answerIfACalledB", new Object[]{"foo", "bar"}});


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
        expectedResults.add(Set.of("K", "I", "J", "F", "B", "A"));


        QueryOnNode query = new QueryOnNode(engine.getId2ASTModule());
        expectedResults.add(query.findFuncWithArgGtN.apply(4));
        expectedResults.add(query.calculateOp2Nums.get());
        expectedResults.add(query.processNodeFreq.get());
        expectedResults.add(query.calculateNode2Nums.apply("2"));

        expectedResults.add(Set.of(
                "2:7-2:21",
                "3:12-3:26",
                "5:12-5:26"));
        expectedResults.add(Set.of("toggle_light"));
        expectedResults.add(Set.of("param2", "param3"));
        expectedResults.add(Set.of("A"));
        expectedResults.add(true);

        long mode0timeElapsed = 0, mode1timeElapsed = 0, mode2timeElapsed = 0;
        long start, finish;
        
        QueryOnClass.clearCounts();
        start = System.nanoTime();
        engine.processCommands(commands, 0);
        var ret1 = engine.getAllResults();
        finish = System.nanoTime();
        mode0timeElapsed += finish - start;

        QueryOnClass.clearCounts();
        start = System.nanoTime();
        engine.processCommands(commands, 1);
        var ret2 = engine.getAllResults();
        finish = System.nanoTime();
        mode1timeElapsed += finish - start;

        QueryOnClass.clearCounts();
        start = System.nanoTime();
        engine.processCommands(commands, 2);
        var ret3 = engine.getAllResults();
        finish = System.nanoTime();
        mode2timeElapsed += finish - start;


        System.out.println(mode0timeElapsed);
        System.out.println(mode1timeElapsed);
        System.out.println(mode2timeElapsed);

        assertEquals(ret1, ret2);        
        assertEquals(ret2, ret3);


        assertTrue(mode0timeElapsed > mode1timeElapsed);
        assertTrue(mode1timeElapsed > mode2timeElapsed);
    }

    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testSerialExecution() throws InterruptedException {

        final AtomicBoolean running = new AtomicBoolean(true);
        final int[] maxNumThread = {Thread.activeCount()};

        Thread counterThread = new Thread(() -> {
            maxNumThread[0] = Thread.activeCount();
            while (running.get()) {
                int curThread = Thread.activeCount();
                if (curThread >= maxNumThread[0]) {
                    maxNumThread[0] = curThread;
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxml/", List.of("18", "19", "1"), 4);

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "0", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "18", "haveSuperClass", new Object[]{"B", "A"}});

        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);

        counterThread.start();
        final int initialNumThread = Thread.activeCount();
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalPrintStream = System.out;
        System.setOut(printStream);
        QueryOnClass.clearCounts();
        engine.processCommands(commands, 0);
        System.setOut(originalPrintStream);
        String printedOutput = outputStream.toString();
        running.set(false);
        counterThread.join();
    
        List<Object> allResults = engine.getAllResults();
        TestUtil.checkResults(expectedResults, allResults, commands);
        TestUtil.checkConsoleOutput(
                List.of("[LOG FROM QueryOnClass] Querying findClassesWithMain on AST 18",
                        "[LOG FROM QueryOnClass] Querying findClassesWithMain on AST 19",
                        "[LOG FROM QueryOnNode] Querying calculateOp2Nums on #AST 3",
                        "[LOG FROM QueryOnClass] Querying haveSuperClass on AST 18"),
                new ArrayList<>(Arrays.asList(printedOutput.split("\\r?\\n"))),
                true);
        assertEquals(initialNumThread, maxNumThread[0]);
    }


    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testParallelExecution() throws InterruptedException {

        final AtomicBoolean running = new AtomicBoolean(true);
        final int[] maxNumThread = {Thread.activeCount()};

        Thread counterThread = new Thread(() -> {
            maxNumThread[0] = Thread.activeCount();
            while (running.get()) {
                int curThread = Thread.activeCount();
                if (curThread >= maxNumThread[0]) {
                    maxNumThread[0] = curThread;
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxml/", List.of("18", "19", "1"), 4);

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "0", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "18", "haveSuperClass", new Object[]{"B", "A"}});

        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);

        counterThread.start();
        final int initialNumThread = Thread.activeCount();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalPrintStream = System.out;
        System.setOut(printStream);
        QueryOnClass.clearCounts();
        engine.processCommands(commands, 1);
        System.setOut(originalPrintStream);
        String printedOutput = outputStream.toString();
        running.set(false);
        counterThread.join();

        List<Object> allResults = engine.getAllResults();
        TestUtil.checkResults(expectedResults, allResults, commands);
        TestUtil.checkConsoleOutput(
                List.of("[LOG FROM QueryOnClass] Querying findClassesWithMain on AST 18",
                        "[LOG FROM QueryOnClass] Querying findClassesWithMain on AST 19",
                        "[LOG FROM QueryOnNode] Querying calculateOp2Nums on #AST 1",
                        "[LOG FROM QueryOnNode] Querying calculateOp2Nums on #AST 1",
                        "[LOG FROM QueryOnNode] Querying calculateOp2Nums on #AST 1",
                        "[LOG FROM QueryOnClass] Querying haveSuperClass on AST 18"),
                new ArrayList<>(Arrays.asList(printedOutput.split("\\r?\\n"))),
                false);
        assertTrue(initialNumThread <= maxNumThread[0]);
    }

    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testParallelExecutionOrder() throws InterruptedException {

        final AtomicBoolean running = new AtomicBoolean(true);
        final int[] maxNumThread = {Thread.activeCount()};

        Thread counterThread = new Thread(() -> {
            maxNumThread[0] = Thread.activeCount();
            while (running.get()) {
                int curThread = Thread.activeCount();
                if (curThread >= maxNumThread[0]) {
                    maxNumThread[0] = curThread;
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxml/", List.of("18", "19", "1"), 4);

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "0", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "18", "haveSuperClass", new Object[]{"B", "A"}});

        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);

        counterThread.start();
        final int initialNumThread = Thread.activeCount();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalPrintStream = System.out;
        System.setOut(printStream);
        QueryOnClass.clearCounts();
        engine.processCommands(commands, 2);
        System.setOut(originalPrintStream);
        String printedOutput = outputStream.toString();
        running.set(false);
        counterThread.join();

        List<Object> allResults = engine.getAllResults();
        TestUtil.checkResults(expectedResults, allResults, commands);
        TestUtil.checkConsoleOutput(
                List.of("[LOG FROM QueryOnClass] Querying findClassesWithMain on AST 18",
                        "[LOG FROM QueryOnClass] Querying findClassesWithMain on AST 19",
                        "[LOG FROM QueryOnNode] Querying calculateOp2Nums on #AST 1",
                        "[LOG FROM QueryOnNode] Querying calculateOp2Nums on #AST 1",
                        "[LOG FROM QueryOnNode] Querying calculateOp2Nums on #AST 1",
                        "[LOG FROM QueryOnClass] Querying haveSuperClass on AST 18"),
                new ArrayList<>(Arrays.asList(printedOutput.split("\\r?\\n"))),
                false);
        assertTrue(initialNumThread <= maxNumThread[0]);
    }
}
