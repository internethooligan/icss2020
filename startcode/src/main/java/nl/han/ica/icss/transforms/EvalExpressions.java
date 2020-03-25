package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class EvalExpressions implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public EvalExpressions() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(final AST ast) {
        variableValues = new LinkedList<>();
        variableValues.add(new HashMap<>());

        traverseEvaluation(ast.root);
    }

    /**
     * Traverse through all nodes in tree
     *
     * @param node
     */
    private void traverseEvaluation(final ASTNode node) {
        if (node instanceof VariableAssignment) {
            addVariable((VariableAssignment) node);
        }
        if (node instanceof Declaration) {
            transformExpression(node);
        }

        if (node.getChildren().size() > 0) {
            for (final ASTNode child : node.getChildren()) {
                traverseEvaluation(child);
            }
        }
    }

    /**
     * Add variable to variableValues list
     *
     * @param node
     */
    private void addVariable(final VariableAssignment node) {
        if (node.expression instanceof ColorLiteral) {
            variableValues.get(0).put(node.name.name, (ColorLiteral) node.expression);
        } else if (node.expression instanceof BoolLiteral) {
            variableValues.get(0).put(node.name.name, (BoolLiteral) node.expression);
        } else if (node.expression instanceof PixelLiteral) {
            variableValues.get(0).put(node.name.name, (PixelLiteral) node.expression);
        } else if (node.expression instanceof PercentageLiteral) {
            variableValues.get(0).put(node.name.name, (PercentageLiteral) node.expression);
        } else if (node.expression instanceof ScalarLiteral) {
            variableValues.get(0).put(node.name.name, (ScalarLiteral) node.expression);
        } else {
            transformExpression(node.expression);
        }
    }

    private void transformExpression(final ASTNode expression) {
        for (final ASTNode child : expression.getChildren()) {
            if (child instanceof VariableReference) {
                expression.removeChild(child);
                expression.addChild(transformVariableReference((VariableReference) child));
            }
            if (child instanceof Operation) {
                expression.removeChild(child);
                expression.addChild(transformOperation((Operation) child));
            }
        }
    }

    private Literal transformOperation(final Operation operation) {
        int o = 0;
        if (!(operation.lhs instanceof Literal)) {
            operation.removeChild(operation.lhs);
        }
        if (operation.lhs instanceof VariableReference) {
            operation.lhs = transformVariableReference((VariableReference) operation.lhs);
        } else if (operation.lhs instanceof Operation) {
            operation.lhs = transformOperation((Operation) operation.lhs);
        }

        if (!(operation.rhs instanceof Literal)) {
            operation.removeChild(operation.rhs);
        }
        if (operation.rhs instanceof VariableReference) {
            operation.rhs = transformVariableReference((VariableReference) operation.rhs);
        } else if (operation.rhs instanceof Operation) {
            operation.rhs = transformOperation((Operation) operation.rhs);
        }

        return executeOperation(operation);
    }

    private Literal executeOperation(final Operation operation) {
        if (operation instanceof MultiplyOperation) {
            return multiply(operation);
        }
        if (operation instanceof SubtractOperation) {
            return subtract(operation);
        }
        if (operation instanceof AddOperation) {
            return add(operation);
        }

        return null;
    }

    private Literal multiply(final Operation operation) {
        if (operation.lhs instanceof ScalarLiteral && operation.rhs instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) operation.lhs).value * ((ScalarLiteral) operation.rhs).value);
        } else if (operation.rhs instanceof ScalarLiteral) {
            if (operation.lhs instanceof PercentageLiteral) {
                return new PercentageLiteral(((ScalarLiteral) operation.rhs).value * ((PercentageLiteral) operation.lhs).value);
            }
            if (operation.lhs instanceof PixelLiteral) {
                return new PixelLiteral(((ScalarLiteral) operation.rhs).value * ((PixelLiteral) operation.lhs).value);
            }
        } else if (operation.lhs instanceof ScalarLiteral) {
            if (operation.rhs instanceof PercentageLiteral) {
                return new PercentageLiteral(((ScalarLiteral) operation.lhs).value * ((PercentageLiteral) operation.rhs).value);
            }
            if (operation.rhs instanceof PixelLiteral) {
                return new PixelLiteral(((ScalarLiteral) operation.lhs).value * ((PixelLiteral) operation.rhs).value);
            }
        }
        return null;
    }

    private Literal subtract(final Operation operation) {
        if (operation.lhs instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) operation.lhs).value - ((ScalarLiteral) operation.rhs).value);
        } else if (operation.lhs instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) operation.lhs).value - ((PixelLiteral) operation.rhs).value);
        } else if (operation.lhs instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) operation.lhs).value - ((PercentageLiteral) operation.rhs).value);
        }
        return null;
    }

    private Literal add(final Operation operation) {
        if (operation.lhs instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) operation.lhs).value + ((ScalarLiteral) operation.rhs).value);
        } else if (operation.lhs instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) operation.lhs).value + ((PixelLiteral) operation.rhs).value);
        } else if (operation.lhs instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) operation.lhs).value + ((PercentageLiteral) operation.rhs).value);
        }
        return null;
    }

    private Literal transformVariableReference(final VariableReference child) {
        if (variableValues.get(0).get(child.name) != null) {
            return variableValues.get(0).get(child.name);
        }
        return null;
    }
}
