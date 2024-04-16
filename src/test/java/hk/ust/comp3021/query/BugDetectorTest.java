package hk.ust.comp3021.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import hk.ust.comp3021.ASTManagerEngine;
import hk.ust.comp3021.utils.TestKind;

public class BugDetectorTest {
    @Tag(TestKind.PUBLIC)
    @Test
    public void testBugDetector() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", String.valueOf(10));
        BugDetector bugDetector = new BugDetector(engine.getId2ASTModules().get("10"));
        List<String> functionsWithBugs = bugDetector.detect.get();

        Set<String> expectedOutput = Set.of("bar3", "bar");
        assertEquals(expectedOutput, new HashSet<String>(functionsWithBugs));
    }
}
