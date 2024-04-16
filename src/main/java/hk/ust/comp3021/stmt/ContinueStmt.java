package hk.ust.comp3021.stmt;

import hk.ust.comp3021.misc.*;
import hk.ust.comp3021.utils.*;
import java.util.*;

public class ContinueStmt extends ASTStmt {
    public ContinueStmt(XMLNode node) {
        super(node);
        this.stmtType = ASTStmt.StmtType.Continue;
    }

    @Override
    public ArrayList<ASTElement> getChildren() {
        ArrayList<ASTElement> children = new ArrayList<>();
        return children;
    }

}
