package hk.ust.comp3021.stmt;

import hk.ust.comp3021.expr.*;
import hk.ust.comp3021.misc.*;
import hk.ust.comp3021.utils.*;
import java.util.*;

public class FunctionDefStmt extends ASTStmt {
    /*
     * FunctionDef(identifier name, arguments args, stmt* body, expr*
     * decorator_list, expr? returns, ...)
     */
    private String name;
    private ASTArguments args;
    private ArrayList<ASTStmt> body = new ArrayList<>();
    private ArrayList<ASTExpr> decoratorList = new ArrayList<>();
    private ASTExpr returns = null;

    public FunctionDefStmt(XMLNode node) {
        super(node);

        this.stmtType = ASTStmt.StmtType.FunctionDef;

        this.name = node.getAttribute("name");
        this.args = new ASTArguments(node.getChildByIdx(0));
        for (XMLNode bodyNode : node.getChildByIdx(1).getChildren()) {
            body.add(ASTStmt.createASTStmt(bodyNode));
        }
        for (XMLNode listNode : node.getChildByIdx(2).getChildren()) {
            decoratorList.add(ASTExpr.createASTExpr(listNode));
        }
        if (node.getNumChildren() >= 4) {
            this.returns = ASTExpr.createASTExpr(node.getChildByIdx(3));
        }

    }


    public ArrayList<CallExpr> getAllCalledFunc() {
        ArrayList<CallExpr> calledFuncs = new ArrayList<CallExpr>();
        ArrayList<ASTElement> processedChild = new ArrayList<ASTElement>();

        processedChild.add(this);

        while (!processedChild.isEmpty()) {
            ASTElement curChild = processedChild.get(0);
            processedChild.remove(0);
            if (curChild instanceof CallExpr) {
                calledFuncs.add((CallExpr) curChild);
            }
            for (ASTElement child : curChild.getChildren()) {
                processedChild.add(child);
            }
        }

        return calledFuncs;
    }


    public int getParamNum() {
        return args.getParamNum();
    }

    public String getName() {
        return name;
    }

    @Override
    public ArrayList<ASTElement> getChildren() {
        ArrayList<ASTElement> children = new ArrayList<>();
        children.add(args);
        children.addAll(body);
        children.addAll(decoratorList);
        if (returns != null) {
            children.add(returns);
        }
        return children;
    }

}
