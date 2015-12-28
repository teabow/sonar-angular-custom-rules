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
 * Controller name check.
 */
@Rule(
        key = "NGR8",
        priority = Priority.MAJOR,
        name = "Controller name should be suffixed.",
        tags = {"convention"},
        description = "Controller name should be suffixed, eg : HomeCtrl or HomeController"
)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("10min")
public class ControllerNameCheck extends BaseTreeVisitor implements CharsetAwareVisitor {

    private static final String CONTROLLER_PATTERN = AngularUtil.PATTERN_START + AngularUtil.CONTROLLER + AngularUtil.PATTERN_END;

    private static final String MESSAGE = "Controller name should be suffixed, eg : HomeCtrl or HomeController";

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
            throw new IllegalStateException("Unable to execute rule \"NGR8\" for file " + fileName, e);
        }

        if (lines != null) {
            int lineCount = 0;
            for (String line : lines) {
                if (RegexUtil.hasPattern(line, ImmutableList.of(CONTROLLER_PATTERN))) {
                    String group = RegexUtil.getGroup(line, ".*?" + CONTROLLER_PATTERN + "['\"](.*?)['\"],.*" , 1);
                    if (group != null && !isControllerNameValid(group)) {
                        reportIssue(lineCount + 1);
                    }
                }
                lineCount++;
            }
        }
    }

    /**
     * Checks if controller name is valid
     * @param name the controller name
     * @return true if name is valid
     */
    private boolean isControllerNameValid(String name) {
        return name.endsWith("Ctrl") || name.endsWith("Controller");
    }

    private void reportIssue(int line) {
        getContext().addIssue(this, line, MESSAGE);
    }

}
