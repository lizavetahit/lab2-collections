package ua.kpi.comsys.collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/** Частотний аналізатор тексту. */
    public class WordCounter {

    private static final Pattern NON_WORD = Pattern.compile("[^A-Za-z0-9]+");

    private static final String NULL_MESSAGE = "textLines cannot be null";

    /** Підраховує кількість входжень кожного слова. */
    public Map<String, Integer> countWords(List<String> textLines) {
        validate(textLines);

        Map<String, Integer> counts = new HashMap<>();
        for (String line : textLines) {
            if (line == null) {
                continue;
            }
            for (String token : NON_WORD.split(line)) {
                if (token.isEmpty()) {
                    continue;
                }
                counts.merge(token.toLowerCase(Locale.ROOT), 1, Integer::sum);
            }
        }
        return counts;
    }

    /** Повертає множину всіх унікальних слів. */
    public Set<String> getUniqueWords(List<String> textLines) {
        validate(textLines);
        return new HashSet<>(countWords(textLines).keySet());
    }

    private static void validate(List<String> textLines) {
        if (textLines == null) {
            throw new IllegalArgumentException(NULL_MESSAGE);
        }
    }
}
