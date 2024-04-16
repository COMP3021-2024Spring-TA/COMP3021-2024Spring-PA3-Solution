package hk.ust.comp3021.expr;

import hk.ust.comp3021.misc.*;
import hk.ust.comp3021.utils.*;
import java.util.ArrayList;

public class ConstantExpr extends ASTExpr {
    // Constant(constant value, string? kind)
    private String value;
    private String kind;

    public ConstantExpr(XMLNode node) {
        super(node);
        this.exprType = ASTExpr.ExprType.Constant;
        this.value = node.getAttribute("value");

        if (node.hasAttribute("kind")) {
            this.kind = node.getAttribute("kind");
        }
    }
    
    @Override
    public ArrayList<ASTElement> getChildren() {
        ArrayList<ASTElement> children = new ArrayList<>();
        return children;
    }

}
