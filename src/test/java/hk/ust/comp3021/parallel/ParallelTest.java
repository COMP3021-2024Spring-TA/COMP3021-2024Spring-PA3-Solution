package hk.ust.comp3021.parallel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import hk.ust.comp3021.RapidASTManagerEngine;
import hk.ust.comp3021.utils.TestKind;
import hk.ust.comp3021.utils.ASTModule;

public class ParallelTest {
    @Tag(TestKind.PUBLIC)
    @Test
    public void testParallelLoading() {
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml/", List.of("1", "2", "3", "100"));
        assertEquals(3, engine.getId2ASTModule2().size());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    public void testParallelQuery() {


    }
}
