package hk.ust.comp3021.misc;

import hk.ust.comp3021.ASTManagerEngine;
import hk.ust.comp3021.utils.TestKind;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ASTElementTest {
    @Tag(TestKind.PUBLIC)
    @Test
    public void testASTFileter() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", String.valueOf(1));
        var results = engine.getId2ASTModules().get("1").filter(node -> node.getLineNo() == 2);
        assertEquals(4, results.size());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    public void testASTForEach() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", String.valueOf(1));
        List<ASTElement> results = new ArrayList<>();
        engine.getId2ASTModules().get("1").forEach(
                node -> {
                    if (node.getLineNo() == 2) {
                        results.add(node);
                    }
                    ;
                });
        assertEquals(4, results.size());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    public void testASTGroupingBy() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", String.valueOf(1));
        Map<Integer, List<ASTElement>> lineNo2Elems =  engine.getId2ASTModules().get("1").groupingBy(
                ASTElement::getLineNo,
                Collectors.toList()
                );
        assertEquals(5, lineNo2Elems.get(5).size());
    }
}
