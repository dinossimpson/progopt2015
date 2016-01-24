package petter.cfg.expression;
import petter.cfg.*;
import petter.cfg.edges.*;
/**
 * provides an abstract class to visit an expression;
 * the visitor performs a run through the whole expression, as long as it's visit methods return true;
 * to terminate you have to ensure that the return value of a visit method becomes false at some point;
 * @see ExpressionVisitor
 * @author Michael Petter
 * @author Andrea Flexeder
 */
public class ConstantTransformation extends AbstractExpressionVisitor{
    /**
     * provides a standardized return value for all not overwritten visit methods.
     * the default value here is true, meaning, that a not overwritten visit method just passes on to the next item in the queue
     * @param  e item, which is visited; can be ignored
     * @return a default value for visit methods
     */

    private HashMap<String, HashMap<Variable, IntegerConstant>> constants;
    public ConstantTransformation(HashMap<String, HashMap<Variable, IntegerConstant>> constants){
        this.constants = constants;
    }

    protected boolean defaultBehaviour(Expression e){
	    return true;
    }

    public boolean preVisit(IntegerConstant s){return defaultBehaviour(s);}
    
    public boolean preVisit(Variable s){
        if(constants.get("local").get(s) != null){

        }
        else if(constants.get("global").get(s) != null){
            
        }
        return defaultBehaviour(s);
    }

    public boolean preVisit(MethodCall s){return defaultBehaviour(s);}
    public boolean preVisit(UnknownExpression s){return defaultBehaviour(s);}
    public boolean preVisit(UnaryExpression s){return defaultBehaviour(s);}
    
    public boolean preVisit(BinaryExpression s){
        // Expressiong lex = s.getLeft();
        s.getLeft().accept(this);       
        // Expressiong rex = s.getRight();
        s.getRight().accept(this);
        return defaultBehaviour(s);
    }

    public void postVisit(IntegerConstant s){}
    public void postVisit(Variable s){}
    public void postVisit(MethodCall s){}
    public void postVisit(UnknownExpression s){}
    public void postVisit(UnaryExpression s){}
    public void postVisit(BinaryExpression s){}
}