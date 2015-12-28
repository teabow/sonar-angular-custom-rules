package org.sonar.javascript.angular.checks;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.javascript.angular.util.AngularUtil;
import org.sonar.javascript.angular.util.RegexUtil;
import org.sonar.javascript.tree.visitors.CharsetAwareVisitor;
import org.sonar.plugins.javascript.api.visitors.BaseTreeVisitor;
import org.sonar.plugins.javascript.api.visitors.TreeVisitorContext;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by thibaud.bourgeois on 28/12/2015.
 * Directive name check.
 */
@Rule(
        key = "NGR7",
        priority = Priority.MAJOR,
        name = "Directive name should be prefixed.",
        tags = {"convention"},
        description = "Directive name should be prefixed, eg : appVersion for <div app-version></div>"
)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("10min")
public class DirectiveNameCheck extends BaseTreeVisitor implements CharsetAwareVisitor {

    private static final String CAMEL_CASE_PATTERN = "([a-z]+[A-Z]+\\w+)+";
    private static final String DIRECTIVE_PATTERN = AngularUtil.PATTERN_START + AngularUtil.DIRECTIVE + AngularUtil.PATTERN_END;

    private static final String MESSAGE = "Directive name should be prefixed, eg : appVersion for <div app-version></div>";

    private Charset charset;

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void scanFile(TreeVisitorContext context) {

        super.scanFile(context);

        List<String> lines;
        File file = getContext().getFile();

        try {
            lines = Files.readLines(file, charset);

        } catch (IOException e) {
            String fileName = file.getName();
            throw new IllegalStateException("Unable to execute rule \"NGR7\" for file " + fileName, e);
        }

        if (lines != null) {
            int lineCount = 0;
            for (String line : lines) {
                if (RegexUtil.hasPattern(line, ImmutableList.of(DIRECTIVE_PATTERN))) {
                    String group = RegexUtil.getGroup(line, ".*?" + DIRECTIVE_PATTERN + "['\"](.*?)['\"],.*" , 1);
                    if (group != null && group.length() > 0
                            && !RegexUtil.hasPattern(group, ImmutableList.of(CAMEL_CASE_PATTERN))) {
                        reportIssue(lineCount + 1);
                    }
                }
                lineCount++;
            }
        }
    }

    private void reportIssue(int line) {
        getContext().addIssue(this, line, MESSAGE);
    }

}
