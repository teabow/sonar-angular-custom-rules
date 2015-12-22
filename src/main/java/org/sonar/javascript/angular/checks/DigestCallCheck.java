package org.sonar.javascript.angular.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.javascript.angular.util.AngularUtil;
import org.sonar.plugins.javascript.api.tree.Tree;
import org.sonar.plugins.javascript.api.tree.expression.CallExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.DotMemberExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.IdentifierTree;
import org.sonar.plugins.javascript.api.visitors.SubscriptionBaseTreeVisitor;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import java.util.List;

/**
 * Created by thibaud.bourgeois on 22/12/2015.
 * Digest call check.
 */
@Rule(
        key = "NGR5",
        priority = Priority.BLOCKER,
        name = "$digest function should not be called explicitly.",
        tags = {"convention"},
        description = "$digest is an internal Angular function, use Angular wrapper services or call $apply."
)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("30min")
public class DigestCallCheck extends SubscriptionBaseTreeVisitor {

    private static final String MESSAGE = "$digest function should not be called explicitly, use Angular wrapper services or call $apply.";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return ImmutableList.of(Tree.Kind.CALL_EXPRESSION);
    }

    @Override
    public void visitNode(Tree tree) {
        CallExpressionTree callExpression = (CallExpressionTree) tree;

        if (callExpression.callee().is(Tree.Kind.DOT_MEMBER_EXPRESSION)) {
            DotMemberExpressionTree callee = (DotMemberExpressionTree) callExpression.callee();

            if (isCalleeDigest(callee)) {
                addIssue(tree, MESSAGE);
            }
        }
    }

    private static boolean isCalleeDigest(DotMemberExpressionTree callee) {
        return callee.object().is(Tree.Kind.IDENTIFIER_REFERENCE) &&
                AngularUtil.isObjectScope(((IdentifierTree) callee.object()).name())
                && "$digest".equals(callee.property().name());
    }

}
