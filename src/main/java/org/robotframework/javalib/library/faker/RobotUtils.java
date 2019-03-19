package org.robotframework.javalib.library.faker;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfoList;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.replacePattern;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

public class RobotUtils {
    public static Map<String, MethodInfoList> getClassNonStaticMethods(final ClassInfo ci) {
        return getClassMethods(ci,mi -> mi.isPublic() && !mi.isStatic());
    }

    //    static Map<String, MethodInfoList> getClassNonStaticParameterlessMethods(final ClassInfo ci) {
    //        return getClassMethods(ci,mi -> mi.isPublic() && !mi.isStatic() && mi.getParameterInfo().length==0);
    //    }

    static private Map<String, MethodInfoList> getClassMethods(final ClassInfo ci, MethodInfoList.MethodInfoFilter predicate) {
        return Optional.ofNullable(ci)
            .map(_ci->_ci.getDeclaredMethodInfo()
                .filter(predicate)
                .asMap())
            .orElse(Collections.emptyMap());
    }

    //        static String normalize(String keywordName) {
    //            return keywordNameNormalizer.normalize(keywordName);
    //        }


    public static String splitBySpaces(String keywordName) {
        return join(
            splitByCharacterType(keywordName, true, true),
            " "
        );

    }

    /**
     * StringUtils.splitByCharacterType with digits glued to left loken
     * @param str the String to split, may be null
     * @param camelCase whether to use so-called "camel-case" for letter types
     * @param stickyDigits treat digit and previous letter as same type
     * @return an array of parsed Strings, {@code null} if null String input
     */
    static String[] splitByCharacterType(final String str, final boolean camelCase, final boolean stickyDigits) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        final char[] c = str.toCharArray();
        final List<String> list = new ArrayList<String>();
        int tokenStart = 0;
        int currentType = Character.getType(c[tokenStart]);
        for (int pos = tokenStart + 1; pos < c.length; pos++) {
            final int type = Character.getType(c[pos]);
            if (type == currentType) {
                continue;
            } else if(stickyDigits && type == Character.DECIMAL_DIGIT_NUMBER) {
                continue;
            }
            if (camelCase && type == Character.LOWERCASE_LETTER && currentType == Character.UPPERCASE_LETTER) {
                final int newTokenStart = pos - 1;
                if (newTokenStart != tokenStart) {
                    list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                    tokenStart = newTokenStart;
                }
            } else {
                list.add(new String(c, tokenStart, pos - tokenStart));
                tokenStart = pos;
            }
            currentType = type;
        }
        list.add(new String(c, tokenStart, c.length - tokenStart));
        return list.toArray(new String[0]);
    }

    public static boolean isVowel(char c){
        String vowels = "aeiouAEIOU";
        return vowels.indexOf(c) > -1;
    }

    public static String simplifyTypeName(String name) {
        return replacePattern(
            substringAfterLast(
                "." + name,
                "."
            ),
            "Type$",
            ""
        );
    }
}
