package hk.ust.comp3021.parallel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
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

    public void checkResults(List<Object> expecteds, List<Object> actuals, List<Object[]> commands) {
        for(int i=0; i<expecteds.size(); i++) {
            Object expected = expecteds.get(i);
            Object actual = actuals.get(i);
            String queryName = (String) ((Object[])commands.get(i))[2];

            if(queryName == "findClassesWithMain") {
                assertEquals((Set<String>)expected, new HashSet<String>((List<String>)actual));
            } else {
                assertEquals(expected, actual);
            }

        }

    }

    @Tag(TestKind.PUBLIC)
    @Test
    public void testParallelLoading() {
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml/", List.of("18", "19", "20", "100"));
        assertEquals(3, engine.getId2ASTModule().size());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    public void testSerialExecution() {
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml/", List.of("18", "19", "20"));

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        commands.add(new Object[] {"1", "18", "findClassesWithMain", new Object[] {}}); 
        commands.add(new Object[] {"1", "19", "findClassesWithMain", new Object[] {}});
        commands.add(new Object[] {"1", "20", "findClassesWithMain", new Object[] {}});
        commands.add(new Object[] {"1", "18", "haveSuperClass", new Object[] {"B", "A"}});
        
        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        expectedResults.add(Set.of("B", "D"));
        expectedResults.add(true);

        engine.processCommands(commands, 0);

        List<Object> allResults = engine.getAllResults();
        
        checkResults(expectedResults, allResults, commands);
    }


    @Tag(TestKind.PUBLIC)
    @Test
    public void testParallelExecution() {
        RapidASTManagerEngine engine = new RapidASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml/", List.of("18", "19", "1"));

        List<Object[]> commands = new ArrayList<>();
        List<Object> expectedResults = new ArrayList<>();
        commands.add(new Object[] {"1", "18", "findClassesWithMain", new Object[] {}}); 
        commands.add(new Object[] {"1", "19", "findClassesWithMain", new Object[] {}});
        commands.add(new Object[] {"0", "1", "calculateOp2Nums", new Object[] {}});
        commands.add(new Object[] {"1", "18", "haveSuperClass", new Object[] {"B", "A"}});
        
        expectedResults.add(Set.of("B", "C", "D", "E", "F", "G", "H"));
        expectedResults.add(Set.of("C", "D", "F", "G", "H"));
        HashMap<String, Integer> m3 = new HashMap<>();
        m3.put("Eq", 3);
        expectedResults.add(m3);
        expectedResults.add(true);

        engine.processCommands(commands, 1);

        List<Object> allResults = engine.getAllResults();
        
        checkResults(expectedResults, allResults, commands);

    }

    @Tag(TestKind.PUBLIC)
    @Test
    public void testParallelExecutionWithOrder() {

    }
}
