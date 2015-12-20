package org.sonar.javascript.angular.checks;

import com.google.common.collect.ImmutableSet;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.javascript.angular.util.AngularUtil;
import org.sonar.javascript.tree.visitors.CharsetAwareVisitor;
import org.sonar.plugins.javascript.api.tree.expression.*;
import org.sonar.plugins.javascript.api.visitors.BaseTreeVisitor;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import java.nio.charset.Charset;
import java.util.Set;

/**
 * Created by thibaud.bourgeois on 19/12/2015.
 * Jquery use check.
 */
@Rule(
    key = "NGR1",
    priority = Priority.CRITICAL,
    name = "JQuery in controller should not be used.",
    tags = {"convention"},
    description = "Create a directive instead."
)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("10min")
public class JqueryUseCheck extends BaseTreeVisitor implements CharsetAwareVisitor {

    private static final String $ = "$";
    private static final String JQUERY = "jQuery";
    private static final String ZEPTO = "Zepto";

    Charset charset;
    private static final Set<String> FORBIDDEN_FUNCTIONS =
            ImmutableSet.of($, JQUERY, ZEPTO);

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void visitCallExpression(CallExpressionTree tree) {
        ExpressionTree callee = tree.callee();

        if (callee instanceof IdentifierTree
                && AngularUtil.isController(getContext().getFile(), charset)
                && FORBIDDEN_FUNCTIONS.contains(((IdentifierTree) callee).name())) {
            getContext().addIssue(this, tree, "Remove jQuery call from controller.");
        }

        // super method must be called in order to visit what is under the function node in the
        super.visitCallExpression(tree);
    }

}
