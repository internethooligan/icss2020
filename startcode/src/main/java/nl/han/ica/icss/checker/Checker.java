package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;

public class Checker {

    public static final int GLOBAL_SCOPE = 0;
    public static final String A_COLOR_STYLE_SHOULD_CONTAIN_A_COLOR_LITERAL = "A color style should contain a color literal..";
    public static final String A_WIDTH_NODE_SHOULD_CONTAIN_A_PIXEL_OF_PERCENTAGE_LITERAL = "A width node should contain a pixel of percentage literal..";
    public static final String YOU_CAN_T_ADD_SUBTRACT_VALUES_WHICH_ARE_NOT_OF_THE_SAME_TYPE = "You can't add/subtract values which are not of the same type..";
    public static final String YOU_CAN_T_MULTIPLY_VALUES_IF_AT_LEAST_ONE_OF_THEM_ISN_T_SCALAR = "You can't multiply values if at least one of them isn't scalar..";
    public static final String VARIABLE_IS_NOT_AVAILABLE_FOR_CURRENT_SCOPE_OR_IS_NOT_DEFINED_AT_ALL = "Variable is not available for current scope or is not defined at all..";
    public static final String COLOR = "color";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String EXPRESSION_IS_NOT_A_BOOLEAN = "Expression is not a boolean..";

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;
    private int currentScope = 0;

    public void check(final AST ast) {
        variableTypes = new LinkedList<>();
        variableTypes.add(new HashMap<>());

        traverseCheck(ast.root);
    }

    private void traverseCheck(final ASTNode node) {
        if (node instanceof Stylerule) {
            currentScope++;
            variableTypes.add(new HashMap<>());
        }
        if (node instanceof VariableAssignment) {
            addVariable((VariableAssignment) node);
        }

        if (node instanceof VariableReference) {
            checkScope((VariableReference) node);
        }
        if (node instanceof Expression) {
            checkOperator((Expression) node);
        }
        if (node instanceof Declaration) {
            checkDeclaration((Declaration) node);
        }
        if (node instanceof IfClause) {
            checkIfClause((IfClause) node);
        }

        if (node.getChildren().size() > 0) {
            for (final ASTNode child : node.getChildren()) {
                traverseCheck(child);
            }
        }
    }

    private void checkIfClause(final IfClause node) {
        final Expression expression = node.getConditionalExpression();

        if (!(expression instanceof BoolLiteral) && (expression instanceof Literal)) {
            node.setError(EXPRESSION_IS_NOT_A_BOOLEAN);
        } else if (expression instanceof VariableReference) {
            if (contains((VariableReference) expression, ExpressionType.BOOL)) {
                node.setError(EXPRESSION_IS_NOT_A_BOOLEAN);
            }
        }
    }

    private void checkDeclaration(final Declaration node) {
        if (node.property.name.contains(COLOR)) {
            if (!(node.expression instanceof ColorLiteral) && !(node.expression instanceof VariableReference)) {
                node.setError(A_COLOR_STYLE_SHOULD_CONTAIN_A_COLOR_LITERAL);
            } else {
                if (node.expression instanceof VariableReference && !contains((VariableReference) node.expression, ExpressionType.COLOR)) {
                    node.setError(A_COLOR_STYLE_SHOULD_CONTAIN_A_COLOR_LITERAL);
                }
            }
        }
        if (node.property.name.contains(WIDTH) || node.property.name.contains(HEIGHT)) {
            if (node.expression instanceof VariableReference && (contains((VariableReference) node.expression, ExpressionType.SCALAR) || contains((VariableReference) node.expression, ExpressionType.COLOR) || contains((VariableReference) node.expression, ExpressionType.BOOL))) {
                node.setError(A_WIDTH_NODE_SHOULD_CONTAIN_A_PIXEL_OF_PERCENTAGE_LITERAL);
            }
        }
    }

    private void checkOperator(final Expression node) {
        if (node instanceof Operation) {
            final Literal left = getLiteral(((Operation) node).lhs);
            final Literal right = getLiteral(((Operation) node).rhs);

            if (left != null && right != null) {
                if (node instanceof AddOperation || node instanceof SubtractOperation) {
                    if (!((left instanceof PixelLiteral && right instanceof PixelLiteral) ||
                            (left instanceof PercentageLiteral && right instanceof PercentageLiteral) ||
                            (left instanceof ScalarLiteral && right instanceof ScalarLiteral))) {
                        node.setError(YOU_CAN_T_ADD_SUBTRACT_VALUES_WHICH_ARE_NOT_OF_THE_SAME_TYPE);
                    }
                }
                if (node instanceof MultiplyOperation) {
                    if (!((left instanceof ScalarLiteral) || (right instanceof ScalarLiteral))) {
                        node.setError(YOU_CAN_T_MULTIPLY_VALUES_IF_AT_LEAST_ONE_OF_THEM_ISN_T_SCALAR);
                    }
                }
            }
        }
    }

    private Literal getLiteral(final Expression node) {
        if (node instanceof Literal) {
            return (Literal) node;
        }

        return null;
    }

    private boolean contains(final VariableReference node, final ExpressionType type) {
        for (final HashMap variableType : variableTypes) {
            if (variableType.get(node.name) == type) {
                return true;
            }
        }

        return false;
    }

    private void addVariable(final VariableAssignment node) {
        if (node.expression instanceof ColorLiteral) {
            variableTypes.get(currentScope).put(node.name.name, ExpressionType.COLOR);
        } else if (node.expression instanceof BoolLiteral) {
            variableTypes.get(currentScope).put(node.name.name, ExpressionType.BOOL);
        } else if (node.expression instanceof PixelLiteral) {
            variableTypes.get(currentScope).put(node.name.name, ExpressionType.PIXEL);
        } else if (node.expression instanceof PercentageLiteral) {
            variableTypes.get(currentScope).put(node.name.name, ExpressionType.PERCENTAGE);
        } else if (node.expression instanceof ScalarLiteral) {
            variableTypes.get(currentScope).put(node.name.name, ExpressionType.SCALAR);
        } else {
            variableTypes.get(currentScope).put(node.name.name, ExpressionType.UNDEFINED);
        }
    }

    private void checkScope(final VariableReference node) {
        if (!variableTypes.get(currentScope).containsKey(node.name) && !variableTypes.get(GLOBAL_SCOPE).containsKey(node.name)) {
            node.setError(VARIABLE_IS_NOT_AVAILABLE_FOR_CURRENT_SCOPE_OR_IS_NOT_DEFINED_AT_ALL);
        }
    }
}
