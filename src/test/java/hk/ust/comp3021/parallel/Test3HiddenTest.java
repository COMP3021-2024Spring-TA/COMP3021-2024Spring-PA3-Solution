package hk.ust.comp3021.parallel;

import hk.ust.comp3021.RapidASTManagerEngine;
import hk.ust.comp3021.query.QueryOnClass;
import hk.ust.comp3021.utils.TestKind;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class Test3HiddenTest {

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
    public void testInterleavedImportQueryNumThreadNoExp() {
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "1", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "19", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"5", "18", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"6", "1", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"7", "18", "haveSuperClass", new Object[]{"B", "A"}});

        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalPrintStream = System.err;
        System.setErr(printStream);

        QueryOnClass.clearCounts();
        engine.processCommandsInterLeaved(commands);
        System.setErr(originalPrintStream);
        String printedOutput = outputStream.toString();

        assertTrue(!printedOutput.contains("java.lang.NullPointerException")
                && !printedOutput.contains("because \"this.module\" is null"));
        assertTrue(initialNumThread <= maxNumThread[0]);
    }

    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testInterleavedImportQueryNumThreadCorrectNum() {
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "1", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "19", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"5", "18", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"6", "1", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"7", "18", "haveSuperClass", new Object[]{"B", "A"}});

        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalPrintStream = System.err;
        System.setErr(printStream);
        QueryOnClass.clearCounts();
        engine.processCommandsInterLeaved(commands);
        System.setErr(originalPrintStream);
        String printedOutput = outputStream.toString();

        assertTrue(initialNumThread <= maxNumThread[0]);
    }

    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testInterleavedImportQueryTwoNoExp() {
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "1", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "19", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"5", "18", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"6", "1", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"7", "18", "haveSuperClass", new Object[]{"B", "A"}});

        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalPrintStream = System.err;
        System.setErr(printStream);
        QueryOnClass.clearCounts();
        engine.processCommandsInterLeavedTwoThread(commands);
        System.setErr(originalPrintStream);
        String printedOutput = outputStream.toString();

        assertTrue(!printedOutput.contains("java.lang.NullPointerException")
                && !printedOutput.contains("because \"this.module\" is null"));
        assertTrue(initialNumThread <= maxNumThread[0]);
    }

    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testInterleavedImportQueryTwoCorrectNum() {
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "1", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "19", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"5", "18", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"6", "1", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"7", "18", "haveSuperClass", new Object[]{"B", "A"}});

        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);

        QueryOnClass.clearCounts();
        engine.processCommandsInterLeavedTwoThread(commands);
        assertTrue(initialNumThread + 2 >= maxNumThread[0] && initialNumThread <= maxNumThread[0]);
    }

    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testInterleavedImportQueryNumThreadTime() {
        // maybe unuseful
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "1", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "19", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"5", "18", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"6", "1", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"7", "18", "haveSuperClass", new Object[]{"B", "A"}});

        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);

        QueryOnClass.clearCounts();
        long start = System.nanoTime();
        engine.processCommandsInterLeaved(commands);
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

//        assertTrue(serialTimeElapsed > paralleTimeElapsed);
    }
    
    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testInterleavedImportQueryTwoCorrectTime() {
        // maybe unuseful
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        commands.add(new Object[]{"1", "18", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"2", "19", "findClassesWithMain", new Object[]{}});
        commands.add(new Object[]{"3", "1", "calculateOp2Nums", new Object[]{}});
        commands.add(new Object[]{"4", "19", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"5", "18", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"6", "1", "processXMLParsing", new Object[]{"resources/pythonxml/"}});
        commands.add(new Object[]{"7", "18", "haveSuperClass", new Object[]{"B", "A"}});

        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);

        QueryOnClass.clearCounts();        
        long start = System.nanoTime();
        engine.processCommandsInterLeavedTwoThread(commands);
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

//        assertTrue(serialTimeElapsed > paralleTimeElapsed);
    }
}
