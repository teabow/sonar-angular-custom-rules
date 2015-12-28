package org.sonar.javascript.angular.util;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thibaud.bourgeois on 20/12/2015.
 * File util class.
 */
public class FileUtil {

    /**
     * Search for a string pattern in a specific file
     * @param file the file in which the pattern is searched
     * @param charset the file charset
     * @param patternString the string pattern to search
     * @return true if pattern found in file, false if not
     */
    public static boolean findPattern(File file, Charset charset, String patternString) {
        List<String> lines;

        try {
            lines = Files.readLines(file, charset);

        } catch (IOException e) {
            String fileName = file.getName();
            throw new IllegalStateException("Unable to execute rule \"S1451\" for file " + fileName, e);
        }

        if (lines == null) {
            return false;
        }
        else {
            boolean result = false;
            Pattern pattern = Pattern.compile(patternString);
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                result |= matcher.find();
            }
            return result;
        }
    }
}
