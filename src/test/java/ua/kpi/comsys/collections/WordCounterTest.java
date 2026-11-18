package ua.kpi.comsys.collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordCounterTest {

    private WordCounter counter;

    @BeforeEach
    void setUp() {
        counter = new WordCounter();
    }

    @Nested
    @DisplayName("countWords")
    class CountWords {

        @Test
        @DisplayName("null -> IllegalArgumentException")
        void throwsOnNull() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> counter.countWords(null));
            assertEquals("textLines cannot be null", ex.getMessage());
        }

        @Test
        @DisplayName("порожній список -> порожня Map")
        void emptyList() {
            assertTrue(counter.countWords(Collections.emptyList()).isEmpty());
        }

        @Test
        @DisplayName("рахує повторення слів")
        void countsSimpleWords() {
            Map<String, Integer> result = counter.countWords(List.of("apple banana apple", "banana apple"));

            assertEquals(2, result.size());
            assertEquals(3, result.get("apple"));
            assertEquals(2, result.get("banana"));
        }

        @Test
        @DisplayName("ігнорує регістр")
        void caseInsensitive() {
            Map<String, Integer> result = counter.countWords(List.of("Java JAVA java JaVa"));

            assertEquals(Map.of("java", 4), result);
        }

        @Test
        @DisplayName("пунктуація та пробіли є роздільниками")
        void punctuationIsSeparator() {
            Map<String, Integer> result = counter.countWords(
                    List.of("Hello, world! Hello...world?", "  hello\tworld\n"));

            assertEquals(Map.of("hello", 3, "world", 3), result);
        }

        @Test
        @DisplayName("цифри є частиною слова")
        void digitsArePartOfWord() {
            Map<String, Integer> result = counter.countWords(List.of("java21 Java21 2025 lab2"));

            assertEquals(Map.of("java21", 2, "2025", 1, "lab2", 1), result);
        }

        @Test
        @DisplayName("дефіс, апостроф і підкреслення розбивають слово")
        void specialCharsSplitWord() {
            Map<String, Integer> result = counter.countWords(List.of("e-mail don't snake_case"));

            assertEquals(Map.of("e", 1, "mail", 1, "don", 1, "t", 1, "snake", 1, "case", 1), result);
        }

        @Test
        @DisplayName("нелатинські символи не входять у слова")
        void nonLatinIgnored() {
            Map<String, Integer> result = counter.countWords(List.of("Привіт hello світ café"));

            assertEquals(Map.of("hello", 1, "caf", 1), result);
        }

        @Test
        @DisplayName("рядки без слів дають порожній результат")
        void blankAndSymbolOnlyLines() {
            Map<String, Integer> result = counter.countWords(List.of("", "   ", "!!! ... ---", "Привіт"));

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("null-рядки всередині списку пропускаються")
        void nullLineSkipped() {
            List<String> lines = Arrays.asList("one", null, "one two");

            assertEquals(Map.of("one", 2, "two", 1), counter.countWords(lines));
        }

        @Test
        @DisplayName("не змінює вхідний список")
        void doesNotModifyInput() {
            List<String> lines = new ArrayList<>(List.of("A b", "c"));
            counter.countWords(lines);

            assertEquals(List.of("A b", "c"), lines);
        }
    }

    @Nested
    @DisplayName("getUniqueWords")
    class GetUniqueWords {

        @Test
        @DisplayName("null -> IllegalArgumentException")
        void throwsOnNull() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> counter.getUniqueWords(null));
            assertEquals("textLines cannot be null", ex.getMessage());
        }

        @Test
        @DisplayName("порожній список -> порожній Set")
        void emptyList() {
            assertTrue(counter.getUniqueWords(List.of()).isEmpty());
        }

        @Test
        @DisplayName("повертає унікальні слова у нижньому регістрі")
        void uniqueLowercase() {
            Set<String> result = counter.getUniqueWords(List.of("The cat and THE dog", "Cat, dog; cat!"));

            assertEquals(Set.of("the", "cat", "and", "dog"), result);
        }

        @Test
        @DisplayName("порожні слова не потрапляють у результат")
        void noEmptyWords() {
            Set<String> result = counter.getUniqueWords(List.of(",,a,,b,,", "  "));

            assertEquals(Set.of("a", "b"), result);
            assertFalse(result.contains(""));
        }

        @Test
        @DisplayName("null-рядки всередині списку пропускаються")
        void nullLineSkipped() {
            assertEquals(Set.of("x"), counter.getUniqueWords(Arrays.asList(null, "x X")));
        }

        @Test
        @DisplayName("узгоджено з ключами countWords")
        void consistentWithCountWords() {
            List<String> lines = List.of("To be, or not to be: that is the question.");

            assertEquals(counter.countWords(lines).keySet(), counter.getUniqueWords(lines));
        }
    }
}
