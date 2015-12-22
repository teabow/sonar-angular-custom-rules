package org.sonar.javascript.angular.checks;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.javascript.angular.util.AngularUtil;
import org.sonar.javascript.tree.visitors.CharsetAwareVisitor;
import org.sonar.plugins.javascript.api.tree.Tree;
import org.sonar.plugins.javascript.api.tree.expression.LiteralTree;
import org.sonar.plugins.javascript.api.visitors.BaseTreeVisitor;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import java.io.File;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thibaud.bourgeois on 19/12/2015.
 * HTML use check.
 */
@Rule(
    key = "NGR2",
    priority = Priority.CRITICAL,
    name = "HTML in controller should not be used.",
    tags = {"convention"},
    description = "Create a directive or template instead."
)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("10min")
public class HtmlUseCheck extends BaseTreeVisitor implements CharsetAwareVisitor {

    Charset charset;
    Pattern htmlPattern = Pattern.compile("<(\"[^\"]*\"|'[^']*'|[^'\">])*>");

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void visitLiteral(LiteralTree tree) {

        if (tree.is(Tree.Kind.STRING_LITERAL)
                && isForbidden(getContext().getFile())) {
            Matcher matcher = htmlPattern.matcher(tree.value());
            if (matcher.find()) {
                getContext().addIssue(this, tree, "Remove HTML templating in controller, create a directive or a template instead");
            }
        }
        super.visitLiteral(tree);
    }

    private boolean isForbidden(File file) {
        return AngularUtil.isController(file, charset)
                || AngularUtil.isFactory(file, charset)
                || AngularUtil.isProvider(file, charset)
                || AngularUtil.isService(file, charset);
    }

}
