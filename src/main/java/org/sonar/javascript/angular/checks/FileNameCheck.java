package org.sonar.javascript.angular.checks;

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
import java.nio.charset.Charset;

/**
 * Created by thibaud.bourgeois on 21/12/2015.
 * File name check.
 */
@Rule(
        key = "NGR3",
        priority = Priority.MAJOR,
        name = "Files names should explicitly mention the component type.",
        tags = {"convention"},
        description = "Rename the file to comply with naming convention."
)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.DATA_RELIABILITY)
@SqaleConstantRemediation("10min")
public class FileNameCheck extends BaseTreeVisitor implements CharsetAwareVisitor {

    Charset charset;

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void scanFile(TreeVisitorContext context) {

        File file = context.getFile();

        String fileName = file.getName();
        if (AngularUtil.isController(file, charset)
            && !fileName.contains(AngularUtil.CONTROLLER)) {
            context.addFileIssue(this, "Files names should explicitly mention the component type (eg : user-controller.js");
        }
        else if (AngularUtil.isDirective(file, charset)
                && !fileName.contains(AngularUtil.DIRECTIVE)) {
            context.addFileIssue(this, "Files names should explicitly mention the component type (eg : user-directive.js");
        }
        else if (AngularUtil.isFactory(file, charset)
                && !fileName.contains(AngularUtil.FACTORY)) {
            context.addFileIssue(this, "Files names should explicitly mention the component type (eg : user-factory.js");
        }
        else if (AngularUtil.isService(file, charset)
                && !fileName.contains(AngularUtil.SERVICE)) {
            context.addFileIssue(this, "Files names should explicitly mention the component type (eg : user-service.js");
        }
        else if (AngularUtil.isProvider(file, charset)
                && !fileName.contains(AngularUtil.PROVIDER)) {
            context.addFileIssue(this, "Files names should explicitly mention the component type (eg : user-provider.js");
        }

    }
}
