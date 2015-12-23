package org.sonar.javascript.angular.checks;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.javascript.angular.util.AngularUtil;
import org.sonar.javascript.tree.visitors.CharsetAwareVisitor;
import org.sonar.plugins.javascript.api.visitors.BaseTreeVisitor;
import org.sonar.plugins.javascript.api.visitors.TreeVisitorContext;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thibaud.bourgeois on 19/12/2015.
 * Injections check.
 */
@Rule(
    key = "NGR6",
    priority = Priority.BLOCKER,
    name = "Injections should be done with ngAnnotate or $inject",
    tags = {"convention"},
    description = "ngAnnotate or $inject should be used to manage injections."
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

    private static final String MESSAGE = "Injections should be done with ngAnnotate or $inject.";

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
            boolean hasInject = false;
            for (String line : lines) {
                if (findPattern(line, INJECT_PATTERNS)) {
                    hasInject = true;
                }
                if (findPattern(line, ANGULAR_PATTERNS) && !hasInject) {
                    // TODO : don't raise exception if no args in function
                    reportIssue(lineCount + 1);
                    hasInject = false;
                }
                lineCount++;
            }
        }
    }

    private boolean findPattern(String line, List<String> stringPatterns) {
        boolean result = false;
        for (String stringPattern : stringPatterns) {
            Pattern pattern = Pattern.compile(stringPattern);
            Matcher matcher = pattern.matcher(line);
            result |= matcher.find();
        }
        return result;
    }

    private void reportIssue(int line) {
        getContext().addIssue(this, line, MESSAGE);
    }

}
