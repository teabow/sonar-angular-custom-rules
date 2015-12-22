package org.sonar.javascript.angular.checks;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.javascript.angular.util.AngularUtil;
import org.sonar.javascript.tree.visitors.CharsetAwareVisitor;
import org.sonar.plugins.javascript.api.tree.Tree;
import org.sonar.plugins.javascript.api.tree.declaration.InitializedBindingElementTree;
import org.sonar.plugins.javascript.api.tree.expression.*;
import org.sonar.plugins.javascript.api.visitors.BaseTreeVisitor;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by thibaud.bourgeois on 19/12/2015.
 * String constants use check.
 */
@Rule(
    key = "NGR4",
    priority = Priority.INFO,
    name = "String constants should be defined in a provider",
    tags = {"convention"},
    description = "Create a constant provider."
)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("5min")
public class ConstantsUseCheck extends BaseTreeVisitor implements CharsetAwareVisitor {

    Charset charset;

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    private static final String MESSAGE = "String constants should be defined in a provider.";

    @Override
    public void visitInitializedBindingElement(InitializedBindingElementTree tree) {
        if (isForbiddenString(tree.right(), getContext().getFile())) {
            reportIssue(tree);
        }

        super.visitInitializedBindingElement(tree);
    }

    @Override
    public void visitAssignmentExpression(AssignmentExpressionTree tree) {
        reportIssue(tree);
        if (tree.is(Tree.Kind.ASSIGNMENT) && isForbiddenString(tree.expression(), getContext().getFile())) {
            reportIssue(tree);
        }

        super.visitAssignmentExpression(tree);
    }

    @Override
    public void visitBinaryExpression(BinaryExpressionTree tree) {
        if (tree.is(Tree.Kind.PLUS)
                && (isForbiddenString(tree.leftOperand(), getContext().getFile())
                    || isForbiddenString(tree.rightOperand(), getContext().getFile()))) {
            reportIssue(tree);
        }

        super.visitBinaryExpression(tree);
    }

    private boolean isForbiddenString(ExpressionTree expression, File file) {
        return expression.is(Tree.Kind.STRING_LITERAL)
                && (AngularUtil.isController(file, charset)
                || !AngularUtil.isDirective(file, charset)
                || !AngularUtil.isFilter(file, charset));
    }

    private void reportIssue(Tree tree) {
        getContext().addIssue(this, tree, MESSAGE);
    }

}
