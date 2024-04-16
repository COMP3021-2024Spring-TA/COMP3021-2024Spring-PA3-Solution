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

import hk.ust.comp3021.ASTManagerEngine;
import hk.ust.comp3021.utils.TestKind;
import hk.ust.comp3021.utils.ASTModule;

public class ParallelTest {
    @Tag(TestKind.PUBLIC)
    @Test
    public void testParallelLoading() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processingXMLParsingParallel("resources/pythonxml", List.of("18", "19", "20", "100"));
        HashMap<String, ASTModule> id2ASTModules = engine.getId2ASTModules();
        assertEquals(id2ASTModules.size(), 3);
    }

    @Tag(TestKind.PUBLIC)
    @Test
    public void testParallelQuery() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processingXMLParsingParallel("resources/pythonxml", List.of("18", "19", "20"));

        HashMap<String, List<String>> orders = new HashMap<>();
        orders.put("18", List.of("19"));
        orders.put("19", List.of("20"));

        engine.findClassesWithMainParallel(orders);

        HashMap<String, List<String>> results = engine.getQueryResults();
        HashMap<String, List<String>> expectedResults = new HashMap<>();
        expectedResults.put("18", List.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.put("19", List.of("C", "D", "F", "G", "H"));
        expectedResults.put("20", List.of("B", "D"));
        assertEquals(expectedResults, results);

        
        List<String>queryOrder = engine.getQueryRunningOrder();
        List<String> expectedOrder = List.of("18", "19", "20");
        assertEquals(queryOrder, expectedOrder);


    }
}
