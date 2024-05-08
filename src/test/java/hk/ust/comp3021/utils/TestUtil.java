package hk.ust.comp3021.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtil {
    @SuppressWarnings("unchecked")
    public static void checkConsoleOutput(List<String> expecteds, List<String> actuals, boolean isOrdered) {
        actuals.removeIf(s -> !s.contains("Querying"));
        assertEquals(expecteds.size(), actuals.size());

        if (isOrdered) {
            for (int i = 0; i < expecteds.size(); i++) {
                Object expected = expecteds.get(i);
                Object actual = actuals.get(i);
                assertEquals(expected, actual);
            }
        } else {
            for (String item : expecteds) {
                assertEquals(Collections.frequency(expecteds, item), Collections.frequency(actuals, item));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void checkResults(List<Object> expecteds, List<Object> actuals, List<Object[]> commands) {
        for (int i = 0; i < expecteds.size(); i++) {
            Object expected = expecteds.get(i);
            Object actual = actuals.get(i);
            String queryName = (String) ((Object[]) commands.get(i))[2];

            if (queryName.equals("findClassesWithMain") || queryName.equals("findSuperClasses")) {
                assertEquals(expected, new HashSet<String>((List<String>) actual));
            } else {
                assertEquals(expected, actual);
            }
        }
    }
}
