package hk.ust.comp3021.utils;

import hk.ust.comp3021.expr.BinOpExpr;
import hk.ust.comp3021.expr.BoolOpExpr;
import hk.ust.comp3021.expr.CompareExpr;
import hk.ust.comp3021.expr.UnaryOpExpr;
import hk.ust.comp3021.misc.*;
import hk.ust.comp3021.stmt.*;

import java.util.*;


public class ASTModule extends ASTElement {
    // Module(stmt* body, ...)
    private ArrayList<ASTStmt> body;
    private String astID;

    public ASTModule(XMLNode node, String astID) {
        this.astID = astID;

        this.body = new ArrayList<>();
        for (XMLNode bodyNode : node.getChildByIdx(0).getChildren()) {
            this.body.add(ASTStmt.createASTStmt(bodyNode));
        }
    }

    public ArrayList<ASTStmt> getBody() {
        return body;
    }

    public void setAstID(String astID) {
        this.astID = astID;
    }

    public void setBody(ArrayList<ASTStmt> body) {
        this.body = body;
    }
    

    @Override
    public ArrayList<ASTElement> getChildren() {
        ArrayList<ASTElement> children = new ArrayList<>();
        children.addAll(body);
        return children;
    }
    

    public String getASTID() {
        return astID;
    }

    @Override
    public String getNodeType() {
        return "Module";
    }
}
