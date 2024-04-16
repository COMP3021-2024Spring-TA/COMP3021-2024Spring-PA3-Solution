package hk.ust.comp3021.stmt;

import hk.ust.comp3021.misc.*;
import hk.ust.comp3021.utils.*;
import java.util.*;

public class BreakStmt extends ASTStmt {
    public BreakStmt(XMLNode node) {
        super(node);
        this.stmtType = ASTStmt.StmtType.Break;
    }
    @Override
    public ArrayList<ASTElement> getChildren() {
        ArrayList<ASTElement> children = new ArrayList<>();
        return children;
    }
}
