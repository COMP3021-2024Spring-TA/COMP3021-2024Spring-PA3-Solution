package hk.ust.comp3021.expr;

import hk.ust.comp3021.misc.*;
import hk.ust.comp3021.utils.*;
import java.util.*;

public class UnaryOpExpr extends ASTExpr {
    // UnaryOp(unaryop op, expr operand)
    private ASTEnumOp op;
    private ASTExpr operand;

    public UnaryOpExpr(XMLNode node) {
        super(node);
        this.exprType = ASTExpr.ExprType.UnaryOp;
        this.op = new ASTEnumOp(node.getChildByIdx(0));
        this.operand = ASTExpr.createASTExpr(node.getChildByIdx(1));
    }

    public ASTEnumOp getOp() {
        return op;
    }

    @Override
    public ArrayList<ASTElement> getChildren() {
        ArrayList<ASTElement> children = new ArrayList<>();
        children.add(operand);
        return children;
    }

}
