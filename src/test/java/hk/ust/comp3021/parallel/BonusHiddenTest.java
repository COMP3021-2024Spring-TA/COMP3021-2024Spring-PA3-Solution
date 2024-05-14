package hk.ust.comp3021.parallel;

import hk.ust.comp3021.RapidASTManagerEngine;
import hk.ust.comp3021.query.QueryOnClass;
import hk.ust.comp3021.query.QueryOnNode;
import hk.ust.comp3021.utils.TestKind;
import hk.ust.comp3021.utils.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BonusHiddenTest {

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final int[] maxNumThread = {Thread.activeCount()};
    private int initialNumThread = 0;
    private Thread counterThread = null;

    @BeforeEach
    public void startCount() {
        running.set(true);
        counterThread = new Thread(() -> {
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
        counterThread.start();

    }

    @AfterEach
    public void endCount() throws InterruptedException {
        running.set(false);
        counterThread.join();
    }

    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testInterleavedImportQueryTest1() {
        // check when one AST is not loaded
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();

        List<Object[]> commands = new ArrayList<>();
        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "1", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "18", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"5", "1", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"6", "18", "haveSuperClass", new Object[]{"B", "A"}});

        List<Object> expectedResults = new ArrayList<>();
        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalPrintStream = System.err;
        System.setErr(printStream);

        QueryOnClass.clearCounts();
        engine.processCommandsInterLeavedFixedThread(commands, 4);
        List<Object> allResults = engine.getAllResults();

        System.setErr(originalPrintStream);
        String printedOutput = outputStream.toString();

        // check the correctness of results
        TestUtil.checkResults(expectedResults, allResults,
                Arrays.asList(commands.get(0), commands.get(2), commands.get(5)));

        // check no NULL pointer exceptions
        assertTrue(!printedOutput.contains("java.lang.NullPointerException")
                && !printedOutput.contains("because \"this.module\" is null"));

        // check number of thread is correct
        assertTrue(initialNumThread + 4 >= maxNumThread[0] && initialNumThread <= maxNumThread[0]);
    }


    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testInterleavedImportQueryNumThreadTest2() {
        // check when another queryOnNode command results
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();

        List<Object[]> commands = new ArrayList<>();

        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "1", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "18", "processNodeFreq", new Object[]{}});
        commands.add(new Object[]{"5", "18", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"6", "19", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"7", "1", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"8", "18", "haveSuperClass", new Object[]{"B", "A"}});

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalPrintStream = System.err;
        System.setErr(printStream);

        QueryOnClass.clearCounts();
        engine.processCommandsInterLeavedFixedThread(commands, 4);
        List<Object> allResults = engine.getAllResults();

        System.setErr(originalPrintStream);
        String printedOutput = outputStream.toString();

        List<Object> expectedResults = new ArrayList<>();
        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        QueryOnNode queryOnNode = new QueryOnNode(engine.getId2ASTModule());
        expectedResults.add(queryOnNode.processNodeFreq.get());
        expectedResults.add(true);

        // check correctness
        TestUtil.checkResults(expectedResults, allResults,
                Arrays.asList(commands.get(0), commands.get(1), commands.get(2), commands.get(3), commands.get(7)));

        // check no exception
        assertTrue(!printedOutput.contains("java.lang.NullPointerException")
                && !printedOutput.contains("because \"this.module\" is null"));

        // check num thread
        assertTrue(initialNumThread + 4 >= maxNumThread[0] && initialNumThread <= maxNumThread[0]);
    }
    
    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testInterleavedImportQueryNumThreadNoExp() {
        // the same as public test cases, but check exception & number of threads
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();

        List<Object[]> commands = new ArrayList<>();
        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "1", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "19", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"5", "18", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"6", "1", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"7", "18", "haveSuperClass", new Object[]{"B", "A"}});
        
        commands.add(new Object[]{"8", "16", "findOverridingMethods", new Object[]{}}); // 1 -> 2, 1 -> 5
        commands.add(new Object[]{"9", "16", "findAllMethods", new Object[]{"H"}}); // 2 -> 6
        commands.add(new Object[]{"10", "16", "findAllMethods", new Object[]{"I"}});  // 3 -> 5
        commands.add(new Object[]{"11", "16", "haveSuperClass", new Object[]{"I", "H"}}); // 4 -> 5
        commands.add(new Object[]{"12", "16", "findSuperClasses", new Object[]{"I"}});
        commands.add(new Object[]{"13", "16", "findSuperClasses", new Object[]{"H"}});
        commands.add(new Object[]{"14", "16", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        
        commands.add(new Object[]{"15", "11", "findClassesWithMain", new Object[]{}}); //  7 -> 8
        commands.add(new Object[]{"16", "11", "findAllMethods", new Object[]{"H"}});
        commands.add(new Object[]{"17", "11", "haveSuperClass", new Object[]{"D", "B"}});
        commands.add(new Object[]{"18", "11", "haveSuperClass", new Object[]{"M", "K"}});
        commands.add(new Object[]{"19", "11", "haveSuperClass", new Object[]{"L", "I"}});
        commands.add(new Object[]{"20", "11", "haveSuperClass", new Object[]{"H", "C"}});  // 12 -> 14 
        commands.add(new Object[]{"21", "11", "findSuperClasses", new Object[]{"D"}});  // 13 -> 9, 13 -> 7
        commands.add(new Object[]{"22", "11", "findSuperClasses", new Object[]{"H"}}); // 8 -> 14 
        commands.add(new Object[]{"23", "11", "findSuperClasses", new Object[]{"M"}}); // 11 -> 15
        commands.add(new Object[]{"24", "11", "findSuperClasses", new Object[]{"L"}});
        commands.add(new Object[]{"25", "11", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        
        
        List<Object> expectedResults = new ArrayList<>();

        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);
        
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

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalPrintStream = System.err;
        System.setErr(printStream);

        QueryOnClass.clearCounts();
        engine.processCommandsInterLeavedFixedThread(commands, 4);

        System.setErr(originalPrintStream);
        String printedOutput = outputStream.toString();

        commands.removeIf(command -> ((String) command[2]).equals("processXMLParsing"));
        // check correctness
        TestUtil.checkResults(expectedResults, engine.getAllResults(), commands);

        assertTrue(!printedOutput.contains("java.lang.NullPointerException")
                && !printedOutput.contains("because \"this.module\" is null"));
        assertTrue(initialNumThread + 4 >= maxNumThread[0] && initialNumThread <= maxNumThread[0]);
    }
    
    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testInterleavedImportQueryNumThreadTime() {
        // maybe unuseful
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();

        List<Object[]> commands = new ArrayList<>();
        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "1", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "19", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"5", "18", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"6", "1", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"7", "18", "haveSuperClass", new Object[]{"B", "A"}});

        commands.add(new Object[]{"8", "16", "findOverridingMethods", new Object[]{}}); // 1 -> 2, 1 -> 5
        commands.add(new Object[]{"9", "16", "findAllMethods", new Object[]{"H"}}); // 2 -> 6
        commands.add(new Object[]{"10", "16", "findAllMethods", new Object[]{"I"}});  // 3 -> 5
        commands.add(new Object[]{"11", "16", "haveSuperClass", new Object[]{"I", "H"}}); // 4 -> 5
        commands.add(new Object[]{"12", "16", "findSuperClasses", new Object[]{"I"}});
        commands.add(new Object[]{"13", "16", "findSuperClasses", new Object[]{"H"}});
        commands.add(new Object[]{"14", "16", "processXMLParsing", new Object[]{"resources/pythonxml/"}});

        commands.add(new Object[]{"15", "11", "findClassesWithMain", new Object[]{}}); //  7 -> 8
        commands.add(new Object[]{"16", "11", "findAllMethods", new Object[]{"H"}});
        commands.add(new Object[]{"17", "11", "haveSuperClass", new Object[]{"D", "B"}});
        commands.add(new Object[]{"18", "11", "haveSuperClass", new Object[]{"M", "K"}});
        commands.add(new Object[]{"19", "11", "haveSuperClass", new Object[]{"L", "I"}});
        commands.add(new Object[]{"20", "11", "haveSuperClass", new Object[]{"H", "C"}});  // 12 -> 14 
        commands.add(new Object[]{"21", "11", "findSuperClasses", new Object[]{"D"}});  // 13 -> 9, 13 -> 7
        commands.add(new Object[]{"22", "11", "findSuperClasses", new Object[]{"H"}}); // 8 -> 14 
        commands.add(new Object[]{"23", "11", "findSuperClasses", new Object[]{"M"}}); // 11 -> 15
        commands.add(new Object[]{"24", "11", "findSuperClasses", new Object[]{"L"}});
        commands.add(new Object[]{"25", "11", "processXMLParsing", new Object[]{"resources/pythonxml/"}});

        List<Object> expectedResults = new ArrayList<>();

        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);

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
        List<Object> parallelResults = engine.getAllResults();

        QueryOnClass.clearCounts();
        long start = System.nanoTime();
        engine.processCommandsInterLeavedFixedThread(commands, 4);
        long finish = System.nanoTime();
        long paralleTimeElapsed = finish - start;

        QueryOnClass.clearCounts();
        start = System.nanoTime();
        for (Object[] command : commands) {
            if (command[2].equals("processXMLParsing")) {
                ParserWorker parser = new ParserWorker((String) command[1],
                        (String) ((Object[]) command[3])[0], engine.getId2ASTModule());
                parser.run();
            }
        }
        for (Object[] command : commands) {
            QueryWorker worker = new QueryWorker(engine.getId2ASTModule(), (String) command[0],
                    (String) command[1], (String) command[2], (Object[]) command[3], 0);
            worker.run();
        }
        finish = System.nanoTime();
        long serialTimeElapsed = finish - start;
        System.out.println(serialTimeElapsed);
        System.out.println(paralleTimeElapsed);
        commands.removeIf(command -> ((String) command[2]).equals("processXMLParsing"));
        TestUtil.checkResults(expectedResults, parallelResults, commands);
        assertTrue(serialTimeElapsed > paralleTimeElapsed);
    }

    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testInterleavedImportQueryNumThreadTime10() {
        // maybe unuseful
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();

        List<Object[]> commands = new ArrayList<>();
        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "1", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "19", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"5", "18", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"6", "1", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"7", "18", "haveSuperClass", new Object[]{"B", "A"}});

        commands.add(new Object[]{"8", "16", "findOverridingMethods", new Object[]{}}); // 1 -> 2, 1 -> 5
        commands.add(new Object[]{"9", "16", "findAllMethods", new Object[]{"H"}}); // 2 -> 6
        commands.add(new Object[]{"10", "16", "findAllMethods", new Object[]{"I"}});  // 3 -> 5
        commands.add(new Object[]{"11", "16", "haveSuperClass", new Object[]{"I", "H"}}); // 4 -> 5
        commands.add(new Object[]{"12", "16", "findSuperClasses", new Object[]{"I"}});
        commands.add(new Object[]{"13", "16", "findSuperClasses", new Object[]{"H"}});
        commands.add(new Object[]{"14", "16", "processXMLParsing", new Object[]{"resources/pythonxml/"}});

        commands.add(new Object[]{"15", "11", "findClassesWithMain", new Object[]{}}); //  7 -> 8
        commands.add(new Object[]{"16", "11", "findAllMethods", new Object[]{"H"}});
        commands.add(new Object[]{"17", "11", "haveSuperClass", new Object[]{"D", "B"}});
        commands.add(new Object[]{"18", "11", "haveSuperClass", new Object[]{"M", "K"}});
        commands.add(new Object[]{"19", "11", "haveSuperClass", new Object[]{"L", "I"}});
        commands.add(new Object[]{"20", "11", "haveSuperClass", new Object[]{"H", "C"}});  // 12 -> 14 
        commands.add(new Object[]{"21", "11", "findSuperClasses", new Object[]{"D"}});  // 13 -> 9, 13 -> 7
        commands.add(new Object[]{"22", "11", "findSuperClasses", new Object[]{"H"}}); // 8 -> 14 
        commands.add(new Object[]{"23", "11", "findSuperClasses", new Object[]{"M"}}); // 11 -> 15
        commands.add(new Object[]{"24", "11", "findSuperClasses", new Object[]{"L"}});
        commands.add(new Object[]{"25", "11", "processXMLParsing", new Object[]{"resources/pythonxml/"}});

        List<Object> expectedResults = new ArrayList<>();

        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);

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
        List<Object> parallelResults = engine.getAllResults();

        QueryOnClass.clearCounts();
        long start = System.nanoTime();
        engine.processCommandsInterLeavedFixedThread(commands, 10);
        long finish = System.nanoTime();
        long paralleTimeElapsed = finish - start;

        QueryOnClass.clearCounts();
        start = System.nanoTime();
        for (Object[] command : commands) {
            if (command[2].equals("processXMLParsing")) {
                ParserWorker parser = new ParserWorker((String) command[1],
                        (String) ((Object[]) command[3])[0], engine.getId2ASTModule());
                parser.run();
            }
        }
        for (Object[] command : commands) {
            QueryWorker worker = new QueryWorker(engine.getId2ASTModule(), (String) command[0],
                    (String) command[1], (String) command[2], (Object[]) command[3], 0);
            worker.run();
        }
        finish = System.nanoTime();
        long serialTimeElapsed = finish - start;
        System.out.println(serialTimeElapsed);
        System.out.println(paralleTimeElapsed);
        commands.removeIf(command -> ((String) command[2]).equals("processXMLParsing"));
        TestUtil.checkResults(expectedResults, parallelResults, commands);
        assertTrue(serialTimeElapsed > paralleTimeElapsed);
    }


    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testInterleavedImportQueryNumThreadTime20() {
        // maybe unuseful
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();

        List<Object[]> commands = new ArrayList<>();
        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "1", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "19", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"5", "18", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"6", "1", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"7", "18", "haveSuperClass", new Object[]{"B", "A"}});

        commands.add(new Object[]{"8", "16", "findOverridingMethods", new Object[]{}}); // 1 -> 2, 1 -> 5
        commands.add(new Object[]{"9", "16", "findAllMethods", new Object[]{"H"}}); // 2 -> 6
        commands.add(new Object[]{"10", "16", "findAllMethods", new Object[]{"I"}});  // 3 -> 5
        commands.add(new Object[]{"11", "16", "haveSuperClass", new Object[]{"I", "H"}}); // 4 -> 5
        commands.add(new Object[]{"12", "16", "findSuperClasses", new Object[]{"I"}});
        commands.add(new Object[]{"13", "16", "findSuperClasses", new Object[]{"H"}});
        commands.add(new Object[]{"14", "16", "processXMLParsing", new Object[]{"resources/pythonxml/"}});

        commands.add(new Object[]{"15", "11", "findClassesWithMain", new Object[]{}}); //  7 -> 8
        commands.add(new Object[]{"16", "11", "findAllMethods", new Object[]{"H"}});
        commands.add(new Object[]{"17", "11", "haveSuperClass", new Object[]{"D", "B"}});
        commands.add(new Object[]{"18", "11", "haveSuperClass", new Object[]{"M", "K"}});
        commands.add(new Object[]{"19", "11", "haveSuperClass", new Object[]{"L", "I"}});
        commands.add(new Object[]{"20", "11", "haveSuperClass", new Object[]{"H", "C"}});  // 12 -> 14 
        commands.add(new Object[]{"21", "11", "findSuperClasses", new Object[]{"D"}});  // 13 -> 9, 13 -> 7
        commands.add(new Object[]{"22", "11", "findSuperClasses", new Object[]{"H"}}); // 8 -> 14 
        commands.add(new Object[]{"23", "11", "findSuperClasses", new Object[]{"M"}}); // 11 -> 15
        commands.add(new Object[]{"24", "11", "findSuperClasses", new Object[]{"L"}});
        commands.add(new Object[]{"25", "11", "processXMLParsing", new Object[]{"resources/pythonxml/"}});

        List<Object> expectedResults = new ArrayList<>();

        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);

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
        List<Object> parallelResults = engine.getAllResults();

        QueryOnClass.clearCounts();
        long start = System.nanoTime();
        engine.processCommandsInterLeavedFixedThread(commands, 20);
        long finish = System.nanoTime();
        long paralleTimeElapsed = finish - start;

        QueryOnClass.clearCounts();
        start = System.nanoTime();
        for (Object[] command : commands) {
            if (command[2].equals("processXMLParsing")) {
                ParserWorker parser = new ParserWorker((String) command[1],
                        (String) ((Object[]) command[3])[0], engine.getId2ASTModule());
                parser.run();
            }
        }
        for (Object[] command : commands) {
            QueryWorker worker = new QueryWorker(engine.getId2ASTModule(), (String) command[0],
                    (String) command[1], (String) command[2], (Object[]) command[3], 0);
            worker.run();
        }
        finish = System.nanoTime();
        long serialTimeElapsed = finish - start;
        System.out.println(serialTimeElapsed);
        System.out.println(paralleTimeElapsed);
        commands.removeIf(command -> ((String) command[2]).equals("processXMLParsing"));
        TestUtil.checkResults(expectedResults, parallelResults, commands);
        assertTrue(serialTimeElapsed > paralleTimeElapsed);
    }
}
