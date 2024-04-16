package hk.ust.comp3021.misc;

import hk.ust.comp3021.expr.*;
import hk.ust.comp3021.utils.*;
import java.util.*;

public class ASTArguments extends ASTElement {
    public class ASTArg extends ASTElement {
        /*
         * arg = (identifier arg, expr? annotation, ...)
         *       attributes (int lineno, int colOffset, int? endLineno, int? endColOffset)
         */
        private String arg;
        private ASTExpr annotation;

        public ASTArg(XMLNode node) {
            super(node);
            this.arg = node.getAttribute("arg");
            if (!node.hasAttribute("annotation")) {
                this.annotation = ASTExpr.createASTExpr(node.getChildByIdx(0));
            }
        }

        public ASTExpr getAnnotation() {
            return annotation;
        }

        public String getArg() {
            return arg;
        }

        @Override
        public ArrayList<ASTElement> getChildren() {
            ArrayList<ASTElement> children = new ArrayList<>();
            if (annotation != null) {
                children.add(annotation);
            }
            return children;
        }
        
        
        @Override
        public String getNodeType() {
            return "arg";
        }
    }
    /*
     * arguments = (.., arg* args, ..., expr* defaults)
     */

    private ArrayList<ASTArg> args = new ArrayList<>();
    private ArrayList<ASTExpr> defaults = new ArrayList<>();

    public ASTArguments(XMLNode node) {
        super(node);
        for (XMLNode argNode : node.getChildByIdx(1).getChildren()) {
            this.args.add(new ASTArg(argNode));
        }
        int nextIdx = 2;
        if (!node.hasAttribute("vararg")) {
            nextIdx += 1;
        }
        nextIdx += 2;
        if (!node.hasAttribute("kwarg")) {

            nextIdx += 1;
        }
        for (XMLNode defNode : node.getChildByIdx(nextIdx).getChildren()) {
            this.defaults.add(ASTExpr.createASTExpr(defNode));
        }

    }
    
    /*
    * Return the number of ASTArg child nodes
    */
    public int getParamNum() {
        int paramNum = 0;
        paramNum += this.args.size();
        return paramNum;
    }

    @Override
    public ArrayList<ASTElement> getChildren() {
        ArrayList<ASTElement> children = new ArrayList<>();
        children.addAll(args);
        children.addAll(defaults);

        return children;
    }
    

    @Override
    public String getNodeType() {
        return "arguments";
    }

}
