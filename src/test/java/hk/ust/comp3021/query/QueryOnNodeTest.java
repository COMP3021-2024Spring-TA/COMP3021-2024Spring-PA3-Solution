package hk.ust.comp3021.query;

import hk.ust.comp3021.ASTManagerEngine;
import hk.ust.comp3021.utils.TestKind;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class QueryOnNodeTest {

    @Tag(TestKind.PUBLIC)
    @Test
    public void testPrintedInformation() {
        ASTManagerEngine engine = new ASTManagerEngine();
        engine.processXMLParsing("resources/pythonxmlPA1", String.valueOf(227));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalPrintStream = System.out;
        System.setOut(printStream);

        engine.queryOnNode.findFuncWithArgGtN.accept(4);
        System.setOut(originalPrintStream);
        String printedOutput = outputStream.toString();

        // the function name should be astID_FuncName_UniqueID
        Set<String> expectedOutput = Set.of(
                "227_diagonalBinarySearch_2",
                "227_rowBinarySearch_13",
                "227_colBinarySearch_27");
        assertEquals(expectedOutput, Set.of(printedOutput.trim().split("\\r?\\n")));
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testCalculateOp2Nums() {
        ASTManagerEngine engine = new ASTManagerEngine();
        int xmlFileTot = engine.countXMLFiles("resources/pythonxmlPA1");
        for (int i = 0; i < xmlFileTot; i++) {
           engine.processXMLParsing("resources/pythonxmlPA1", String.valueOf(i));
        }
        HashMap<String, Integer> op2Num = engine.queryOnNode.calculateOp2Nums.get();
        HashMap<String, Integer> expectedOp2Num = new HashMap<>();

        expectedOp2Num.put("And", 253);
        expectedOp2Num.put("Or", 101);

        expectedOp2Num.put("Add", 1257);
        expectedOp2Num.put("Sub", 862);
        expectedOp2Num.put("Mult", 171);
        expectedOp2Num.put("Div", 18);
        expectedOp2Num.put("Mod", 86);
        expectedOp2Num.put("Pow", 11);
        expectedOp2Num.put("LShift", 16);
        expectedOp2Num.put("RShift", 22);
        expectedOp2Num.put("BitOr", 6);
        expectedOp2Num.put("BitXor", 24);
        expectedOp2Num.put("BitAnd", 43);
        expectedOp2Num.put("FloorDiv", 153);

        expectedOp2Num.put("Invert", 2);
        expectedOp2Num.put("Not", 222);
        expectedOp2Num.put("USub", 265);
        expectedOp2Num.put("Eq", 671);
        expectedOp2Num.put("NotEq", 119);

        expectedOp2Num.put("Lt", 375);
        expectedOp2Num.put("LtE", 156);
        expectedOp2Num.put("Gt", 238);
        expectedOp2Num.put("GtE", 92);
        expectedOp2Num.put("Is", 2);
        expectedOp2Num.put("IsNot", 17);
        expectedOp2Num.put("In", 95);
        expectedOp2Num.put("NotIn", 76);
        assertEquals(expectedOp2Num, op2Num);
    }
    


    @Tag(TestKind.PUBLIC)
    @Test
    void testCalculateNode2Num() {
        ASTManagerEngine engine = new ASTManagerEngine();
        int xmlFileTot = engine.countXMLFiles("resources/pythonxmlPA1");
        for (int i = 0; i < xmlFileTot; i++) {
            engine.processXMLParsing("resources/pythonxmlPA1", String.valueOf(i));
        }
        Map<String, Long> node2Num = engine.queryOnNode.calculateNode2Nums.apply("0");
        Map<String, Long> expectedNode2Num = new HashMap<>();
        expectedNode2Num.put("Module", 1L);
        expectedNode2Num.put("ClassDef", 1L);
        expectedNode2Num.put("FunctionDef", 2L);
        expectedNode2Num.put("arguments", 2L);

        expectedNode2Num.put("arg", 4L);
        expectedNode2Num.put("Name", 29L);
        expectedNode2Num.put("Assign", 7L);
        expectedNode2Num.put("Constant", 1L);

        expectedNode2Num.put("While", 2L);

        expectedNode2Num.put("BoolOp", 1L);
        expectedNode2Num.put("Compare", 2L);
        expectedNode2Num.put("Attribute", 13L);

        expectedNode2Num.put("Tuple", 2L);
        expectedNode2Num.put("Return", 2L);

        expectedNode2Num.put("Subscript", 2L);

        expectedNode2Num.put("Call", 1L);
        expectedNode2Num.put("If", 1L);
        assertEquals(expectedNode2Num, node2Num);
    }
    
    @Tag(TestKind.PUBLIC)
    @Test
    public void testProcessNodeFreq() {
        ASTManagerEngine engine = new ASTManagerEngine();
        int xmlFileTot = engine.countXMLFiles("resources/pythonxmlPA1");
        for (int i = 0; i < xmlFileTot; i++) {
            engine.processXMLParsing("resources/pythonxmlPA1", String.valueOf(i));
        }

        assertEquals(engine.getId2ASTModules().size(), 837);
        List<Map.Entry<String, Integer>> funcName2NodeNum = engine.queryOnNode.processNodeFreq.get();
        assertEquals(funcName2NodeNum.size(), 1126);
        assertEquals(funcName2NodeNum.get(0).getValue(), 221);
        assertEquals(funcName2NodeNum.get(funcName2NodeNum.size() - 1).getValue(), 6);
    }
    
}
