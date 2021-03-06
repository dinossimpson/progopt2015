
package petter.cfg.expression;

import petter.cfg.*;
import petter.cfg.edges.*;
import java.util.*;
/**
 * provides an abstract class to visit an expression;
 * the visitor performs a run through the whole expression, as long as it's visit methods return true;
 * to terminate you have to ensure that the return value of a visit method becomes false at some point;
 * @see ExpressionVisitor
 * @author Michael Petter
 * @author Andrea Flexeder
 */
public class VarSubstituteVisitor extends AbstractExpressionVisitor {

    private VarToVarMoveAnalysis varTovarMap;
    private State source;
     private State dest;
    private BinaryExpression be;
    private Assignment transition;

    public VarSubstituteVisitor(VarToVarMoveAnalysis varTovarMap, State source, State dest, Assignment tr) {
        this.source = source;
        this.dest = dest;
        this.varTovarMap = varTovarMap;
        this.be = null;
        this.transition = tr;
    }

    public boolean preVisit(IntegerConstant s) {
        return true;
    }

    public boolean preVisit(Variable s) {
        HashMap<Expression, ArrayList<Variable>> flowOfSource = this.varTovarMap.dataflowOf(this.source);
        if(this.be == null) {  // just a Var - not member of Bin Expr
            for(Expression key : flowOfSource.keySet()) {
                if(flowOfSource.get(key).contains(s) && flowOfSource.get(key).size() > 1) {
                    this.transition.removeEdge();
                    Assignment newEdge = new Assignment(this.source, this.dest, this.transition.getLhs(), flowOfSource.get(key).get(0));
                    this.source.addOutEdge(newEdge);
                    this.source.getMethod().resetTransitions();
                }
            }
        }
        else {  // Var in a Bin Expr
            for(Expression key : flowOfSource.keySet()) {
                if(flowOfSource.get(key).contains(s) && flowOfSource.get(key).size() > 1) {
                    this.be.substitute(s, flowOfSource.get(key).get(0));
                }
            }
        }
        return true;
    }

    public boolean preVisit(UnaryExpression s) {
        if(!s.hasArrayAccess()) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean preVisit(BinaryExpression s) {
        this.be = s;
        return true;
    }

}
