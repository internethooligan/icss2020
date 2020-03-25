package nl.han.ica.icss.parser;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

import java.util.Stack;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

    //Accumulator attributes:
    private AST ast;

    //Use this to keep track of the parent nodes when recursively traversing the ast
    private Stack<ASTNode> currentContainer;

    public ASTListener() {
        ast = new AST();
        currentContainer = new Stack<>();
    }

    public AST getAST() {
        return ast;
    }

    // OVERRIDING
    @Override
    public void enterStylesheet(final ICSSParser.StylesheetContext ctx) {
        Stylesheet stylesheet = new Stylesheet();
        ast.setRoot(stylesheet);
        currentContainer.push(stylesheet);
    }

    @Override
    public void enterStyleRule(final ICSSParser.StyleRuleContext ctx) {
        Stylerule stylerule = new Stylerule();
        currentContainer.peek().addChild(stylerule);
        currentContainer.push(stylerule);
    }

    @Override
    public void exitStyleRule(final ICSSParser.StyleRuleContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterVariableAssignment(final ICSSParser.VariableAssignmentContext ctx) {
        VariableAssignment variableAssignment = new VariableAssignment();
        currentContainer.peek().addChild(variableAssignment);
        currentContainer.push(variableAssignment);
    }

    @Override
    public void exitVariableAssignment(final ICSSParser.VariableAssignmentContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterVariableReference(final ICSSParser.VariableReferenceContext ctx) {
        VariableReference variableReference = new VariableReference(ctx.getText());
        currentContainer.peek().addChild(variableReference);
        currentContainer.push(variableReference);
    }

    @Override
    public void exitVariableReference(final ICSSParser.VariableReferenceContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterPixelLiteral(final ICSSParser.PixelLiteralContext ctx) {
        PixelLiteral pixelLiteral = new PixelLiteral(ctx.getText());
        currentContainer.peek().addChild(pixelLiteral);
        currentContainer.push(pixelLiteral);
    }

    @Override
    public void exitPixelLiteral(final ICSSParser.PixelLiteralContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterColorLiteral(final ICSSParser.ColorLiteralContext ctx) {
        ColorLiteral colorLiteral = new ColorLiteral(ctx.getText());
        currentContainer.peek().addChild(colorLiteral);
        currentContainer.push(colorLiteral);
    }

    @Override
    public void exitColorLiteral(final ICSSParser.ColorLiteralContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterBoolLiteral(final ICSSParser.BoolLiteralContext ctx) {
        BoolLiteral boolLiteral = new BoolLiteral(ctx.getText());
        currentContainer.peek().addChild(boolLiteral);
        currentContainer.push(boolLiteral);
    }

    @Override
    public void exitBoolLiteral(final ICSSParser.BoolLiteralContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterPercentageLiteral(final ICSSParser.PercentageLiteralContext ctx) {
        PercentageLiteral percentageLiteral = new PercentageLiteral(ctx.getText());
        currentContainer.peek().addChild(percentageLiteral);
        currentContainer.push(percentageLiteral);
    }

    @Override
    public void exitPercentageLiteral(final ICSSParser.PercentageLiteralContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterScalarLiteral(final ICSSParser.ScalarLiteralContext ctx) {
        ScalarLiteral scalarLiteral = new ScalarLiteral(ctx.getText());
        currentContainer.peek().addChild(scalarLiteral);
        currentContainer.push(scalarLiteral);
    }

    @Override
    public void exitScalarLiteral(final ICSSParser.ScalarLiteralContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterMultiplyOperation(final ICSSParser.MultiplyOperationContext ctx) {
        MultiplyOperation multiplyOperation = new MultiplyOperation();
        currentContainer.peek().addChild(multiplyOperation);
        currentContainer.push(multiplyOperation);
    }

    @Override
    public void exitMultiplyOperation(final ICSSParser.MultiplyOperationContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterAddOperation(final ICSSParser.AddOperationContext ctx) {
        AddOperation addOperation = new AddOperation();
        currentContainer.peek().addChild(addOperation);
        currentContainer.push(addOperation);
    }

    @Override
    public void exitAddOperation(final ICSSParser.AddOperationContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterSubtractOperation(final ICSSParser.SubtractOperationContext ctx) {
        SubtractOperation subtractOperation = new SubtractOperation();
        currentContainer.peek().addChild(subtractOperation);
        currentContainer.push(subtractOperation);
    }

    @Override
    public void exitSubtractOperation(final ICSSParser.SubtractOperationContext ctx) {
        currentContainer.pop();
    }


    @Override
    public void enterDeclaration(final ICSSParser.DeclarationContext ctx) {
        Declaration declaration = new Declaration();
        currentContainer.peek().addChild(declaration);
        currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(final ICSSParser.DeclarationContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterPropertyName(final ICSSParser.PropertyNameContext ctx) {
        PropertyName propertyName = new PropertyName(ctx.getText());
        currentContainer.peek().addChild(propertyName);
        currentContainer.push(propertyName);
    }

    @Override
    public void exitPropertyName(final ICSSParser.PropertyNameContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterTagSelector(final ICSSParser.TagSelectorContext ctx) {
        TagSelector tagSelector = new TagSelector(ctx.getText());
        currentContainer.peek().addChild(tagSelector);
        currentContainer.push(tagSelector);
    }

    @Override
    public void exitTagSelector(final ICSSParser.TagSelectorContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterIdSelector(final ICSSParser.IdSelectorContext ctx) {
        IdSelector idSelector = new IdSelector(ctx.getText());
        currentContainer.peek().addChild(idSelector);
        currentContainer.push(idSelector);
    }

    @Override
    public void exitIdSelector(final ICSSParser.IdSelectorContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterClassSelector(final ICSSParser.ClassSelectorContext ctx) {
        ClassSelector classSelector = new ClassSelector(ctx.getText());
        currentContainer.peek().addChild(classSelector);
        currentContainer.push(classSelector);
    }

    @Override
    public void exitClassSelector(final ICSSParser.ClassSelectorContext ctx) {
        currentContainer.pop();
    }
}
