package petter.cfg;

import petter.cfg.*;
import petter.cfg.edges.Assignment;
import petter.cfg.edges.GuardedTransition;
import petter.cfg.edges.Nop;
import petter.cfg.edges.MethodCall;
import petter.cfg.edges.Transition;
import java.io.*;
import java.util.*;
import petter.cfg.expression.Operator;
import petter.cfg.expression.Variable;
import petter.cfg.expression.BinaryExpression;
import petter.cfg.expression.IntegerConstant;
import petter.cfg.expression.UnknownExpression;
import petter.cfg.expression.UnaryExpression;
import petter.cfg.expression.Expression;
import petter.cfg.expression.FindVarsVisitor;

public class IntraTrulyLivenessAnalysis extends AbstractPropagatingVisitor<HashSet<Variable>> {

    private boolean fixpointCheck;
    private List<State> visitedStates;

    public IntraTrulyLivenessAnalysis() {
        super(false); // backward analysis
        this.visitedStates = new ArrayList<State>();
    }

    // LUB is the union
    static HashSet<Variable> lub(HashSet<Variable> b1, HashSet<Variable> b2){
        if (b1 == null)
            return b2;
        if (b2 == null)
            return b1;

        b1.addAll(b2);
        return b1;
    }

    static boolean notequal(HashSet<Variable> b1, HashSet<Variable> b2){
        if (b1 == null || b2 == null)
            return true;
        boolean res = !b1.equals(b2);
        return res;
    }

    public boolean getFixpointCheck() {
        return this.fixpointCheck;
    }

    public HashSet<Variable> deepCopy(HashSet<Variable> currentState){
        HashSet<Variable> newState = new HashSet<Variable>();

        if(currentState == null) {
            return newState;
        }
        for(Variable var : currentState){
            newState.add(var);
        }
        return newState;
    }

    public void setDataFlow(State s, HashSet<Variable> d) {
        HashSet<Variable> oldflow = dataflowOf(s);
        HashSet<Variable> newflow;

        if(oldflow != null) {
            newflow = lub(dataflowOf(s), d);
        }
        else {
            newflow = d;
        }
        dataflowOf(s, newflow);

        if(notequal(newflow, oldflow)) {
            this.fixpointCheck = false;
        }
    }

    public HashSet<Variable> visit(State s, HashSet<Variable> d) {
        if(visitedStates.contains(s)) {
            return null;
        }
        visitedStates.add(s);
        return d;
    }

    public HashSet<Variable> visit(Assignment s, HashSet<Variable> d) {
        d = deepCopy(dataflowOf(s.getDest()));
        if(s.getLhs() instanceof Variable && !s.getLhs().toString().startsWith("$")) {
            Variable v = (Variable) s.getLhs();
            if(d.contains(v) || v.toString().equals("return")){
                d.remove(v);
                FindVarsVisitor usedV = new FindVarsVisitor();
                s.getRhs().accept(usedV);
                d.addAll(usedV.getUsedVars());
            }
        }
        else if(s.getLhs() instanceof BinaryExpression) {
            BinaryExpression bExpr = (BinaryExpression) s.getLhs();
            if(bExpr.hasArrayAccess()) {
                // Expression visitor to find variables used in an expression
                FindVarsVisitor usedV2 = new FindVarsVisitor();
                bExpr.getRight().accept(usedV2);
                d.addAll(usedV2.getUsedVars());
            }

            FindVarsVisitor usedV = new FindVarsVisitor();
            s.getRhs().accept(usedV);
            d.addAll(usedV.getUsedVars());
        }
        setDataFlow(s.getSource(), d);
        return d;
    }

    public HashSet<Variable> visit(GuardedTransition s, HashSet<Variable> d) {

        d = deepCopy(dataflowOf(s.getDest()));

        FindVarsVisitor usedV = new FindVarsVisitor();
        s.getAssertion().accept(usedV);
        d.addAll(usedV.getUsedVars());
        setDataFlow(s.getSource(), d);

        return d;
    }

    public HashSet<Variable> visit(Procedure s, HashSet<Variable> d) {
        if(d == null) {
            d = new HashSet<Variable>();
            setDataFlow(s.getEnd(), d);
        }
        this.fixpointCheck = true;
        this.visitedStates.clear();
        return d;
    }

    public HashSet<Variable> visit(Nop s, HashSet<Variable> d) {
        setDataFlow(s.getSource(), deepCopy(dataflowOf(s.getDest())));
        return d;
    }

    public HashSet<Variable> visit(MethodCall s, HashSet<Variable> d) {
        return d;
    }

}
