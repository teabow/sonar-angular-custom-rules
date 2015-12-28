package org.sonar.javascript.angular.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.javascript.api.tree.Tree;
import org.sonar.plugins.javascript.api.tree.expression.CallExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.DotMemberExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.ExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.IdentifierTree;
import org.sonar.plugins.javascript.api.visitors.SubscriptionBaseTreeVisitor;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import java.util.List;

/**
 * Created by thibaud.bourgeois on 28/12/2015.
 * Wrapper services use check.
 */
@Rule(
        key = "NGR9",
        priority = Priority.MAJOR,
        name = "Wrapper services should be used.",
        tags = {"convention"},
        description = "Wrapper services should be used. Use $window, $document, $timeout, $interval instead."
)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("15min")
public class WrapperServicesCheck extends SubscriptionBaseTreeVisitor {

    private static final String MESSAGE = "Wrapper services should be used. Use $window, $document, $timeout, $interval instead.";

    private static final List<String> INVALID_CALLS = ImmutableList.of(
            "setTimeout",
            "setInterval"
    );

    private static final List<String> INVALID_IDENTIFIERS = ImmutableList.of(
            "document",
            "window"
    );

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return ImmutableList.of(
                Tree.Kind.CALL_EXPRESSION,
                Tree.Kind.IDENTIFIER_NAME,
                Tree.Kind.IDENTIFIER_REFERENCE
        );
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree instanceof CallExpressionTree) {
            CallExpressionTree callExpression = (CallExpressionTree) tree;
            if (callExpression.callee().is(Tree.Kind.DOT_MEMBER_EXPRESSION)
                    && (isCalleeInvalid((DotMemberExpressionTree) callExpression.callee()))) {
                addIssue(tree, MESSAGE);
            }
            else {
                if (isCalleeInvalid(callExpression.callee())) {
                    addIssue(tree, MESSAGE);
                }
            }
        }
        else {
            if (isIdentifierInvalid((IdentifierTree) tree)) {
                addIssue(tree, MESSAGE);
            }
        }
    }

    private static boolean isIdentifierInvalid(IdentifierTree identifierTree) {
        return INVALID_IDENTIFIERS.contains(identifierTree.name());
    }

    private static boolean isCalleeInvalid(ExpressionTree callee) {
        return callee instanceof IdentifierTree
                && INVALID_CALLS.contains(((IdentifierTree) callee).name());
    }

    private static boolean isCalleeInvalid(DotMemberExpressionTree tree) {
        return tree.object().is(Tree.Kind.IDENTIFIER_REFERENCE)
                && INVALID_CALLS.contains(((IdentifierTree) tree.object()).name())
                || INVALID_CALLS.contains(tree.property().name());
    }

}
