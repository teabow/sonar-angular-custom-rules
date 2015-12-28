package org.sonar.javascript.angular.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thibaud.bourgeois on 28/12/2015.
 * Regex util  class.
 */
public class RegexUtil {

    /**
     * Checks if string match one of specified patterns
     * @param line the string to check
     * @param stringPatterns the patterns to match
     * @return true if pattern has been found
     */
    public static boolean hasPattern(String line, List<String> stringPatterns) {
        boolean result = false;
        for (String stringPattern : stringPatterns) {
            Pattern pattern = Pattern.compile(stringPattern);
            Matcher matcher = pattern.matcher(line);
            result |= matcher.find();
        }
        return result;
    }

    /**
     * Extracts the group that matches the specific pattern
     * @param line the string to inspect
     * @param groupPattern the group pattern to match
     * @param group the group number to extract
     * @return the extracted string
     */
    public static String getGroup(String line, String groupPattern, int group) {
        Pattern pattern = Pattern.compile(groupPattern);
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches() && matcher.groupCount() >= group) {
            return matcher.group(group);
        }
        return null;
    }

}
