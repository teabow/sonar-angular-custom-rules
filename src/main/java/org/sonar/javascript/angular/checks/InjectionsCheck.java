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
 * Created by thibaud.bourgeois on 19/12/2015.
 * Injections check.
 */
@Rule(
    key = "NGR6",
    priority = Priority.BLOCKER,
    name = "Injections should be done with ngAnnotate or $inject property",
    tags = {"convention"},
    description = "Manage injections with ngAnnotate or $inject property."
)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("30min")
public class InjectionsCheck extends BaseTreeVisitor implements CharsetAwareVisitor {

    Charset charset;

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    private static final String $INJECT = ".$inject";
    private static final String NG_ANNOTATE = "@ngInject";

    private static final String MESSAGE = "Injections should be done with ngAnnotate"
        + " or the $inject property.";

    private static final List<String> ANGULAR_PATTERNS = ImmutableList.of(
            AngularUtil.PATTERN_START + AngularUtil.CONFIG + AngularUtil.PATTERN_END,
            AngularUtil.PATTERN_START + AngularUtil.CONTROLLER + AngularUtil.PATTERN_END,
            AngularUtil.PATTERN_START + AngularUtil.CONSTANT + AngularUtil.PATTERN_END,
            AngularUtil.PATTERN_START + AngularUtil.DIRECTIVE + AngularUtil.PATTERN_END,
            AngularUtil.PATTERN_START + AngularUtil.FACTORY + AngularUtil.PATTERN_END,
            AngularUtil.PATTERN_START + AngularUtil.FILTER + AngularUtil.PATTERN_END,
            AngularUtil.PATTERN_START + AngularUtil.PROVIDER + AngularUtil.PATTERN_END,
            AngularUtil.PATTERN_START + AngularUtil.SERVICE + AngularUtil.PATTERN_END,
            AngularUtil.PATTERN_START + AngularUtil.VALUE + AngularUtil.PATTERN_END
    );
    private static final List<String> INJECT_PATTERNS = ImmutableList.of(
            $INJECT,
            NG_ANNOTATE
    );

    @Override
    public void scanFile(TreeVisitorContext context) {

        super.scanFile(context);

        List<String> lines;
        File file = getContext().getFile();

        try {
            lines = Files.readLines(file, charset);

        } catch (IOException e) {
            String fileName = file.getName();
            throw new IllegalStateException("Unable to execute rule \"NGR6\" for file " + fileName, e);
        }

        if (lines != null) {
            int lineCount = 0;
            boolean hasInject = false, isComponent = false;
            String functionPattern = ".*?function\\((.*?)\\).*";
            for (String line : lines) {
                if (RegexUtil.hasPattern(line, INJECT_PATTERNS)) {
                    hasInject = true;
                }
                if (RegexUtil.hasPattern(line, ANGULAR_PATTERNS)) {
                    isComponent = true;
                }
                if (isComponent && !hasInject) {
                    String group = RegexUtil.getGroup(line, functionPattern , 1);
                    if (group != null && group.length() > 0) {
                        reportIssue(lineCount + 1);
                    }
                    hasInject = false;
                    isComponent = false;
                }
                lineCount++;
            }
        }
    }

    private void reportIssue(int line) {
        getContext().addIssue(this, line, MESSAGE);
    }

}
