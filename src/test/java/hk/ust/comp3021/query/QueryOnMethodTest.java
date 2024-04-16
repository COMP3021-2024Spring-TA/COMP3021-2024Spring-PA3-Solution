package hk.ust.comp3021.query;

import hk.ust.comp3021.ASTManagerEngine;
import hk.ust.comp3021.utils.TestKind;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class QueryOnMethodTest {

    @Tag(TestKind.PUBLIC)
    @Test
    public void testFindEqualCompareInFunc() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", String.valueOf(1));
        QueryOnMethod queryOnMethod = new QueryOnMethod(engine.getId2ASTModules().get("1"));
        List<String> exprs = queryOnMethod.findEqualCompareInFunc.apply("foo");

        // the function name should be astID_FuncName_UniqueID
        Set<String> expectedOutput = Set.of(
                "2:7-2:21",
                "3:12-3:26",
                "5:12-5:26");
        assertEquals(expectedOutput, new HashSet<>(exprs));
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testFindFuncWithBoolParam() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", String.valueOf(2));
        QueryOnMethod queryOnMethod = new QueryOnMethod(engine.getId2ASTModules().get("2"));
        
        List<String> funcNames = queryOnMethod.findFuncWithBoolParam.get();
        Set<String> expectedOutput = Set.of("toggle_light");
        assertEquals(expectedOutput, new HashSet<>(funcNames));
    }
    
    @Tag(TestKind.PUBLIC)
    @Test
    void testFindUnusedParamInFunc() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", String.valueOf(3));
        QueryOnMethod queryOnMethod = new QueryOnMethod(engine.getId2ASTModules().get("3"));

        List<String> params = queryOnMethod.findUnusedParamInFunc.apply("foo");
        Set<String> expectedOutput = Set.of("param2", "param3");
        assertEquals(expectedOutput, new HashSet<>(params));
    }

    @Tag(TestKind.PUBLIC)
    @Test
    public void testFindDirectCalledOtherB() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", String.valueOf(4));
        QueryOnMethod queryOnMethod = new QueryOnMethod(engine.getId2ASTModules().get("4"));

        List<String> params = queryOnMethod.findDirectCalledOtherB.apply("B");
        Set<String> expectedOutput = Set.of("A");
        assertEquals(expectedOutput, new HashSet<>(params));

        params = queryOnMethod.findDirectCalledOtherB.apply("D");
        expectedOutput = Set.of("A");
        assertEquals(expectedOutput, new HashSet<>(params));
    }

    @Tag(TestKind.PUBLIC)
    @Test
    public void testAnswerIfACalledB() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxml", String.valueOf(5));
        QueryOnMethod queryOnMethod = new QueryOnMethod(engine.getId2ASTModules().get("5"));
        
        assertEquals(true, queryOnMethod.answerIfACalledB.test("foo", "bar"));
    }


}
