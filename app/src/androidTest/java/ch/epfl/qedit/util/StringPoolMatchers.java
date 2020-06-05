package ch.epfl.qedit.util;

import ch.epfl.qedit.model.StringPool;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class StringPoolMatchers {
    public static Matcher<StringPool> containsPair(String key, String value) {
        return new TypeSafeMatcher<StringPool>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Check if the StringPool contains " + key + " -> " + value);
            }

            @Override
            protected boolean matchesSafely(StringPool item) {
                return item.get(key).equals(value);
            }
        };
    }

    public static Matcher<StringPool> containsLanguage(String languageCode) {
        return new TypeSafeMatcher<StringPool>() {
            @Override
            public void describeTo(Description description) {
                description.appendText(
                        "Check if the StringPool contains " + languageCode + " as language");
            }

            @Override
            protected boolean matchesSafely(StringPool item) {
                return item.getLanguageCode().equals(languageCode);
            }
        };
    }
}
