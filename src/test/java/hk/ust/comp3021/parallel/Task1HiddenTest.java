package hk.ust.comp3021.parallel;

import hk.ust.comp3021.RapidASTManagerEngine;
import hk.ust.comp3021.utils.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task1HiddenTest {
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
    public void testParallelLoadingPoolNumThreadCorrectNum() throws InterruptedException {
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxml/",
                IntStream.rangeClosed(0, 33)
                        .boxed()
                        .map(Objects::toString)
                        .collect(Collectors.toList()),
                3);
        assertEquals(33, engine.getId2ASTModule().size());
        assertTrue(initialNumThread + 3 >= maxNumThread[0]);
    }

    
    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testParallelLoadingPoolNumThread() throws InterruptedException {
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxml/",
                IntStream.rangeClosed(0, 1000)
                        .boxed()
                        .map(Objects::toString)
                        .collect(Collectors.toList()),
                2);
        assertEquals(33, engine.getId2ASTModule().size());
        assertTrue(initialNumThread + 2 >=maxNumThread[0] && maxNumThread[0] >= initialNumThread);
    }

    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testParallelLoadingAllPoolCorrectNum() throws InterruptedException {
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxmlPA1/",
                IntStream.rangeClosed(0, 836)
                        .boxed()
                        .map(Object::toString)
                        .collect(Collectors.toList()), 5);
        assertEquals(837, engine.getId2ASTModule().size());
        assertTrue(initialNumThread + 5 >= maxNumThread[0]);
    }

    @Tag(TestKind.HIDDEN)
    @RepeatedTest(50)
    public void testParallelLoadingAllPool() throws InterruptedException {
        initialNumThread = Thread.activeCount();
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsingPool("resources/pythonxmlPA1/",
                IntStream.rangeClosed(0, 2000)
                        .boxed()
                        .map(Object::toString)
                        .collect(Collectors.toList()), 5);
        assertEquals(837, engine.getId2ASTModule().size());
        assertEquals(initialNumThread + 5, maxNumThread[0]);
    }
}
