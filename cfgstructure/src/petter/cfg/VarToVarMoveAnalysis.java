package petter.cfg;

import petter.cfg.*;
import petter.cfg.edges.Assignment;
import petter.cfg.edges.GuardedTransition;
import petter.cfg.edges.Nop;
import petter.cfg.edges.MethodCall;
import petter.cfg.edges.Transition;
import java.io.*;
import java.util.*;
import petter.cfg.expression.Variable;
import petter.cfg.expression.BinaryExpression;
import petter.cfg.expression.IntegerConstant;


public class VarToVarMoveAnalysis extends AbstractPropagatingVisitor<HashMap<String, HashSet<Variable>>>{

    private CompilationUnit cu;
    // private TransitionFactory tf;
    private HashMap<String, Variable> availableExpr;
    private ArrayList<State> stopIter;

    static HashMap<String, HashSet<Variable>> lub(HashMap<String, HashSet<Variable>> b1, HashMap<String, HashSet<Variable>> b2){
        if (b1 == null)
            return b2;
        if (b2 == null)
            return b1;

        for(String expr : b1.keySet()) {
            b1.get(expr).retainAll(b2.get(expr));
        }
        return b1;
    }

    static boolean lessoreq(HashMap<String, HashSet<Variable>> b1, HashMap<String, HashSet<Variable>> b2){
        System.out.println("lessoreq: "+ b1 + "   " + b2);
        if (b1 == null)
            return true;
        if (b2 == null)
            return false;
        if(b1.size() > b2.size())
            return false;
        else if(b1.size() == b2.size()) {
            for(String key : b1.keySet()) {
                if(b1.get(key).size() > b2.get(key).size())
                    return false;
            }
            return true;
        }
        else
            return true;
        // return ((!b1) || b2);
    }

    public VarToVarMoveAnalysis(CompilationUnit cu){
        super(true); // forward reachability
        this.cu=cu;
        // this.tf = new TransitionFactory();
        this.availableExpr = new HashMap<String, Variable>();
    }

    public HashMap<String, Variable> getAvailableExpr() {
        return this.availableExpr;
    }

    public HashMap<String, HashSet<Variable>> removeVarFromHashSet(HashMap<String, HashSet<Variable>> d, Variable v, String key) {
        for (String e : d.keySet()) {
            if(!e.equals(key)) {
                d.get(e).remove(v);
            }
        }
        return d;
    }

    public HashMap<String, HashSet<Variable>> deepCopy(HashMap<String, HashSet<Variable>> currentState){
        HashMap<String, HashSet<Variable>> newState = new HashMap<String, HashSet<Variable>>();

        for(String expr : currentState.keySet()) {
            HashSet<Variable> vars = new HashSet<Variable>();
            for(Variable v : currentState.get(expr)) {
                vars.add(v);
            }
            newState.put(expr, vars);
        }
        return newState;
    }

    public HashMap<String, HashSet<Variable>> visit(State s, HashMap<String, HashSet<Variable>> newflow) {
        System.out.println(s);
        System.out.println("Current state: "+newflow);
        HashMap<String, HashSet<Variable>> oldflow = dataflowOf(s);
        System.out.println("Old state: "+oldflow);
        if (!lessoreq(newflow, oldflow)) {
            HashMap<String, HashSet<Variable>> newval = lub(oldflow, newflow);
            System.out.println("intersect state: "+newval);


            dataflowOf(s, deepCopy(newval));
            return newval;
        }

        return null;
    }

    public HashMap<String, HashSet<Variable>> visit(Assignment s, HashMap<String, HashSet<Variable>> d) {
        System.out.println("Visiting assignment: "+s.getLhs().toString()+" = "+s.getRhs().toString());
        System.out.println("Current state in Ass: "+d);
        // Unary??
        String rhs = s.getRhs().toString();
        Variable lhs = (Variable) s.getLhs();

        if(s.getRhs() instanceof Variable) {
            Variable v = (Variable) s.getRhs();
            for (String e : d.keySet()) {
                if(d.get(e).contains(v)) {
                    d.get(e).add(lhs);
                }
                else {
                    d.get(e).remove(lhs);
                }
            }
        }
        // else if(s.getRhs() instanceof UnaryExpression) {
        //     String s = s.getRhs().getExpression().toString();
        //probably nothing to do

        // }
        else {
            if(!this.availableExpr.containsKey(rhs)) {
                System.out.println("vazw sto availableExpr : "+ rhs +"   " +lhs);
                this.availableExpr.put(rhs, lhs);
                System.out.println("Meta: "+ this.availableExpr);
            }
            if(s.getRhs() instanceof IntegerConstant || s.getRhs() instanceof BinaryExpression) {
                if(d.containsKey(rhs)) {
                    d.get(rhs).add(lhs);
                    removeVarFromHashSet(d, lhs, rhs);
                }
                else {
                    HashSet<Variable> vars = new HashSet<Variable>();
                    vars.add(lhs);
                    d.put(rhs, vars);
                    removeVarFromHashSet(d, lhs, rhs);
                }
            }
        }
        System.out.println("After assignment: "+d);
        return d;
    }

    public HashMap<String, HashSet<Variable>> visit(GuardedTransition s, HashMap<String, HashSet<Variable>> d) {
        // Expression e = s.getAssertion(); or do nothing
        System.out.println("Guard " + s.toString());
        System.out.println("Guard contain expr: "+d.containsKey(s.getAssertion().toString()));
        d.remove(s.getAssertion().toString()); // den 8a bei pote!
        return d;
    }
    public HashMap<String, HashSet<Variable>> visit(Procedure s, HashMap<String, HashSet<Variable>> d) {
        if(d == null) {
            d = new HashMap<String, HashSet<Variable>>();
        }
        return d;
    }

    public HashMap<String, HashSet<Variable>> visit(Nop s, HashMap<String, HashSet<Variable>> d) {
        return d;
    }

    public HashMap<String, HashSet<Variable>> visit(MethodCall s, HashMap<String, HashSet<Variable>> d) {
        return d;
    }

}
