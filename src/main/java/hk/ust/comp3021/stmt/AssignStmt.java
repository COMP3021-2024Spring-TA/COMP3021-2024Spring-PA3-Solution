package hk.ust.comp3021.stmt;

import hk.ust.comp3021.expr.*;
import hk.ust.comp3021.misc.*;
import hk.ust.comp3021.utils.*;
import java.util.*;

public class AssignStmt extends ASTStmt {
    // Assign(expr* targets, expr value, ...)
    private ArrayList<ASTExpr> targets = new ArrayList<>();
    private ASTExpr value;

    public AssignStmt(XMLNode node) {
        super(node);
        this.stmtType = ASTStmt.StmtType.Assign;
        for (XMLNode targetNode : node.getChildByIdx(0).getChildren()) {
            this.targets.add(ASTExpr.createASTExpr(targetNode));
        }
        this.value = ASTExpr.createASTExpr(node.getChildByIdx(1));
    }

    @Override
    public ArrayList<ASTElement> getChildren() {
        ArrayList<ASTElement> children = new ArrayList<>();
        children.addAll(targets);
        children.add(value);
        return children;
    }
    
}

